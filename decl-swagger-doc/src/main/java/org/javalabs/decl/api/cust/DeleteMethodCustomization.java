package org.javalabs.decl.api.cust;

import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.CodeGenSupport;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaVariable;
import org.javalabs.decl.util.CharUtil;

/**
 * Customization layer for a delete api call.
 *
 * @author schan280
 */
public class DeleteMethodCustomization extends AbstractCustomization {
        
    public DeleteMethodCustomization() {}
    
    @Override
    public Result handlerEntry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            JavaVariable idVar = null;
            for (JavaVariable jVar : model.variables()) {
                if (jVar.idField()) {
                    idVar = jVar;
                    break;
                }
            }
            if (idVar != null) {
                // Employee employee = employeeBO.view(user(ctx), Integer.valueOf(id));
                buff.append(model.name())
                        .append(CodeGenSupport.SPACE)
                        .append(CharUtil.lowerFirst(model.name()))
                        .append(CodeGenSupport.SPACE)
                        .append(CodeGenSupport.EQUALS)
                        .append(CodeGenSupport.SPACE)
                        .append(CharUtil.lowerFirst(model.name()))
                        .append("BO")
                        .append(".")
                        .append("remove")
                        .append("(")
                        .append("user(ctx)")
                        .append(CodeGenSupport.COMMA)
                        .append(CodeGenSupport.SPACE)
                        .append(idVar.type() == String.class ? "id" : (idVar.type().getSimpleName() + ".valueOf(id)"))
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON)
                        .append(CodeGenSupport.NEW_LINE);
            }
            return new Result(buff.toString());
        }
        finally {
            buff.delete(0, buff.length());
        }
    }
    
    @Override
    public Result boEntry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        return new Result(buff.toString());
    }

    @Override
    public Result entry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            JavaVariable idVar = idField(model);
            
            if (idVar != null) {
                buff.append("// Remove the existing element from the in-memory store.");

                // Employee current = store.get(id);
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(project.inputResource().resource())
                        .append(CodeGenSupport.SPACE)
                        .append("element")
                        .append(CodeGenSupport.SPACE)
                        .append("=")
                        .append(CodeGenSupport.SPACE)
                        .append("store.remove(id)")
                        .append(CodeGenSupport.SEMICOLON);

                // if (current == null) {
                //     throw new NoSuchElementException("No element found for id: " + id);
                // }
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("if")
                        .append(CodeGenSupport.SPACE)
                        .append("(")
                        .append("element")
                        .append(CodeGenSupport.SPACE)
                        .append("==")
                        .append(CodeGenSupport.SPACE)
                        .append("null")
                        .append(")")
                        .append(CodeGenSupport.SPACE)
                        .append("{")
                        .append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("throw new NoSuchElementException(\"No element found for id: \" + id)")
                        .append(CodeGenSupport.SEMICOLON)
                        .append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("}");
            }
            
            // return current;
            buff.append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("return")
                    .append(CodeGenSupport.SPACE)
                    .append("element")
                    .append(CodeGenSupport.SEMICOLON);
            
            return new Result(buff.toString());
        }
        finally {
            buff.delete(0, buff.length());
        }
    }

    @Override
    public Result exit(Project project) {
        // Do Nothing.
        return null;
    }
    
}
