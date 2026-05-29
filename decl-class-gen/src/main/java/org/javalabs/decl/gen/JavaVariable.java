package org.javalabs.decl.gen;

import java.util.List;
import java.util.Objects;
import org.javalabs.decl.visitor.ClassVisitor;
import org.javalabs.decl.visitor.Element;

/**
 * Representation of a java variable.
 * 
 * <p>
 * In Java, a variable is a named container that holds a value. You can think of it as a box where you 
 * store data that you can use and manipulate throughout your program. Calling the appropriate visitor method
 * will generate the java variable names with the appropriate access specifier.
 *
 * @author Sudiptasish Chanda
 */
public class JavaVariable implements CodeGenSupport, Element {
    
    private final JavaClass jClass;
    
    private String accessSpecifier = "private";
    private List<JavaAnnotation> annotations; 
    private Class<?> type;
    private String typeName;
    private String name;
    
    // Extra attribute used in a model class to identify whether this field represents the primary key of the class.
    private Boolean idField = Boolean.FALSE;
    
    public JavaVariable(JavaClass jClass) {
        this.jClass = jClass;
    }

    public void accessSpecifier(String accessSpecifier) {
        this.accessSpecifier = accessSpecifier;
    }
    
    public Class<?> type() {
        return type;
    }

    public JavaVariable type(Class<?> type) {
        this.type = type;
        this.typeName = type.getName();
        return this;
    }
    
    public String typeName() {
        return typeName;
    }

    public JavaVariable typeName(String typeName) {
        this.typeName = typeName;
        return this;
    }
    
    public String name() {
        return name;
    }

    public JavaVariable name(String name) {
        this.name = name;
        return this;
    }
    
    public List<JavaAnnotation> annotations() {
        return annotations;
    }

    public JavaVariable annotations(List<JavaAnnotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.typeName);
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JavaVariable other = (JavaVariable) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.typeName, other.typeName);
    }
    
    public Boolean idField() {
        if (idField == null) {
            idField = (name.toLowerCase().endsWith("id") || name.toLowerCase().contains("identifier"))
                    && (typeName.equals("java.lang.String") || typeName.equals("java.lang.Integer") || typeName.equals("int") || typeName.equals("Long") || typeName.equals("java.lang.long") || typeName.equals("java.math.BigInteger"));
        }
        return idField;
    }
    
    public JavaVariable idField(Boolean idField) {
        this.idField = idField;
        return this;
    }
    
    @Override
    public String snippet() {
        StringBuilder buff = new StringBuilder(128);
        
        if (annotations != null) {
            for (JavaAnnotation ann : annotations) {
                buff.append(jClass.decorator().variableIndent())
                        .append(ann.snippet())
                        .append(NEW_LINE);
            }
        }
        buff.append(jClass.decorator().variableIndent())
                .append(accessSpecifier)
                .append(SPACE)
                .append(typeName.substring(typeName.lastIndexOf(".") + 1))
                .append(SPACE)
                .append(name)
                .append(SEMICOLON);
        
        return buff.toString();
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }
}
