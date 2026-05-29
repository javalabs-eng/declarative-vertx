package org.javalabs.decl.gen;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class JavaAnnotationTest {

    @Test
    public void testChildAnnotation() {
        JavaClass jClass = new JavaClass("User");
        
        LinkedHashMap<String, Object> props = new LinkedHashMap<>();
        props.put("name", "role");
        props.put("nullable", false);
        props.put("updatable", true);
        
        // @Column(name = "role", updatable = true, check = @CheckConstraint(constraint = "role IN ('ACTIVE', 'INACTIVE', 'BLOCKED')")
        
        LinkedHashMap<String, Object> childProp = new LinkedHashMap<>();
        childProp.put("constraint", "role IN ('ACTIVE', 'INACTIVE', 'BLOCKED')");
        
        JavaAnnotation cAnn = new JavaAnnotation(jClass).type(CheckConstraint.class).props(childProp);
        props.put("check", cAnn);
        
        JavaAnnotation jAnn = new JavaAnnotation(jClass).type(Column.class).props(props);
        System.out.println(jAnn.snippet());
    }
}
