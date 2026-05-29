package org.javalabs.decl.gen.decorator;

import java.util.List;
import static org.javalabs.decl.gen.CodeGenSupport.NEW_LINE;
import static org.javalabs.decl.gen.CodeGenSupport.SEMICOLON;
import static org.javalabs.decl.gen.CodeGenSupport.SPACE;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaVariable;

/**
 *
 * @author Sudiptasish Chanda
 */
public class HashCodeDecorator {
    
    public String genHashCode(JavaClass jClass) {
        return genHashCode(jClass, jClass.variables());
    }
    
    public String genHashCode(JavaClass jClass, List<JavaVariable> jVars) {
        StringBuilder buff = new StringBuilder(100);
        
        buff.append(jClass.decorator().methodIndent())
                .append("@Override")
                .append(NEW_LINE);
        
        buff.append(jClass.decorator().methodIndent())
                .append("public")
                .append(SPACE)
                .append("int")
                .append(SPACE)
                .append("hashCode")
                .append("()")
                .append(SPACE)
                .append("{");
        
        buff.append(NEW_LINE);
        
        buff.append(jClass.decorator().bodyIndent())
                .append("int")
                .append(SPACE)
                .append("hash")
                .append(SPACE)
                .append("=")
                .append(SPACE)
                .append(7)
                .append(SEMICOLON)
                .append(NEW_LINE);
        
        for (JavaVariable jVar : jVars) {
            buff.append(jClass.decorator().bodyIndent())
                    .append("hash")
                    .append(SPACE)
                    .append("=")
                    .append(SPACE)
                    .append("71 * hash")
                    .append(SPACE)
                    .append("+")
                    .append(SPACE)
                    .append("Objects.hashCode")
                    .append("(")
                    .append("this")
                    .append(".")
                    .append(jVar.name())
                    .append(")")
                    .append(SEMICOLON)
                    .append(NEW_LINE);
        }
        
        buff.append(jClass.decorator().bodyIndent())
                .append("return")
                .append(SPACE)
                .append("hash")
                .append(SEMICOLON);
        
        buff.append(NEW_LINE)
                .append(jClass.decorator().methodIndent())
                .append("}");
        
        return buff.toString();
    }
}
