package org.javalabs.decl.api.cust;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.CodeGenSupport;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaVariable;
import org.javalabs.decl.util.CharUtil;

/**
 * Customization layer for a put api call.
 *
 * @author Sudiptasish Chanda
 */
public class PutMethodCustomization extends AbstractCustomization {
    
    private static final String THROW_TEMPLATE = "throw new {0}({1})";
    
    public PutMethodCustomization() {}
    
    @Override
    public Result handlerEntry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            JavaVariable idVar = null;
            String idSetter = "";
            for (JavaVariable jVar : model.variables()) {
                if (jVar.idField()) {
                    idVar = jVar;
                    idSetter = "set" + Character.toUpperCase(jVar.name().charAt(0)) + jVar.name().substring(1);
                    break;
                }
            }
            if (idVar != null) {
                buff.append(CharUtil.lowerFirst(model.name()))
                        .append(".")
                        .append(idSetter)
                        .append("(")
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
        
        try {
            // Find the method for the primary key.
            List<String> idGetters = new ArrayList<>();
            for (JavaVariable jVar : model.variables()) {
                if (jVar.idField()) {
                    idGetters.add("get" + Character.toUpperCase(jVar.name().charAt(0)) + jVar.name().substring(1));
                    break;
                }
            }
            // Book existing = bookDAO.find(new Book.BookPK(book.getBookId(), book.getPublishDate()));
            StringBuilder param = new StringBuilder();
            for (int i = 0; i < idGetters.size(); i ++) {
                param.append(CharUtil.lowerFirst(model.name())).append(CodeGenSupport.STOP).append(idGetters.get(i)).append("()");
                if (i < idGetters.size() - 1) {
                    param.append(CodeGenSupport.COMMA).append(CodeGenSupport.SPACE);
                }
            }
            buff.append(model.name())
                    .append(CodeGenSupport.SPACE)
                    .append("existing")
                    .append(CodeGenSupport.SPACE)
                    .append(CodeGenSupport.EQUALS)
                    .append(CodeGenSupport.SPACE)
                    .append(CharUtil.lowerFirst(model.name())).append("DAO")
                    .append(CodeGenSupport.STOP)
                    .append("find")
                    .append("(")
                    .append("new")
                    .append(CodeGenSupport.SPACE)
                    .append(model.name())
                    .append(CodeGenSupport.STOP)
                    .append(model.name()).append("PK")
                    .append("(")
                    .append(param.toString())
                    .append(")")
                    .append(")")
                    .append(CodeGenSupport.SEMICOLON)
                    .append(CodeGenSupport.NEW_LINE);

            //  if (existing == null) {
            //      throw new IllegalArgumentException("No employee found for identifier: " + employee.getEmployeeId());
            //  }
            String errMsg = new StringBuilder(50)
                    .append("\"")
                    .append("No")
                    .append(CodeGenSupport.SPACE)
                    .append(CharUtil.lowerFirst(model.name()))
                    .append(CodeGenSupport.SPACE)
                    .append("found")
                    .append(CodeGenSupport.SPACE)
                    .append("for")
                    .append(CodeGenSupport.SPACE)
                    .append("identifier")
                    .append(CodeGenSupport.COLON)
                    .append(CodeGenSupport.SPACE)
                    .append("\"")
                    .append(CodeGenSupport.SPACE)
                    .append("+")
                    .append(CodeGenSupport.SPACE)
                    .append(CharUtil.lowerFirst(model.name()))
                    .append(".")
                    .append(idGetters.get(0))
                    .append("()")
                    .toString();

            buff.append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("if")
                    .append(CodeGenSupport.SPACE)
                    .append("(")
                    .append("existing")
                    .append(CodeGenSupport.SPACE)
                    .append(CodeGenSupport.EQUALS)
                    .append(CodeGenSupport.EQUALS)
                    .append(CodeGenSupport.SPACE)
                    .append(CodeGenSupport.NULL)
                    .append(")")
                    .append(CodeGenSupport.SPACE)
                    .append("{")
                    .append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(MessageFormat.format(THROW_TEMPLATE, "IllegalArgumentException", errMsg))   // Alternate: java.util.NoSuchElementException
                    .append(CodeGenSupport.SEMICOLON)
                    .append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("}")
                    .append(CodeGenSupport.NEW_LINE);

            // Update attributes of existing record
            buff.append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("// Update attributes of existing record")
                    .append(CodeGenSupport.NEW_LINE);

            JavaVariable lastUpdateVar = null;
            for (JavaVariable jVar : model.variables()) {
                if (jVar.idField()) {
                    // Primary key field cannot be updated.
                    continue;
                }
                if ((jVar.type() == Date.class || jVar.type() == java.sql.Date.class || jVar.type() == Timestamp.class || jVar.type() == Time.class)
                    && (jVar.name().toLowerCase().contains("update") || jVar.name().toLowerCase().contains("last"))) {

                    // Last updated date will be updated separately by taking the current sys timestamp.
                    lastUpdateVar = jVar;
                    continue;
                }
                if ((jVar.type() == Date.class || jVar.type() == java.sql.Date.class || jVar.type() == Timestamp.class || jVar.type() == Time.class)
                    && (jVar.name().toLowerCase().contains("create"))) {

                    // Created date should not be updated again !
                    continue;
                }
                String setter = "set" + Character.toUpperCase(jVar.name().charAt(0)) + jVar.name().substring(1);
                String getter = "get" + Character.toUpperCase(jVar.name().charAt(0)) + jVar.name().substring(1);

                // existing.setLocation(employee.getLocation());
                // existing.setSalary(employee.getSalary());
                // etc ...
                buff.append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("existing")
                        .append(".")
                        .append(setter)
                        .append("(")
                        .append(CharUtil.lowerFirst(model.name()))
                        .append(".")
                        .append(getter)
                        .append("()")
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON)
                        .append(CodeGenSupport.NEW_LINE);
            }
            // Finally, update the last updated timestamp.
            if (lastUpdateVar != null) {
                // existing.setUpdatedDate(new Timestamp(DateUtil.currentUTCDate().getTime()));
                String updMethod = "set" + Character.toUpperCase(lastUpdateVar.name().charAt(0)) + lastUpdateVar.name().substring(1);
                String dateTimeApi = "DateUtil.currentUTCDate()";
                if (lastUpdateVar.type() == Timestamp.class) {
                    dateTimeApi = "new Timestamp(DateUtil.currentUTCDate().getTime())";
                }
                if (lastUpdateVar.type() == Date.class) {
                    buff.append(CodeGenSupport.TAB)
                            .append(CodeGenSupport.TAB)
                            .append("existing")
                            .append(".")
                            .append(updMethod)
                            .append("(")
                            .append(dateTimeApi)
                            .append(")")
                            .append(CodeGenSupport.SEMICOLON)
                            .append(CodeGenSupport.NEW_LINE);
                }
            }
            return new Result(buff.toString());
        }
        finally {
            buff.delete(0, buff.length());
        }
    }

    @Override
    public Result entry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            JavaVariable idVar = idField(model);
            
            if (idVar != null) {
                // Employee current = store.get(id);
                buff.append("// Fetch the existing employee from the in-memory store.");
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(project.inputResource().resource())
                        .append(CodeGenSupport.SPACE)
                        .append("current")
                        .append(CodeGenSupport.SPACE)
                        .append(CodeGenSupport.EQUALS)
                        .append(CodeGenSupport.SPACE)
                        .append("store")
                        .append(CodeGenSupport.STOP)
                        .append("get")
                        .append("(")
                        .append("id")
                        .append(")")
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
                        .append("current")
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
                
                // Update all attributes.
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("// Update all attributes.");
                
                // current.setName(element.getName());
                // current.setLocation(element.getLocation());
                // current.setSalary(element.getSalary());
                for (JavaVariable var : updatableFields(model)) {
                    buff.append(CodeGenSupport.NEW_LINE)
                            .append(CodeGenSupport.TAB)
                            .append(CodeGenSupport.TAB)
                            .append(CodeGenSupport.TAB)
                            .append("current")
                            .append(CodeGenSupport.STOP)
                            .append(setter(var.name()))
                            .append("(")
                            .append("element")
                            .append(CodeGenSupport.STOP)
                            .append(getter(var.name()))
                            .append("()")
                            .append(")")
                            .append(CodeGenSupport.SEMICOLON);
                }
                // current.setUpdatedOn(new Date());
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("current")
                        .append(CodeGenSupport.STOP)
                        .append(updatedOnSetter(model))
                        .append("(")
                        .append("new Date()")
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON);
            }
                
            // Map<String, String> msg = new HashMap<>();
            // msg.put("code", "employee_modified");
            // msg.put("message", "{RESOURCE} modified successfully");
            // return msg;
            buff.append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("Map<String, Object> msg = new HashMap<>()")
                    .append(CodeGenSupport.SEMICOLON);
            
            buff.append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("msg.put")
                    .append("(")
                    .append("\"code\"")
                    .append(CodeGenSupport.COMMA)
                    .append(CodeGenSupport.SPACE)
                    .append("200")
                    .append(")")
                    .append(CodeGenSupport.SEMICOLON);
            
            buff.append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("msg.put")
                    .append("(")
                    .append("\"message\"")
                    .append(CodeGenSupport.COMMA)
                    .append(CodeGenSupport.SPACE)
                    .append("\"")
                    .append(project.inputResource().resource())
                    .append(CodeGenSupport.SPACE)
                    .append("modified successfully\"")
                    .append(")")
                    .append(CodeGenSupport.SEMICOLON);
            
            buff.append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("return")
                    .append(CodeGenSupport.SPACE)
                    .append("msg")
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
