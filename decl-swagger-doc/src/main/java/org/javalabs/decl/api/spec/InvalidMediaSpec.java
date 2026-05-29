package org.javalabs.decl.api.spec;

import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Schema;
import java.util.HashMap;
import java.util.Map;

/**
 * Schema definition for http 415 aka invalid media.
 *
 * @author Sudiptasish Chanda
 */
public class InvalidMediaSpec {
    
    public Schema schema() {
        Map<String, Property> map = new HashMap<>();
        
        Property prop = new Property();
        prop.setType("string");
        prop.setExample("app_code");
        map.put("code", prop);
        
        prop = new Property();
        prop.setType("string");
        prop.setExample("Invalid media type");
        map.put("message", prop);
        
        Schema schema = new Schema();
        schema.setType("object");
        schema.setProperties(map);
        
        return schema;
    }
}
