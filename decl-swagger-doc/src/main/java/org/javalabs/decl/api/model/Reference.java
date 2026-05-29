package org.javalabs.decl.api.model;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Reference {
    
    private String $ref;
    
    public Reference() {}

    public Reference(String $ref) {
        this.$ref = $ref;
    }

    public String get$ref() {
        return $ref;
    }

    public void set$ref(String $ref) {
        this.$ref = $ref;
    }
}
