package org.javalabs.decl.api.spec;

import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Schema;
import java.util.HashMap;
import java.util.Map;

/**
 * Schema definition for http 401 aka un-authorized.
 *
 * @author Sudiptasish Chanda
 */
public class UnAuthorisedSpec {
    
    public Schema schema() {
        Map<String, Property> map = new HashMap<>();
        
        Property prop = new Property();
        prop.setType("string");
        prop.setExample("app_code");
        map.put("code", prop);
        
        prop = new Property();
        prop.setType("string");
        prop.setExample("Access to the requested resource is restricted");
        map.put("message", prop);
        
        Schema schema = new Schema();
        schema.setType("object");
        schema.setProperties(map);
        
        return schema;
    }
}
