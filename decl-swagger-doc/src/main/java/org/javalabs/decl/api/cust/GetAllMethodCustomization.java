package org.javalabs.decl.api.cust;

import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;

/**
 * Customization layer for a get api call.
 *
 * @author schan280
 */
public class GetAllMethodCustomization extends AbstractCustomization {
    
    public GetAllMethodCustomization() {}

    @Override
    public Result handlerEntry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        return new Result(buff.toString());
    }
    
    @Override
    public Result boEntry(Project project, JavaClass model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result entry(Project project, JavaClass model) {
        final StringBuilder buff = new StringBuilder(256);
        
        try {
            // buff.append("\n\t\t\t").append("// Fetch all existing employees from the in-memory store.");
            
            // List<String> vals = ctx.queryParam("offset");
            // Integer offset = vals.isEmpty() ? 0 : Integer.valueOf(vals.get(0));
            
            // vals = ctx.queryParam("limit");
            // Integer limit = vals.isEmpty() ? DEFAULT_LIMIT : Integer.valueOf(vals.get(0));
            // buff.append("\n\t\t\t").append("List<String> vals")
            //         .append(" = ")
            //         .append("ctx.queryParam(\"offset\")")
            //         .append(";");
            // buff.append("\n\t\t\t").append("Integer offset")
            //         .append(" = ")
            //         .append("vals.isEmpty() ? 0 : Integer.valueOf(vals.get(0))")
            //         .append(";");
            //
            // buff.append("\n\n\t\t\t").append("vals")
            //         .append(" = ")
            //         .append("ctx.queryParam(\"limit\")")
            //         .append(";");
            // buff.append("\n\t\t\t").append("Integer limit")
            //         .append(" = ")
            //         .append("vals.isEmpty() ? DEFAULT_LIMIT : Integer.valueOf(vals.get(0))")
            //         .append(";");
            
            // Use offset and limit parameter while fetching the elements from store.
            // List<Employee> result = store.entrySet()
            //         .stream()
            //         .skip(offset)
            //         .limit(limit)
            //         .map((me) -> me.getValue())
            //         .collect(Collectors.toList());
            buff.append("\t\t\t").append("// Use offset and limit parameter while fetching the elements from store.");
            buff.append("\n\t\t\t").append("List<").append(project.inputResource().resource()).append(">").append(" ").append("result")
                    .append("\n\t\t\t\t\t").append(" = ").append("store.entrySet()")
                    .append("\n\t\t\t\t\t").append(".stream()")
                    .append("\n\t\t\t\t\t").append(".skip(offset)")
                    .append("\n\t\t\t\t\t").append(".limit(limit)")
                    .append("\n\t\t\t\t\t").append(".map((me) -> me.getValue())")
                    .append("\n\t\t\t\t\t").append(".collect(Collectors.toList())")
                    .append(";");
            
            // List<Object> items = new ArrayList<>(result.size());
            // for (Employee element : result) {
            //     Object[] row = new Object[] {element.getId()
            //             , element.getName()
            //             , element.getCreatedOn()
            //             , element.getCanonicalLink()};
            //     items.add(row);
            // }
            buff.append("\n\n\t\t\t").append("List<").append("Object").append(">").append(" ").append("items")
                    .append(" = ").append("new ArrayList<>(result.size())").append(";");
            
            buff.append("\n\t\t\t").append("for ").append("(")
                    .append(project.inputResource().resource()).append(" element").append(" : ").append("result")
                    .append(")").append(" {");
            
            String idField = idField(model).name();
            String nameField = nameField(model);
            String createdField = createdOnField(model);
            
            buff.append("\n\t\t\t\t").append("Map<String, Object> map = new LinkedHashMap<>()").append(";");
            buff.append("\n\t\t\t\t")
                    .append("map").append(".").append("put").append("(")
                    .append("\"").append(idField).append("\"").append(", ")
                    .append("element").append(".").append(getter(idField)).append("()").append(")").append(";");
            
            if (nameField != null) {
                buff.append("\n\t\t\t\t")
                    .append("map").append(".").append("put").append("(")
                    .append("\"").append(nameField).append("\"").append(", ")
                    .append("element").append(".").append(getter(nameField)).append("()").append(")").append(";");
            }
            buff.append("\n\t\t\t\t")
                    .append("map").append(".").append("put").append("(")
                    .append("\"").append(createdField).append("\"").append(", ")
                    .append("element").append(".").append(getter(createdField)).append("()").append(")").append(";");
            
            buff.append("\n\t\t\t\t")
                    .append("map").append(".").append("put").append("(")
                    .append("\"").append("canonicalLink").append("\"").append(", ")
                    .append("\"/api/v1/").append(project.inputResource().resource().toLowerCase()).append("s\"").append(" + \"/\" + ").append("element").append(".").append(getter(idField)).append("()").append(")").append(";");
            
            buff.append("\n\t\t\t\t").append("items.add(map)").append(";");
            buff.append("\n\t\t\t").append("}");
            
            // return build(ctx, store.size(), items, offset, limit);
            // buff.append("\n\t\t\t").append("return build(ctx, store.size(), items, offset, limit)").append(";");
            
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
