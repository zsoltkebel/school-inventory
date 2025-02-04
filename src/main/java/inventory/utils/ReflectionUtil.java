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

    /**
     * Returns all instance variables (fields) of a class including those that are inherited
     *
     * @param from   The class whose fields are going to be returned
     * @param until  Inherited fields are retrieved until this superclass is reached
     * @param except Names of the fields that are excluded from the return list
     * @return Fields of the class
     */
    public static List<Field> getFields(Class<?> from, Class<?> until, String... except) {
        // return an empty list if the until class is not a superclass of from
        if (!until.isAssignableFrom(from)) return new ArrayList<>();

        List<Field> fields = new ArrayList<>();

        if (!from.equals(until)) {
            // recursively call this function on the superclass
            fields.addAll(getFields(from.getSuperclass(), until));
        }
        // retrieve fields of the class
        fields.addAll(Arrays.asList(from.getDeclaredFields()));

        // remove those that are excluded
        fields.removeIf(field -> Arrays.asList(except).contains(field.getName()));
        fields.forEach(field -> field.setAccessible(true));
        return fields;
    }
}
