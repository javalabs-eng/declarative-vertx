package org.javalabs.decl.gen;

import java.util.LinkedHashMap;
import java.util.Map;
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
public class JavaAnnotation implements CodeGenSupport, Element {
    
    private final JavaClass jClass;
    
    private Class<?> type;
    private LinkedHashMap<String, Object> props;
    
    // Annotation can have an array of annotations.
    private JavaAnnotation[] childrenAnn;
    
    public JavaAnnotation(JavaClass jClass) {
        this(jClass, null, null);
    }

    public JavaAnnotation(JavaClass jClass, Class<?> type, LinkedHashMap<String, Object> props) {
        this.jClass = jClass;
        this.type = type;
        this.props = props;
    }

    public Class<?> type() {
        return type;
    }

    public JavaAnnotation type(Class<?> type) {
        this.type = type;
        return this;
    }

    public JavaAnnotation typeName(String typeName) {
        try {
            this.type = Class.forName(typeName);
            return this;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object prop(String key) {
        return this.props.get(key);
    }

    public JavaAnnotation props(LinkedHashMap<String, Object> props) {
        this.props = props;
        return this;
    }

    public JavaAnnotation children(JavaAnnotation... childrenAnn) {
        this.childrenAnn = childrenAnn;
        return this;
    }
    
    public JavaAnnotation[] children() {
        return this.childrenAnn;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.type);
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
        final JavaAnnotation other = (JavaAnnotation) obj;
        return Objects.equals(this.type, other.type);
    }
    
    @Override
    public String snippet() {
        StringBuilder buff = new StringBuilder(128);
        if (childrenAnn == null) {
            buff.append(childSnippet(this, jClass.decorator().depth()));
        }
        else {
            // Add child annotation(s)
            if (childrenAnn != null) {
                buff.append(jClass.decorator().indent(jClass.decorator().depth()))
                        .append(ANN)
                        .append(type.getSimpleName())
                        .append("(")
                        .append("{");

                for (int i = 0; i < childrenAnn.length; i ++) {
                    buff.append(NEW_LINE)
                            .append(childSnippet(childrenAnn[i], jClass.decorator().depth() + 1));

                    if (i < childrenAnn.length - 1) {
                        buff.append(COMMA);
                    }
                }
                buff.append(NEW_LINE)
                        .append("}")
                        .append(")");
            }
        }
        return buff.toString();
    }
    
    private String childSnippet(JavaAnnotation ann, int depth) {
        StringBuilder buff = new StringBuilder(128);
        buff.append(jClass.decorator().indent(depth))
                .append(ANN)
                .append(ann.type.getSimpleName());
        
        if (ann.props != null && ! ann.props.isEmpty()) {
            buff.append("(");
            if (ann.props.size() == 1 && ann.props.containsKey("value")) {
                buff.append(ann.props.get("value"));
            }
            else if (ann.props.size() >= 1) {
                int idx = 0;

                String value = "";
                for (Map.Entry<String, Object> me : ann.props.entrySet()) {
                    String key = me.getKey();
                    Object val = me.getValue();

                    if (val != null) {
                        if (idx > 0) {
                            buff.append(COMMA).append(SPACE);
                        }
                        if (val.getClass().isEnum()) {
                            value = val.getClass().getSimpleName() + "." + ((Enum)val).name();
                        }
                        else if (val instanceof JavaAnnotation) {
                            JavaAnnotation cAnn = (JavaAnnotation)val;

                            int j = 0;
                            StringBuilder sb = new StringBuilder();
                            sb.append("@").append(cAnn.type().getSimpleName()).append("(");

                            for (Map.Entry<String, Object> childMe : cAnn.props.entrySet()) {
                                String cKey = childMe.getKey();
                                Object cVal = childMe.getValue();
                                if (j > 0) {
                                    sb.append(COMMA).append(SPACE);
                                }
                                sb.append(cKey)
                                        .append(SPACE)
                                        .append(EQUALS)
                                        .append(SPACE)
                                        .append(cVal.getClass() == String.class ? "\"" : "")
                                        .append(cVal)
                                        .append(cVal.getClass() == String.class ? "\"" : "");

                                j ++;
                            }
                            sb.append(")");
                            value = sb.toString();
                        }
                        else {
                            value = val.toString();
                        }
                        // For data type TEXT, there is no max limit.
                        buff.append(key)
                                .append(SPACE)
                                .append(EQUALS)
                                .append(SPACE)
                                .append(val.getClass() == String.class ? "\"" : "")
                                .append(value)
                                .append(val.getClass() == String.class ? "\"" : "");

                        idx ++;
                    }
                }
            }
            buff.append(")");
        }
        
        return buff.toString();
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }
}
