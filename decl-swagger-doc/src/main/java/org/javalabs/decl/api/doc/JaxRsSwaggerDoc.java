package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.cust.XMLSerializer;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.javalabs.decl.util.Scanner;
import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;

/**
 * Swagger doc generator for spring boot application.
 *
 * @author Sudiptasish Chanda
 */
public class JaxRsSwaggerDoc extends SwaggerDoc {
    
    public JaxRsSwaggerDoc() {
        super();
    }
    
    @Override
    public void generate(DocOption docOpt) throws Exception {
        RoutingConfig rc = new RoutingConfig();
        Api api = new Api();
        api.setVersion("1.0.0");
        rc.setApi(api);
        
        List<Class> classes = Scanner.scan(docOpt.getPackages());
        List<ResourceMapping> resourceMappings = new ArrayList<>();
        
        for (Class<?> clazz : classes) {
            Path path = clazz.getAnnotation(Path.class);
            
            if (path != null) {
                ResourceMapping rm = new ResourceMapping();
                rm.setName(clazz.getSimpleName());
                rm.setPath(path.value());
                rm.setResource(clazz.getName());
                
                resourceMappings.add(rm);
                
                Consumes consumes = null;
                Produces produces = null;
                
                Method[] methods = clazz.getDeclaredMethods();
                List<Mapping> mappings = new ArrayList<>();
                
                for (Method method : methods) {
                    Mapping mapping = null;
                    path = method.getAnnotation(Path.class);
                    consumes = method.getAnnotation(Consumes.class);
                    produces = method.getAnnotation(Produces.class);
                        
                    // Try http method specific annotation.
                    if (method.isAnnotationPresent(POST.class)) {
                        mapping = introspect(method
                                , path != null ? path.value() : ""
                                , "POST"
                                , consumes != null ? consumes.value()[0] : null
                                , produces != null ? produces.value()[0] : null);
                    }
                    else if (method.isAnnotationPresent(PUT.class)) {
                        mapping = introspect(method
                                , path != null ? path.value() : ""
                                , "PUT"
                                , consumes != null ? consumes.value()[0] : null
                                , produces != null ? produces.value()[0] : null);
                    }
                    else if (method.isAnnotationPresent(GET.class)) {
                        mapping = introspect(method
                                , path != null ? path.value() : ""
                                , "GET"
                                , consumes != null ? consumes.value()[0] : null
                                , produces != null ? produces.value()[0] : null);
                    }
                    else if (method.isAnnotationPresent(DELETE.class)) {
                        mapping = introspect(method
                                , path != null ? path.value() : ""
                                , "DELETE"
                                , consumes != null ? consumes.value()[0] : null
                                , produces != null ? produces.value()[0] : null);
                    }
                    else if (method.isAnnotationPresent(PATCH.class)) {
                        mapping = introspect(method
                                , path != null ? path.value() : ""
                                , "PATCH"
                                , consumes != null ? consumes.value()[0] : null
                                , produces != null ? produces.value()[0] : null);
                    }
                    else {
                        // Non-controller method.
                        // Do nothing (mapping object will be null, therefore don't consider it).
                    }
                    if (mapping != null) {
                        mappings.add(mapping);
                    }
                }
                rm.setMapping(mappings);
            }
        }
        rc.setResourceMapping(resourceMappings);
        
        // Dump it in xml format.
        String result = XMLSerializer.serialize(rc);
        docOpt.setCfgContent(result);
        
        // Call the super class
        super.generate(docOpt);
    }
    
    private Mapping introspect(Method method
            , String path
            , String httpMethod
            , String consume
            , String produce) {
        
        Mapping mapping = new Mapping();
        mapping.setApi(method.getName());

        if (path != null && path.length() > 0) {
            // If the Path has {%s}, then replace it with :%s
            // E.g., /employees/{empId}/departments/{deptId} will be changed to /employees/:empId/departments/:deptId
            path = path.replace("{", ":");
            path = path.replace("}", "");
            mapping.setUri(path);
        }
        if (httpMethod != null && httpMethod.length() > 0) {
            mapping.setMethod(httpMethod);
        }
        if (consume != null && consume.length() > 0) {
            mapping.setConsume(consume);
        }
        if (produce != null && produce.length() > 0) {
            mapping.setProduce(produce);
        }
        // Now, try to populate the schema
        if (httpMethod.equals("POST") || httpMethod.equals("PUT") || httpMethod.equals("PATCH")) {
            Class[] types = method.getParameterTypes();
            for (Class type : types) {
                if (! type.getName().startsWith("java.")
                        && ! type.getName().startsWith("javax.")) {
                    mapping.setSchema(type.getName());
                }
            }
        }
        else if (httpMethod.equals("GET") || httpMethod.equals("DELETE")) {
            Class<?> returnType = method.getReturnType();
            mapping.setSchema(returnType.getName());
        }
        return mapping;
    }
}
