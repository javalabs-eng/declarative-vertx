package org.javalabs.decl.api.cust;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.CodeGenSupport;
import org.javalabs.decl.gen.JavaAnnotation;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaVariable;
import org.javalabs.decl.util.CharUtil;

/**
 * Customization layer for a post api call.
 *
 * @author schan280
 */
public class BatchMethodCustomization extends AbstractCustomization {

    private static final String THROW_TEMPLATE = "throw new {0}({1})";

    public BatchMethodCustomization() {
    }

    @Override
    public Result handlerEntry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        return new Result(buff.toString());
    }
    
    @Override
    public Result boEntry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            JavaVariable idVar = null;
            JavaVariable createDateVar = null;

            for (JavaVariable jVar : model.variables()) {
                if (jVar.idField()) {
                    // Primary key field cannot be updated.
                    idVar = jVar;
                } else if ((jVar.type() == Date.class || jVar.type() == java.sql.Date.class || jVar.type() == Timestamp.class || jVar.type() == Time.class)
                        && (jVar.name().toLowerCase().contains("create"))) {

                    // Created date will be updated separately by taking the current sys timestamp.
                    createDateVar = jVar;
                    break;
                }
            }
            String nextTab = "";
            if (idVar != null) {
                String idGetter = "get" + Character.toUpperCase(idVar.name().charAt(0)) + idVar.name().substring(1);
                // if (employee.getEmployeeId() != null) {
                //     throw new IllegalArgumentException("Should not specify id. It is auto-generated");
                // }
                for (JavaAnnotation jAnn : idVar.annotations()) {
                    if (jAnn.type().getName().equals("jakarta.persistence.GeneratedValue")) {
                        String errMsg = new StringBuilder(50)
                                .append("\"")
                                .append("Should not specify id. It is auto-generated")
                                .append("\"")
                                .toString();

                        nextTab = CodeGenSupport.TAB.concat(CodeGenSupport.TAB);
                        buff.append(nextTab)
                                .append("if")
                                .append(CodeGenSupport.SPACE)
                                .append("(")
                                .append(CharUtil.lowerFirst(model.name()))
                                .append(".")
                                .append(idGetter)
                                .append("()")
                                .append(CodeGenSupport.SPACE)
                                .append(CodeGenSupport.NOT)
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
                                .append(MessageFormat.format(THROW_TEMPLATE, "IllegalArgumentException", errMsg)) // Alternate: java.util.NoSuchElementException
                                .append(CodeGenSupport.SEMICOLON)
                                .append(CodeGenSupport.NEW_LINE)
                                .append(CodeGenSupport.TAB)
                                .append(CodeGenSupport.TAB)
                                .append("}")
                                .append(CodeGenSupport.NEW_LINE);
                    }
                }
            }
            // if (employee.getCreatedDate() == null) {
            //     employee.setCreatedDate(new Timestamp(DateUtil.currentUTCDate().getTime()));
            // }
            if (createDateVar != null) {
                String setter = "set" + Character.toUpperCase(createDateVar.name().charAt(0)) + createDateVar.name().substring(1);
                String getter = "get" + Character.toUpperCase(createDateVar.name().charAt(0)) + createDateVar.name().substring(1);

                String dateTimeApi = "DateUtil.currentUTCDate()";
                if (createDateVar.type() == Timestamp.class) {
                    dateTimeApi = "new Timestamp(DateUtil.currentUTCDate().getTime())";
                }
                nextTab = CodeGenSupport.TAB.concat(CodeGenSupport.TAB);
                buff.append("for")
                        .append(CodeGenSupport.SPACE)
                        .append("(")
                        .append(model.name())
                        .append(CodeGenSupport.SPACE)
                        .append(CharUtil.lowerFirst(model.name()))
                        .append(CodeGenSupport.SPACE)
                        .append(CodeGenSupport.COLON)
                        .append(CodeGenSupport.SPACE)
                        .append("records")
                        .append(")")
                        .append(CodeGenSupport.SPACE)
                        .append("{")
                        .append(CodeGenSupport.NEW_LINE);
                
                buff.append(nextTab)
                        .append(CodeGenSupport.TAB)
                        .append("if")
                        .append(CodeGenSupport.SPACE)
                        .append("(")
                        .append(CharUtil.lowerFirst(model.name()))
                        .append(".")
                        .append(getter)
                        .append("()")
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
                        .append(CodeGenSupport.TAB)
                        .append(CharUtil.lowerFirst(model.name()))
                        .append(".")
                        .append(setter)
                        .append("(")
                        .append(dateTimeApi)
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON)
                        .append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("}")
                        .append(CodeGenSupport.NEW_LINE)
                        .append(nextTab)
                        .append("}");
            }
            return new Result(buff.toString());
        } finally {
            buff.delete(0, buff.length());
        }
    }

    @Override
    public Result entry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            JavaVariable idVar = idField(model);
            String idGetter = null;
            
            if (idVar != null) {
                idGetter = getter(idVar.name());
                
                // for ({Resource} element : elements) {
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("for")
                        .append(CodeGenSupport.SPACE)
                        .append("(")
                        .append(project.inputResource().resource())
                        .append(CodeGenSupport.SPACE)
                        .append("element")
                        .append(CodeGenSupport.SPACE)
                        .append(CodeGenSupport.COLON)
                        .append(CodeGenSupport.SPACE)
                        .append("elements")
                        .append(")")
                        .append(CodeGenSupport.SPACE)
                        .append("{");
                

                // if (element.getId() != null) {
                //     throw new IllegalArgumentException("Should not specify id. It is auto-generated");
                // }
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("if")
                        .append(CodeGenSupport.SPACE)
                        .append("(")
                        .append("element")
                        .append(CodeGenSupport.STOP)
                        .append(idGetter)
                        .append("()")
                        .append(CodeGenSupport.SPACE)
                        .append("!=")
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
                        .append(CodeGenSupport.TAB)
                        .append("throw new IllegalArgumentException(\"Should not specify id. It is auto-generated\")")
                        .append(CodeGenSupport.SEMICOLON)
                        .append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("}");

                String setter = setter(idVar.name());

                // element.setId(COUNTER.incrementAndGet());
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("element")
                        .append(CodeGenSupport.STOP)
                        .append(setter)
                        .append("(")
                        .append(idVar.type() == String.class ? "String.valueOf(" : "")
                        .append("COUNTER.incrementAndGet()")
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON);
            }

            // element.setCreatedOn(new Date());
            String cg = createdOnSetter(model);
            if (cg != null) {
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("element")
                        .append(CodeGenSupport.STOP)
                        .append(cg)
                        .append("(")
                        .append("new Date()")
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON);
            }

            // store.put(element.getId(), element);
            if (idVar != null) {
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("// Keep it in the in-memory store.");

                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("store.put")
                        .append("(")
                        .append(Number.class.isAssignableFrom(idVar.type()) ? "String.valueOf(" : "")
                        .append("element").append(".").append(idGetter).append("()")
                        .append(Number.class.isAssignableFrom(idVar.type()) ? ")" : "")
                        .append(CodeGenSupport.COMMA)
                        .append(CodeGenSupport.SPACE)
                        .append("element")
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON);

                // element.setCanonicalLink(ctx.normalizedPath() + "/" + element.getId());
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("// Add the canonical link.");
                
                buff.append(CodeGenSupport.NEW_LINE)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append(CodeGenSupport.TAB)
                        .append("element.setCanonicalLink")
                        .append("(")
                        .append("\"api/v1\"").append(" + \"/\" + ").append("\"").append(project.inputResource().resource().toLowerCase()).append("s\"")
                        .append(" + ").append("\"/\"")
                        .append(" + ").append("element").append(".").append(idGetter).append("()")
                        .append(")")
                        .append(CodeGenSupport.SEMICOLON);
            }
            // }
            buff.append(CodeGenSupport.NEW_LINE)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append(CodeGenSupport.TAB)
                    .append("}");

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
                    .append("201")
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
                    .append("\"Inserted")
                    .append(CodeGenSupport.SPACE)
                    .append("\"")
                    .append(CodeGenSupport.SPACE)
                    .append("+")
                    .append(CodeGenSupport.SPACE)
                    .append("elements.size()")
                    .append(CodeGenSupport.SPACE)
                    .append("+")
                    .append(CodeGenSupport.SPACE)
                    .append("\"")
                    .append(CodeGenSupport.SPACE)
                    .append(project.inputResource().resource())
                    .append("(s)")
                    .append("\"")
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
