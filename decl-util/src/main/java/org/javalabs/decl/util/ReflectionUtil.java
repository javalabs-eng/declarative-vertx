package org.javalabs.decl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Utility class that makes use of {@link java.lang.reflect} package to invoke methods of an object.
 *
 * @author Sudiptasish Chanda
 */
public final class ReflectionUtil {

    private static final Map<Class<?>, ReflectiveClass> MAPPING = new IdentityHashMap<>();

    public static Class<?> getElementType(Class<?> clazz, Integer order) {
        Type genericSuperclass = clazz.getGenericSuperclass();

        if (! (genericSuperclass instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Cache does not have parameterized superclass: "+ clazz.getName());
        }
        ParameterizedType parameterizedType = (ParameterizedType)genericSuperclass;

        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Type valueType = typeArguments[order];

        if (valueType instanceof Class<?>) {
            return (Class<?>)valueType;
        }
        throw new IllegalArgumentException("Unable to determine cached element type for "+ clazz.getName());
    }

    /**
     * Invoke the no-arg static method of the given class.
     *
     * @param clazz Class whose static method to be invoked.
     * @param method Method name
     * @return Object Return value of the method invocation.
     */
    public static Object invokeStatic(Class<?> clazz, String method) {
        return invokeStatic(clazz, method, new Object[0]);
    }

    /**
     * Invoke the no-arg static method of the given class.
     *
     * @param clazz Class whose static method to be invoked.
     * @param method Method name
     * @param args Set of method arguments
     * @return Object Return value of the method invocation.
     */
    public static Object invokeStatic(Class<?> clazz, String method, Object[] args) {
        try {
            Class<?>[] clazzez = new Class[0];
            if (args == null) {
                args = new Object[0];
            }
            if (args != null && args.length > 0) {
                clazzez = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    clazzez[i] = args[i].getClass();
                }
            }

            Method m = clazz.getDeclaredMethod(method, clazzez);
            return m.invoke(null, args);
        } catch (Exception e) {
            // Do Nothing
        }
        return null;
    }

    /**
     * Invoke the getter method on the object passed as a method argument.
     *
     * <p>
     * A getter method of an attribute <code>foo</code> will be <code>getFoo()</code>. Such method does not take any
     * method argument, hence the {@link Method#invoke(java.lang.Object, java.lang.Object...) }
     * is called with an empty arg. If no associated getter method found, then this api will return null.
     *
     * @param obj Object whose getter method will be invoked.
     * @param attrName Attribute name, whose associated getter method would be called.
     * @return Object
     */
    public static Object invokeGetter(Object obj, String attrName) {
        try {
            ReflectiveClass ref = MAPPING.get(obj.getClass());
            if (ref == null) {
                MAPPING.put(obj.getClass(), (ref = new ReflectiveClass(obj.getClass())));
            }
            Method method = ref.getter(attrName);
            if (method != null) {
                return method.invoke(obj, new Object[]{});
            }
        } catch (Exception e) {
            // Do Nothing
        }
        return null;
    }

    /**
     * Invoke the setter method on the object passed as a method argument.
     *
     * <p>
     * A setter method of an attribute <code>foo</code> will be <code>setFoo()</code>. Such method takes one method
     * argument, hence the {@link Method#invoke(java.lang.Object, java.lang.Object...) }
     * is called with the argument <code>attrVal</code>. If no associated setter method is found, then this api will
     * return null.
     *
     * @param obj Object whose getter method will be invoked.
     * @param attrName Attribute name, whose associated setter method would be called.
     * @param attrVal The argument used in the method call.
     *
     * @return Object
     */
    public static Object invokeSetter(Object obj, String attrName, Object attrVal) {
        try {
            ReflectiveClass ref = MAPPING.get(obj.getClass());
            if (ref == null) {
                MAPPING.put(obj.getClass(), (ref = new ReflectiveClass(obj.getClass())));
            }
            Method method = ref.setter(attrName);
            if (method != null) {
                return method.invoke(obj, attrVal);
            }
        } catch (Exception e) {
            // Do Nothing
        }
        return null;
    }

    /**
     * Invoke the method of the given object.
     *
     * @param obj Object whose method will be invoked.
     * @param method The method name to be invoked.
     * @param args Optional method arguments.
     * @return Object
     */
    public static Object invoke(Object obj, String method, Object[] args) {
        Class[] types = new Class[args.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = args[i].getClass();
        }
        try {
            Method m = obj.getClass().getDeclaredMethod(method, types);
            return m.invoke(obj, args);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static class ReflectiveClass {

        private final Class<?> clazz;
        private final Map<String, Method> getters = new HashMap<>();
        private final Map<String, Method> setters = new HashMap<>();

        ReflectiveClass(Class<?> clazz) {
            this.clazz = clazz;

            while (clazz != Object.class) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isSynthetic()
                            || field.getName().equals("serialVersionUID")
                            || Modifier.isTransient(field.getModifiers())
                            || Modifier.isStatic(field.getModifiers())
                            || Modifier.isInterface(field.getModifiers())
                            || Modifier.isNative(field.getModifiers())) {

                        continue;
                    }
                    try {
                        // Generate Getter method mapping
                        Method getterMethod = clazz.getDeclaredMethod("get"
                                + Character.toUpperCase(field.getName().charAt(0))
                                + field.getName().substring(1), new Class[]{});

                        getters.put(field.getName(), getterMethod);

                        // Generate Setter method mapping
                        Method setterMethod = clazz.getDeclaredMethod("set"
                                + Character.toUpperCase(field.getName().charAt(0))
                                + field.getName().substring(1), new Class[]{field.getType()});

                        setters.put(field.getName(), setterMethod);
                    } catch (NoSuchMethodException | SecurityException e) {
                        // do nothing, there could be exceptions at each field level if
                        // getters or setters are not defined or access to those methods 
                        // is restricted.
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        public Class<?> clazz() {
            return clazz;
        }

        public Method getter(String name) {
            return getters.get(name);
        }

        public Method setter(String name) {
            return setters.get(name);
        }
    }
}
