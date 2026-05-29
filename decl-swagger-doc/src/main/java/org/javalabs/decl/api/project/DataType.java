package org.javalabs.decl.api.project;

import java.util.Date;

/**
 * Supported data types.
 *
 * @author Sudiptasish Chanda
 */
public enum DataType {
    
    STR (String.class),
    INT (Integer.class),
    LONG (Long.class),
    FLOAT (Double.class),
    BOOL (Boolean.class),
    DATE (Date.class);
    
    private final Class<?> dtype;
    
    DataType(Class<?> dtype) {
        this.dtype = dtype;
    }
    
    public Class<?> dtype() {
        return this.dtype;
    }
}
