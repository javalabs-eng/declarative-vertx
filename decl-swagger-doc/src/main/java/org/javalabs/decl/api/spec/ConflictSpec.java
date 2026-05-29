package org.javalabs.decl.api.spec;

import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Schema;
import java.util.HashMap;
import java.util.Map;

/**
 * Schema definition for http 409 aka conflict.
 *
 * @author Sudiptasish Chanda
 */
public class ConflictSpec {
    
    public Schema schema() {
        Map<String, Property> map = new HashMap<>();
        
        Property prop = new Property();
        prop.setType("string");
        prop.setExample("app_code");
        map.put("code", prop);
        
        prop = new Property();
        prop.setType("string");
        prop.setExample("Specified element already exists");
        map.put("message", prop);
        
        Schema schema = new Schema();
        schema.setType("object");
        schema.setProperties(map);
        
        return schema;
    }
}
