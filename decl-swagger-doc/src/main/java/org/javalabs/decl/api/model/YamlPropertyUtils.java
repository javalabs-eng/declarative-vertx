package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.FieldProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 *
 * @author Sudiptasish Chanda
 */
public class YamlPropertyUtils extends PropertyUtils {
    
    public YamlPropertyUtils() {
        super();
    }

    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        try {
            Map<String, Property> properties = new LinkedHashMap<>();

            JsonPropertyOrder annotation = type.getAnnotation(JsonPropertyOrder.class);
            if (annotation == null) {
                return super.getPropertiesMap(type, bAccess);
            }
            String[] vals = annotation.value();

            for (String val : vals) {
                Field field = type.getDeclaredField(val);
                Method method = type.getDeclaredMethod(getter(val));
                properties.put(val, new FieldProperty(field));
            }
            return properties;
        }
        catch (NoSuchFieldException | NoSuchMethodException e) {
            System.err.println("Error introspecting class: " + type);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
        return getPropertiesMap(type, bAccess).values().stream().sequential()
            .filter(prop -> prop.isReadable() && (isAllowReadOnlyProperties() || prop.isWritable()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    
    
    private String getter(String var) {
        return "get" + Character.toUpperCase(var.charAt(0)) + var.substring(1);
    }
}
