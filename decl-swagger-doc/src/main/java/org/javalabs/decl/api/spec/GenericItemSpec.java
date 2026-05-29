package org.javalabs.decl.api.spec;

import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Schema;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 *
 * @author Sudiptasish Chanda
 */
public class GenericItemSpec {
    
    public Schema schema(String pojo, String uri) {
        Schema schema = new Schema();
        schema.setProperties(new LinkedHashMap<>());
        
        // Include to additional properties, only if they are present in the pojo:
        // 1. id
        // 2. createdDate
        
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(pojo);
            Field idField = null;
            
            for (Field field : clazz.getDeclaredFields()) {
                if (((field.getName().contains("id") || field.getName().contains("Id") || field.getName().contains("ID"))
                        && (Number.class.isAssignableFrom(field.getType()) || field.getType() == String.class))) {
                    
                    idField = field;
                    Property prop = new Property();
                    if (Number.class.isAssignableFrom(field.getType())) {
                        if (field.getType() == Long.class || field.getType() == long.class) {
                            prop.setType("number");
                            prop.setType("int64");
                        }
                        else {
                            prop.setType("integer");
                        }
                        prop.setExample(100);
                    }
                    else {
                        prop.setType(field.getType().getSimpleName().toLowerCase());
                        prop.setExample("100");
                    }
                    schema.getProperties().put(field.getName(), prop);
                }
                else if (field.getName().endsWith("name") || field.getName().endsWith("Name")) {
                    Property prop = new Property();
                    prop.setType("string");
                    prop.setExample("Any Name");
                    schema.getProperties().put(field.getName(), prop);
                }
                else if ((field.getName().contains("create") || field.getName().contains("update")) && Date.class.isAssignableFrom(field.getType())) {
                    Property prop = new Property();
                    prop.setType("string");
                    prop.setFormat("date");
                    prop.setExample(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    schema.getProperties().put(field.getName(), prop);
                }
                else {
                    
                }
            }
            if (idField != null) {
                // For id, add canonical link
                Property prop = new Property();
                prop.setType("string");
                prop.setExample(uri + "/" + "100");
                schema.getProperties().put("canonicalLink", prop);
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return schema;
    }
}
