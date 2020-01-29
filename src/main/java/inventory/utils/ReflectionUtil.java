package inventory.utils;

import inventory.model.Record;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

    public static <T extends Record<?>> List<Field> getFields(Class<T> type, String... except) {
        return getFields(type, Record.class, except);
    }

    public static List<Field> getFields(Class<?> from, Class<?> until, String... except) {
        if (!until.isAssignableFrom(from)) return new ArrayList<>();

        List<Field> fields = new ArrayList<>();

        if (!from.equals(until)) {
            fields.addAll(getFields(from.getSuperclass(), until));
        }
        fields.addAll(Arrays.asList(from.getDeclaredFields()));

        fields.removeIf(field -> Arrays.asList(except).contains(field.getName()));
        fields.forEach(field -> field.setAccessible(true));
        return fields;
    }
}
