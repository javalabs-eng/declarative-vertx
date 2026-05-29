package org.javalabs.decl.api.cust;

/**
 * Output of a customization layer.
 *
 * @author Sudiptasish Chanda
 */
public class Result {
    
    private final Object obj;
    
    Result(Object obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return obj.toString();
    }
}
