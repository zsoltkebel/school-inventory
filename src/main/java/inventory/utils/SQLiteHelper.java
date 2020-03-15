package inventory.utils;

import com.sun.xml.internal.ws.util.StringUtils;
import inventory.model.Record;
import javafx.beans.property.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import static inventory.utils.ReflectionUtil.getFields;

public class SQLiteHelper {

    static <T extends Record<T>> String getInitTableCommand(String tableName, Class<T> forClass) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(";

        Field[] fields = getFields(forClass).toArray(new Field[0]);

        sql += Arrays.stream(fields)
                .map(field -> field.getName() + " " + getSQLiteDatatypeForClass(field.getType())
                        + (field.getName().equals("id") ? " PRIMARY KEY" : "")) // if the field is the id
                .collect(Collectors.joining(","));

        sql += ");";

        return sql;
    }

    static <T extends Record<T>> String getInsertCommand(String tableName, T record) {
        String[] columns = getFields(record.getClass()).stream()
                .map(Field::getName)
                .filter(s -> !s.equals("id"))
                .toArray(String[]::new);

        return "INSERT INTO "
                + tableName + "(" + String.join(",", columns) + ") VALUES("
                + String.join(",", Collections.nCopies(columns.length, "?")) + ")";
    }

    static <T extends Record<T>> String getUpdateCommand(String tableName, T record) {
        String[] columns = getFields(record.getClass(), "id").stream()
                .map(Field::getName)
                .toArray(String[]::new);

        return "UPDATE " + tableName + " SET "
                + Arrays.stream(columns).map(column -> column + " = ?").collect(Collectors.joining(","))
                + " WHERE id = " + record.getId();
    }

    static String getSQLiteDatatypeForClass(Class<?> type) {
        List<Class<?>> integers = Arrays.asList(
                int.class, Integer.class, IntegerProperty.class,
                boolean.class, Boolean.class, BooleanProperty.class,
                Date.class, ObjectProperty.class);
        List<Class<?>> texts = Arrays.asList(String.class, StringProperty.class, ListProperty.class);

        if (integers.contains(type)) {
            return "INTEGER";
        } else if (texts.contains(type)) {
            return "TEXT";
        } else {
            return "text to result in SQL error";
        }
    }

    static String getDropTableCommand(String tableName) {
        return "DROP TABLE " + tableName;
    }

    public static String getSQLTypeString(Class<?> forClass) {
        return StringUtils.capitalize(getSQLType(forClass).getSimpleName());
    }

    public static Class<?> getSQLType(Class<?> type) {
        Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>() {{
            put(int.class, int.class);
            put(IntegerProperty.class, int.class);
            put(StringProperty.class, String.class);
            put(BooleanProperty.class, boolean.class);
            put(ObjectProperty.class, Date.class);
            put(Date.class, Date.class);
            put(String.class, String.class);
            put(boolean.class, boolean.class);
        }};

        for (Map.Entry<Class<?>, Class<?>> entry : map.entrySet()) {
            if (entry.getKey().equals(type)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static Method statementSetterMethod(Field field) {
        String methodName = "set" + getSQLTypeString(field.getType());

        try {
            return PreparedStatement.class.getMethod(methodName, int.class, getSQLType(field.getType()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
