package inventory.utils;

import com.sun.xml.internal.ws.util.StringUtils;
import inventory.model.Category;
import inventory.model.Item;
import inventory.model.Record;
import inventory.model.Reservation;
import javafx.beans.property.ListProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static inventory.utils.ReflectionUtil.getFields;
import static inventory.utils.SQLiteHelper.*;

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

    private final String DB_URL = "jdbc:sqlite:Database.db";

    private Database() {
        getConnection();
    }

    private void getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            initialize();
        } catch (SQLException | ClassNotFoundException e) {
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

    public <T extends Record<T>> T insert(String tableName, T record) {
        String sql = SQLiteHelper.getInsertCommand(tableName, record.getClass());

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            Field[] fields = (Field[]) getFields(record.getClass(), "id").toArray(new Field[0]);;

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Method setter = statementSetterMethod(field);
                Method getter = getterMethod(field);

                try {
                    setter.invoke(statement, i + 1, getter.invoke(record));
                } catch (NullPointerException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            statement.executeUpdate();

            int id = statement.getGeneratedKeys().getInt(1);

            return record.withId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends Record<T>> T update(String tableName, T record) {
        String sql = SQLiteHelper.getUpdateCommand(tableName, record);

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            Field[] fields = getFields(record.getClass(), "id").toArray(new Field[0]);

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Method setter = statementSetterMethod(field);
                Method getter = getterMethod(field);

                try {
                    assert setter != null;
                    assert getter != null;
                    setter.invoke(statement, i + 1, getter.invoke(record));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            statement.executeUpdate();
            int id = record.getId();

            return record.withId(id);
        } catch (SQLException e) {
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

    private String insertCommand(String table, String... columns) {
        return "INSERT INTO "
                + table + "(" + String.join(",", columns) + ") VALUES("
                + String.join(",", Collections.nCopies(columns.length, "?")) + ")";
    }

    private static Method getterMethod(Field field) {
        String methodName = (Objects.equals(getSQLType(field.getType()), boolean.class)
                ? "is"
                : "get")
                + StringUtils.capitalize(field.getName());

        //ending
        if (Objects.equals(getSQLType(field.getType()), Date.class)) {
            methodName += "SQL";
        } else if (Objects.equals(field.getType(), ListProperty.class)) {
            methodName += "String";
        }

        try {
            return field.getDeclaringClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

}
