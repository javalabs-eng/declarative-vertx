package org.javalabs.decl.api.example;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Device {
    
    public static enum Type {Laptop, Mobile, HeadSet};
    
    private String id;
    private Type type;
    private String key;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
}
