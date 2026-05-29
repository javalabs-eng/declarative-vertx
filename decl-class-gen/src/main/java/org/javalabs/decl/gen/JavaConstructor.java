package org.javalabs.decl.gen;

import java.util.List;
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
public class JavaConstructor implements CodeGenSupport, Element {
    
    private final JavaClass jClass;
    
    private String accessSpecifier = "public";
    private String[] argNames;
    private Class<?>[] argTypes;
    private Throwable[] errors;
    private String body;
    
    public JavaConstructor(JavaClass jClass) {
        this.jClass = jClass;
    }

    public void accessSpecifier(String accessSpecifier) {
        this.accessSpecifier = accessSpecifier;
    }

    public JavaConstructor argTypes(Class... args) {
        this.argTypes = args;
        return this;
    }

    public JavaConstructor argNames(String... args) {
        this.argNames = args;
        return this;
    }
    
    public JavaConstructor errors(Throwable... errors) {
        this.errors = errors;
        return this;
    }

    public JavaConstructor body(String body) {
        this.body = body;
        return this;
    }
    
    @Override
    public String snippet() {
        StringBuilder buff = new StringBuilder(512);
        buff.append(jClass.decorator().constructorIndent())
                .append(accessSpecifier)
                .append(SPACE)
                .append(jClass.name());
        
        if (argTypes == null) {
            buff.append("()");
        }
        else {
            buff.append("(");
            for (int i = 0; i < argTypes.length; i ++) {
                Class<?> argType = argTypes[i];
                String argName = argNames[i];
                
                if (argName == null) {
                    String tmp = argType.getSimpleName();
                    char ch = Character.toLowerCase(tmp.charAt(0));
                    argName = ch + tmp.substring(1);
                }
                buff.append(argType.getSimpleName())
                        .append(SPACE)
                        .append(argName);
                
                if (i < argTypes.length - 1) {
                    buff.append(COMMA).append(SPACE);
                }
            }
            buff.append(")");
        }
        if (errors != null) {
            buff.append("throws").append(SPACE);
            for (int i = 0; i < errors.length; i ++) {
                buff.append(errors[i]);
                if (i < errors.length - 1) {
                    buff.append(COMMA).append(SPACE);
                }
            }
        }
        buff.append(SPACE).append("{");
        if (body != null) {
            buff.append(NEW_LINE);
            buff.append(body);
            // buff.append(NEW_LINE);
        }
        if (argTypes == null) {
            buff.append("}");
        }
        else {
            buff.append(jClass.decorator().constructorIndent()).append("}");
        }
        
        return buff.toString();
    }
    
    public static JavaConstructor parameterized(JavaClass jClass) {
        List<JavaVariable> jVars = jClass.variables();
        StringBuilder body = new StringBuilder(jVars.size() * 20);

        Class[] types = new Class[jVars.size()];
        String[] args = new String[jVars.size()];

        for (int i = 0; i < jVars.size(); i ++) {
            JavaVariable jVar = jVars.get(i);

            types[i] = jVar.type();
            args[i] = jVar.name();

            body.append(jClass.decorator().bodyIndent())
                    .append("this")
                    .append(".")
                    .append(args[i])
                    .append(SPACE)
                    .append("=")
                    .append(SPACE)
                    .append(args[i])
                    .append(SEMICOLON)
                    .append(NEW_LINE);
        }
        return new JavaConstructor(jClass)
                .argTypes(types)
                .argNames(args)
                .body(body.toString());
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }
}
