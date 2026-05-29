package org.javalabs.decl.util;

/**
 * Utility class for data type handling.
 *
 * @author Sudiptasish Chanda
 */
public class DtypeUtil {
    
    /**
     * Checks if the specified class type represents a primitive type or a primitive wrapper.
     * 
     * <p>This method evaluates a given {@link java.lang.Class} type. It returns {@code true} 
     * if the type is a core Java primitive (like {@code int} or {@code boolean}) or if it is 
     * one of the standard boxed primitive wrapper objects (like {@link java.lang.Integer} 
     * or {@link java.lang.Boolean}).</p>
     * 
     * <p>Example evaluation checks:</p>
     * <ul>
     *   <li>{@code int.class} returns {@code true}</li>
     *   <li>{@code Integer.class} returns {@code true}</li>
     *   <li>{@code String.class} returns {@code false}</li>
     *   <li>{@code null} returns {@code false}</li>
     * </ul>
     *
     * @param type the class type to inspect, may be null
     * @return Boolean {@code true} if the type is a primitive or wrapper; {@code false} otherwise
     * 
     * @see java.lang.Class#isPrimitive()
     */ 
    public static Boolean isPrimitive(Class<?> type) {
        return type == Byte.class || type == byte.class
                || type == Short.class || type == short.class
                || type == Integer.class || type == int.class
                || type == Long.class || type == long.class
                || type == Float.class || type == float.class
                || type == Double.class || type == double.class
                || type == Boolean.class || type == boolean.class
                || type == String.class
                || type == Character.class || type == char.class
                || type == byte[].class
                || type == short[].class
                || type == int[].class
                || type == long[].class
                || type == float[].class
                || type == double[].class
                || type == String[].class;
    }
}
