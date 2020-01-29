package inventory.utils;

import inventory.model.Record;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    static <T extends Record<T>> String getInsertCommand(String tableName, Class<T> forClass) {
        String[] columns = getFields(forClass).stream()
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
                Date.class);
        List<Class<?>> texts = Arrays.asList(String.class, StringProperty.class);

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


}
