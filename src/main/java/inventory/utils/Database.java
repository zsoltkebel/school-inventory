package inventory.utils;

import com.sun.xml.internal.ws.util.StringUtils;
import inventory.model.Category;
import inventory.model.Item;
import inventory.model.Record;
import inventory.model.Reservation;
import javafx.beans.property.ListProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static inventory.utils.ReflectionUtil.getFields;
import static inventory.utils.SQLiteHelper.getSQLType;
import static inventory.utils.SQLiteHelper.statementSetterMethod;

public class Database {

    // categories ===============================
    public static final String TABLE_CATEGORIES = "categories";

    // items ====================================
    public static final String TABLE_ITEMS = "items";

    // reservations =============================
    public static final String TABLE_RESERVATIONS = "reservations";

    private static Database SINGLE_DATABASE = new Database();

    public static Database getInstance() {
        return SINGLE_DATABASE;
    }

    private Connection conn;

    private Database() {
        getConnection();
    }

    private void getConnection() {
        try {
            String DB_URL = "jdbc:sqlite:Database.db";

            conn = DriverManager.getConnection(DB_URL);
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
//        dropAllTable();
        try {
            Statement statement = conn.createStatement();
            statement.execute(SQLiteHelper.getInitTableCommand(TABLE_CATEGORIES, Category.class));
            statement.execute(SQLiteHelper.getInitTableCommand(TABLE_ITEMS, Item.class));
            statement.execute(SQLiteHelper.getInitTableCommand(TABLE_RESERVATIONS, Reservation.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropAllTable() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(SQLiteHelper.getDropTableCommand(TABLE_CATEGORIES));
            statement.execute(SQLiteHelper.getDropTableCommand(TABLE_ITEMS));
            statement.execute(SQLiteHelper.getDropTableCommand(TABLE_RESERVATIONS));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id, String table) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserts an Record object into the specified table in the database
     *
     * @param tableName The name of the table storing the records of this type
     * @param record    The object that is to be inserted into the database
     * @param <T>       The type of the object that is to be inserted into the database
     * @return the original object with the correct id after insertion
     */
    public <T extends Record<T>> T insert(String tableName, T record) {
        try {
            // retrieving the SQL command corresponding to the record that is to be inserted
            String sql = SQLiteHelper.getInsertCommand(tableName, record);

            PreparedStatement statement = updateFields(sql, record);

            // making sure that the statement executed
            assert statement != null;
            // retrieve the id of the newly inserted record if the statement was executed
            int id = statement.getGeneratedKeys().getInt(1);

            // return the record with the actual id that was inserted
            return record.withId(id);
        } catch (SQLException e) {
            // if an error occurred return null
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates a Record object in the specific table in the database
     *
     * @param tableName The name of the table storing the records of this type
     * @param record    The object that is to be updated in the database
     * @param <T>       The type of the object that is to be updated in the database
     * @return the original object
     */
    public <T extends Record<T>> T update(String tableName, T record) {
        // retrieving the SQL command corresponding to the record that is to be updated
        String sql = SQLiteHelper.getUpdateCommand(tableName, record);

        PreparedStatement statement = updateFields(sql, record);

        // making sure that the statement executed
        assert statement != null;
        // return the record
        return record;
    }

    /**
     * Sets the not specified parameters of an sql command and creates a prepared statement
     *
     * @param sql    The sql command without specified parameters
     * @param record The object whose fields are to be set in the prepared statement
     * @param <T>    The type of the object
     * @return the papered statement that was already executed or null if an error occurred
     */
    private <T extends Record<T>> PreparedStatement updateFields(String sql, T record) {
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            // retrieving the fields of the class that is to be inserted
            Field[] fields = getFields(record.getClass(), "id").toArray(new Field[0]);

            // for each field retrieve the value with the appropriate getter and
            // set the corresponding parameter in the prepared statement
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Method setter = statementSetterMethod(field);
                Method getter = getterMethod(field);

                if (setter != null && getter != null) {
                    setter.invoke(statement, i + 1, getter.invoke(record));
                }
            }

            statement.executeUpdate();

            return statement;
        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param <T>
     * @param type
     * @param resultSet
     * @return
     */
    public <T extends Record<?>> T constructRecord(Class<T> type, ResultSet resultSet) {
        try {
            Method method = Factory.class.getMethod("new" + type.getSimpleName(), ResultSet.class);

            Object newInstance = method.invoke(Factory.class, resultSet);
            if (type.isInstance(newInstance)) {
                return (T) newInstance;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public <T extends Record<T>> List<T> queryAll(Class<T> type, String tableName, boolean descending, Integer limit) {
        List<T> records = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName + " ORDER BY id " + (descending ? "DESC" : "ASC");
        if (limit != null) sql += " LIMIT " + limit;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                records.add(constructRecord(type, rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return records;
    }

    public List<Reservation> queryAll(LocalDate from, LocalDate to, boolean descending, Integer limit) {
        List<Reservation> records = new ArrayList<>();

        Instant instantFrom = from.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant instantTo = to.atStartOfDay(ZoneId.systemDefault()).toInstant();
        String sql = "SELECT * FROM " + TABLE_RESERVATIONS
                + " WHERE date BETWEEN " + instantFrom.toEpochMilli() + " AND " + instantTo.toEpochMilli()
                + " ORDER BY date " + (descending ? "DESC" : "ASC");

        if (limit != null) sql += " LIMIT " + limit;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                records.add(Factory.newReservation(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return records;
    }

    private String insertCommand(String table, String... columns) {
        return "INSERT INTO "
                + table + "(" + String.join(",", columns) + ") VALUES("
                + String.join(",", Collections.nCopies(columns.length, "?")) + ")";
    }

    private static Method getterMethod(Field field) {
        String methodName = (Objects.equals(getSQLType(field.getType()), boolean.class)
                ? "is" // start of the getter method field is boolean
                : "get") // start of the getter method otherwise
                + StringUtils.capitalize(field.getName()); // field name capitalized

        if (Objects.equals(getSQLType(field.getType()), Date.class)) {
            methodName += "SQL"; // if it field has a dedicated SQL class for it e.g. java.sql.Date
        }

        try {
            return field.getDeclaringClass().getMethod(methodName); // return the method of the class
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

}
