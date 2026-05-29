package org.javalabs.decl.gen;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.javalabs.decl.visitor.JavaClassVisitor;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class JavaClassTest {
    
    //@Test
    public void testClassGen() {
        JavaClass jClass = new JavaClass("Job");
        jClass.addParentInterface(Serializable.class);
        jClass.addParentInterface(Cloneable.class);
        
        jClass.pkg(new JavaPackage("org.javalabs.model"));
        jClass.addImport(new JavaImport(Objects.class));
        
        jClass.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_NO_ARG);
        
        jClass.addVar(new JavaVariable(jClass).type(Integer.class).name("jobId"));
        jClass.addVar(new JavaVariable(jClass).type(String.class).name("jobName"));
        jClass.addVar(new JavaVariable(jClass).type(String.class).name("jobType"));
        jClass.addVar(new JavaVariable(jClass).type(Timestamp.class).name("scheduledDate"));
        
        jClass.autoGenMethod(Boolean.TRUE);
        
        JavaClass inner = new JavaClass("JobPK");
        jClass.inner(inner);
        
        inner.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_BOTH);
        inner.addVar(new JavaVariable(inner).type(Integer.class).name("jobId"));
        inner.autoGenMethod(Boolean.TRUE);
        inner.autoGenHashCode(Boolean.TRUE);
        inner.autoGenEquals(Boolean.TRUE);
        inner.freeze();
        
        jClass.freeze();
        
        JavaClassVisitor visitor = new JavaClassVisitor();
        jClass.accept(visitor);
        System.out.println(visitor.result());
    }
    
    @Test
    public void testClassGenWithAnnotation() throws ClassNotFoundException {
        JavaClass jClass = new JavaClass("Employee");
        jClass.addParentInterface(Serializable.class);
        jClass.addParentInterface(Cloneable.class);
        jClass.comment(new JavaComment(jClass).comment("This class is auto generated with jpa-lite.").author("Sudiptasish Chanda"));
        
        jClass.pkg(new JavaPackage("org.javalabs.model"));
        jClass.addImport(new JavaImport(Objects.class));
        
        jClass.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_NO_ARG);
        
        List<JavaAnnotation> anns = new ArrayList<>();
        anns.add(new JavaAnnotation(jClass).typeName("jakarta.persistence.Entity"));
        anns.add(new JavaAnnotation(jClass)
                .typeName("jakarta.persistence.Table")
                .props(new LinkedHashMap<>() {{ put("name", "employees"); }}));
        
        anns.add(new JavaAnnotation(jClass)
                .typeName("jakarta.persistence.IdClass")
                .props(new LinkedHashMap<>() {{ put("value", jClass.name() + "." + jClass.name() + "PK.class"); }}));
        
        JavaAnnotation nqAnns = new JavaAnnotation(jClass).typeName("jakarta.persistence.NamedNativeQueries");
        nqAnns.children(
                new JavaAnnotation(jClass)
                        .typeName("jakarta.persistence.NamedNativeQuery")
                        .props(new LinkedHashMap<>() {{ put("name", "Employee.selectAll"); put("query", "SELECT * FROM employees"); }})
                , new JavaAnnotation(jClass)
                        .typeName("jakarta.persistence.NamedNativeQuery")
                        .props(new LinkedHashMap<>() {{ put("name", "Employee.selectByNameAndLocation"); put("query", "SELECT * FROM employees WHERE name = ? AND location = ?"); }}));
        
        anns.add(nqAnns);
        
        for (JavaAnnotation jAnn : anns) {
            jClass.addAnnotation(jAnn);
        }
        Class<?> enumClazz = Class.forName("jakarta.persistence.GenerationType");
        Object enumVal = null;

        for (Object cons : enumClazz.getEnumConstants()) {
            if (((Enum)cons).name().equals("IDENTITY")) {
                enumVal = cons;
                break;
            }
        }
        final Object ev = enumVal;
        
        jClass.addVar(new JavaVariable(jClass)
                .type(Integer.class)
                .name("employeeId")
                .annotations(Arrays.asList(
                        new JavaAnnotation(jClass).typeName("jakarta.persistence.Id")
                        , new JavaAnnotation(jClass).typeName("jakarta.persistence.GeneratedValue").props(new LinkedHashMap<>() {{ put("strategy", ev); }})
                        , new JavaAnnotation(jClass)
                                .typeName("jakarta.persistence.Column")
                                .props(new LinkedHashMap<>() {{
                                    put("name", "employee_id"); }}))));
        
        jClass.addVar(new JavaVariable(jClass)
                .type(String.class)
                .name("location")
                .annotations(Arrays.asList(
                        new JavaAnnotation(jClass)
                                .typeName("jakarta.persistence.Column")
                                .props(new LinkedHashMap<>() {{
                                    put("name", "location"); 
                                    put("length", "128"); 
                                    put("nullable", Boolean.FALSE); 
                                    put("updatable", Boolean.FALSE);
                                }}))));
        
        jClass.addVar(new JavaVariable(jClass)
                .type(Class.forName("[Ljava.lang.String;"))
                .name("address")
                .annotations(Arrays.asList(
                        new JavaAnnotation(jClass)
                                .typeName("jakarta.persistence.Column")
                                .props(new LinkedHashMap<>() {{
                                    put("name", "address");
                                    put("length", "256"); 
                                    put("nullable", Boolean.FALSE); 
                                    put("updatable", Boolean.FALSE);
                                }}))));
        
        jClass.addVar(new JavaVariable(jClass)
                .type(byte[].class)
                .name("namespace")
                .annotations(Arrays.asList(
                        new JavaAnnotation(jClass)
                                .typeName("jakarta.persistence.Column")
                                .props(new LinkedHashMap<>() {{
                                    put("name", "namespace");
                                    put("length", "32"); 
                                    put("nullable", Boolean.TRUE); 
                                    put("updatable", Boolean.TRUE);
                                }}))));
        
        jClass.addVar(new JavaVariable(jClass)
                .type(Timestamp.class)
                .name("joinedDate")
                .annotations(Arrays.asList(
                        new JavaAnnotation(jClass)
                                .typeName("jakarta.persistence.Column")
                                .props(new LinkedHashMap<>() {{ put("name", "joined_date"); }}))));
        
        jClass.addVar(new JavaVariable(jClass)
                .type(Timestamp.class)
                .name("endDate")
                .annotations(Arrays.asList(
                        new JavaAnnotation(jClass).typeName("jakarta.persistence.Id")
                        , new JavaAnnotation(jClass)
                                .typeName("jakarta.persistence.Column")
                                .props(new LinkedHashMap<>() {{ put("name", "end_date"); }}))));
        
        
        jClass.autoGenMethod(Boolean.TRUE);
        
        JavaClass inner = new JavaClass("EmployeePK");
        jClass.inner(inner);
        
        inner.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_BOTH);
        inner.addVar(new JavaVariable(inner).type(Integer.class).name("employeeId"));
        inner.addVar(new JavaVariable(inner).type(Timestamp.class).name("endDate"));
        inner.autoGenMethod(Boolean.TRUE);
        inner.autoGenHashCode(Boolean.TRUE);
        inner.autoGenEquals(Boolean.TRUE);
        inner.freeze();
        
        jClass.freeze();
        
        JavaClassVisitor visitor = new JavaClassVisitor();
        jClass.accept(visitor);
        System.out.println(visitor.result());
    }
}
