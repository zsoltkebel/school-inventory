package inventory.utils;

import com.sun.xml.internal.ws.util.StringUtils;
import inventory.model.Category;
import inventory.model.Item;
import inventory.model.Record;
import inventory.model.Reservation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.*;
import java.util.*;

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

    public void remove(int id, String table) {
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
            Field[] fields = record.getClass().getFields();

            // id is the last one and we dont need that
            for (int i = 0; i < fields.length - 1; i++) {
                Field field = fields[i];
                Method setter = setterMethod(field);
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
        String sql = SQLiteHelper.getUpdateCommand(tableName, record.getClass());

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            Field[] fields = record.getClass().getFields();

            // id is the last one and we need that
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Method setter = setterMethod(field);
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
     *
     * @param type
     * @param resultSet
     * @param <T>
     * @return
     */
    public <T extends Record<T>> T constructRecord(Class<T> type, ResultSet resultSet) {
        List<Field> fields = new ArrayList<>(Arrays.asList(type.getFields()));
        List<Object> params = new ArrayList<>();

        for (Field field : fields) {
            String methodName = "get" + StringUtils.capitalize(getPrimitiveType(field.getType().getName()));

            try {
                Method method = ResultSet.class.getMethod(methodName, String.class);

                params.add(method.invoke(resultSet, field.getName()));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        fields.add(0, fields.remove(fields.size() - 1)); // move id type to  first
        params.add(0, params.remove(params.size() - 1)); // move id to first

        try {
            Constructor<T> constructor = type.getConstructor(fields.stream()
                    .map(field -> getPrimitiveType(field.getType())).toArray(Class[]::new)
            );
            return constructor.newInstance(params.toArray());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends Record<T>> List<T> queryAll(Class<T> type, String tableName) {
        List<T> records = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName;

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

    private String getPrimitiveType(String type) {
        String capitalized = StringUtils.capitalize(type);
        List<String> types = Arrays.asList("Int", "Boolean", "String", "Long", "Date");

        for (String s : types) {
            if (capitalized.contains(s)) return s;
        }

        return null;
    }

    private Class<?> getPrimitiveType(Class<?> type) {
        List<Class<?>> types = Arrays.asList(int.class, String.class, boolean.class, Date.class);

        for (Class<?> c : types) {
            if (type.getSimpleName().contains(c.getSimpleName())
                    || type.getSimpleName().contains(StringUtils.capitalize(c.getSimpleName()))) return c;
        }

        return null;
    }

    private Method getterMethod(Field field) {
        String methodName = (Objects.equals(getPrimitiveType(field.getType()), boolean.class)
                ? "is"
                : "get")
                + StringUtils.capitalize(field.getName());

        try {
            return field.getDeclaringClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Method setterMethod(Field field) {
        String methodName = "set" + getPrimitiveType(field.getType().getName());

        try {
            return PreparedStatement.class.getMethod(methodName, int.class, getPrimitiveType(field.getType()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String insertCommand(String table, String... columns) {
        return "INSERT INTO "
                + table + "(" + String.join(",", columns) + ") VALUES("
                + String.join(",", Collections.nCopies(columns.length, "?")) + ")";
    }


}
