package org.javalabs.decl.gen.decorator;

import java.util.List;
import static org.javalabs.decl.gen.CodeGenSupport.COMMA;
import static org.javalabs.decl.gen.CodeGenSupport.NEW_LINE;
import static org.javalabs.decl.gen.CodeGenSupport.SEMICOLON;
import static org.javalabs.decl.gen.CodeGenSupport.SPACE;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaVariable;

/**
 *
 * @author Sudiptasish Chanda
 */
public class EqualsDecorator {
    
    public String genEquals(JavaClass jClass) {
        return genEquals(jClass, jClass.variables());
    }
    
    public String genEquals(JavaClass jClass, List<JavaVariable> jVars) {
        StringBuilder buff = new StringBuilder(100);
        
        buff.append(jClass.decorator().methodIndent())
                .append("@Override")
                .append(NEW_LINE);
        
        buff.append(jClass.decorator().methodIndent())
                .append("public")
                .append(SPACE)
                .append("boolean")
                .append(SPACE)
                .append("equals")
                .append("(")
                .append("Object obj")
                .append(")")
                .append(SPACE)
                .append("{");
        
        buff.append(NEW_LINE);
        
        buff.append(jClass.decorator().bodyIndent())
                .append("if")
                .append(SPACE)
                .append("(")
                .append("this == obj")
                .append(")")
                .append(SPACE)
                .append("{")
                .append(NEW_LINE)
                .append(jClass.decorator().bodyConditionalIndent())
                .append("return true")
                .append(SEMICOLON)
                .append(NEW_LINE)
                .append(jClass.decorator().bodyIndent())
                .append("}");
        
        buff.append(NEW_LINE);

        buff.append(jClass.decorator().bodyIndent())
                .append("if")
                .append(SPACE)
                .append("(")
                .append("obj == null")
                .append(")")
                .append(SPACE)
                .append("{")
                .append(NEW_LINE)
                .append(jClass.decorator().bodyConditionalIndent())
                .append("return false")
                .append(SEMICOLON)
                .append(NEW_LINE)
                .append(jClass.decorator().bodyIndent())
                .append("}");
        
        buff.append(NEW_LINE);

        buff.append(jClass.decorator().bodyIndent())
                .append("if")
                .append(SPACE)
                .append("(")
                .append("getClass() != obj.getClass()")
                .append(")")
                .append(SPACE)
                .append("{")
                .append(NEW_LINE)
                .append(jClass.decorator().bodyConditionalIndent())
                .append("return false")
                .append(SEMICOLON)
                .append(NEW_LINE)
                .append(jClass.decorator().bodyIndent())
                .append("}");
        
        buff.append(NEW_LINE);

        buff.append(jClass.decorator().bodyIndent())
                .append("final")
                .append(SPACE)
                .append(jClass.name())
                .append(SPACE)
                .append("other")
                .append(SPACE)
                .append("=")
                .append(SPACE)
                .append("(")
                .append(jClass.name())
                .append(")")
                .append("obj")
                .append(SEMICOLON)
                .append(NEW_LINE);

        for (int i = 0; i < jVars.size(); i ++) {
            JavaVariable jVar = jVars.get(i);
            
            buff.append(jClass.decorator().bodyIndent())
                    .append("if")
                    .append(SPACE)
                    .append("(")
                    .append("! Objects.equals(")
                    .append("this")
                    .append(".")
                    .append(jVar.name())
                    .append(COMMA)
                    .append(SPACE)
                    .append("other")
                    .append(".")
                    .append(jVar.name())
                    .append(")")
                    .append(")")
                    .append(SPACE)
                    .append("{")
                    .append(NEW_LINE)
                    .append(jClass.decorator().bodyConditionalIndent())
                    .append("return false")
                    .append(SEMICOLON)
                    .append(NEW_LINE)
                    .append(jClass.decorator().bodyIndent())
                    .append("}")
                    .append(NEW_LINE);
        }
        buff.append(jClass.decorator().bodyIndent())
                .append("return")
                .append(SPACE)
                .append("true")
                .append(SEMICOLON);
        
        buff.append(NEW_LINE)
                .append(jClass.decorator().methodIndent())
                .append("}");
        
        return buff.toString();
    }
}
