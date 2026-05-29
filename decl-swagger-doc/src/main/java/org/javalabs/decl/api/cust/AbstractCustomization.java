package org.javalabs.decl.api.cust;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaVariable;

/**
 *
 * @author Sudiptasish Chanda
 */
public abstract class AbstractCustomization implements Customization {
    
    /**
     * Return the id field of this java class.
     * 
     * <p>
     * This method assumes that the {@link JavaClass} is precomputed, i.e., the variables and methods
     * are already generated.
     * 
     * @param jClass            Precomputed java class
     * @return JavaVariable     The identifier field.
     */
    protected JavaVariable idField(JavaClass jClass) {
        for (JavaVariable var : jClass.variables()) {
            if (var.idField()) {
                return var;
            }
        }
        return null;
    }
    
    protected String nameField(JavaClass jClass) {
        for (JavaVariable var : jClass.variables()) {
            if (var.name().toLowerCase().contains("name") && var.type() != Date.class) {
                return var.name();
            }
        }
        return null;
    }
    
    protected String createdOnField(JavaClass jClass) {
        for (JavaVariable var : jClass.variables()) {
            if (var.name().toLowerCase().contains("create") && var.type() == Date.class) {
                return var.name();
            }
        }
        return null;
    }
    
    /**
     * Return the updatable fields.
     * 
     * @param jClass
     * @return List
     */
    protected List<JavaVariable> updatableFields(JavaClass jClass) {
        List<JavaVariable> vars = new ArrayList<>();
        
        for (JavaVariable var : jClass.variables()) {
            if (! var.idField()
                    && ! (var.name().contains("create") && var.type() == Date.class)
                    && ! (var.name().contains("update") && var.type() == Date.class)
                    && ! var.name().equals("canonicalLink")) {
                
                vars.add(var);
            }
        }
        return vars;
    }
    
    protected String createdOnSetter(JavaClass jClass) {
        for (JavaVariable var : jClass.variables()) {
            if (var.name().contains("create") && var.type() == Date.class) {
                return setter(var.name());
            }
        }
        return null;
    }
    
    protected String updatedOnSetter(JavaClass jClass) {
        for (JavaVariable var : jClass.variables()) {
            if (var.name().contains("update") && var.type() == Date.class) {
                return setter(var.name());
            }
        }
        return null;
    }
    
    /**
     * Generate the getter method name for this field.
     * 
     * @param field
     * @return String
     */
    protected String getter(String field) {
        return "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
    }
    
    /**
     * Generate the setter method name for this field.
     * 
     * @param field
     * @return String
     */
    protected String setter(String field) {
        return "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
    }
}
