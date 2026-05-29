package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.cust.XMLSerializer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.javalabs.decl.util.Scanner;
import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Swagger doc generator for spring boot application.
 *
 * @author Sudiptasish Chanda
 */
public class VertxSwaggerDoc extends SwaggerDoc {
    
    public VertxSwaggerDoc() {
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
            RestController ann = clazz.getAnnotation(RestController.class);
            if (ann != null) {
                ResourceMapping rm = new ResourceMapping();
                
                if (clazz.getSimpleName().endsWith("Controller") || clazz.getSimpleName().endsWith("controller")) {
                    rm.setName(clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Controller")));
                }
                else {
                    rm.setName(clazz.getSimpleName());
                }
                
                // Found the controller.
                RequestMapping clMap = clazz.getAnnotation(RequestMapping.class);
                if (clMap != null) {
                    if (clMap.path() != null && clMap.path().length > 0) {
                        rm.setPath(clMap.path()[0]);
                    }
                    else if (clMap.value() != null && clMap.value().length > 0) {
                        rm.setPath(clMap.value()[0]);
                    }
                }
                rm.setResource(clazz.getName());
                resourceMappings.add(rm);
                
                Method[] methods = clazz.getDeclaredMethods();
                List<Mapping> mappings = new ArrayList<>();
                
                for (Method method : methods) {
                    Mapping mapping = null;
                    RequestMapping mrMap = method.getAnnotation(RequestMapping.class);
                    
                    if (mrMap != null) {
                        String[] paths = paths(mrMap);
                        String[] httpMethods = httpMethods(mrMap);
                        
                        mapping = introspect(method
                                , paths
                                , httpMethods
                                , mrMap.consumes()
                                , mrMap.produces());
                    }
                    else {
                        // Try http method specific annotation.
                        if (method.isAnnotationPresent(PostMapping.class)) {
                            PostMapping pm = method.getAnnotation(PostMapping.class);
                            String[] paths = paths(pm);
                            String[] httpMethods = new String[] {"POST"};
                            
                            mapping = introspect(method
                                    , paths
                                    , httpMethods
                                    , pm.consumes()
                                    , pm.produces());
                        }
                        else if (method.isAnnotationPresent(PutMapping.class)) {
                            PutMapping pm = method.getAnnotation(PutMapping.class);
                            String[] paths = paths(pm);
                            String[] httpMethods = new String[] {"PUT"};
                            
                            mapping = introspect(method
                                    , paths
                                    , httpMethods
                                    , pm.consumes()
                                    , pm.produces());
                        }
                        else if (method.isAnnotationPresent(GetMapping.class)) {
                            GetMapping gm = method.getAnnotation(GetMapping.class);
                            String[] paths = paths(gm);
                            String[] httpMethods = new String[] {"GET"};
                            
                            mapping = introspect(method
                                    , paths
                                    , httpMethods
                                    , gm.consumes()
                                    , gm.produces());
                        }
                        else if (method.isAnnotationPresent(DeleteMapping.class)) {
                            DeleteMapping dm = method.getAnnotation(DeleteMapping.class);
                            String[] paths = paths(dm);
                            String[] httpMethods = new String[] {"DELETE"};
                            
                            mapping = introspect(method
                                    , paths
                                    , httpMethods
                                    , dm.consumes()
                                    , dm.produces());
                        }
                        else if (method.isAnnotationPresent(PatchMapping.class)) {
                            PatchMapping pm = method.getAnnotation(PatchMapping.class);
                            String[] paths = paths(pm);
                            String[] httpMethods = new String[] {"PATCH"};
                            
                            mapping = introspect(method
                                    , paths
                                    , httpMethods
                                    , pm.consumes()
                                    , pm.produces());
                        }
                        else {
                            // Non-controller method.
                            // Do nothing (mapping object will be null, therefore don't consider it).
                        }
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
    
    private String[] paths(GetMapping mapping) {
        String[] paths = mapping.path();
        if (paths == null || paths.length == 0) {
            paths = mapping.value();
        }
        return paths;
    }
    
    private String[] paths(PutMapping mapping) {
        String[] paths = mapping.path();
        if (paths == null || paths.length == 0) {
            paths = mapping.value();
        }
        return paths;
    }
    
    private String[] paths(PostMapping mapping) {
        String[] paths = mapping.path();
        if (paths == null || paths.length == 0) {
            paths = mapping.value();
        }
        return paths;
    }
    
    private String[] paths(DeleteMapping mapping) {
        String[] paths = mapping.path();
        if (paths == null || paths.length == 0) {
            paths = mapping.value();
        }
        return paths;
    }
    
    private String[] paths(PatchMapping mapping) {
        String[] paths = mapping.path();
        if (paths == null || paths.length == 0) {
            paths = mapping.value();
        }
        return paths;
    }
    
    private String[] paths(RequestMapping mapping) {
        String[] paths = mapping.path();
        if (paths == null || paths.length == 0) {
            paths = mapping.value();
        }
        return paths;
    }
    
    private String[] httpMethods(RequestMapping mapping) {
        String[] httpMethods = null;
        if (mapping.method() != null && mapping.method().length > 0) {
            httpMethods = new String[mapping.method().length];
            for (int i = 0; i < mapping.method().length; i ++) {
                httpMethods[i] = mapping.method()[i].name();
            }
        }
        return httpMethods;
    }
    
    private Mapping introspect(Method method
            , String[] paths
            , String[] httpMethods
            , String[] consumes
            , String[] produces) {
        
        Mapping mapping = new Mapping();
        mapping.setApi(method.getName());

        if (paths != null && paths.length > 0) {
            mapping.setUri(paths[0]);
            int idx =  mapping.getUri().lastIndexOf("/");
            if (idx != -1 &&  mapping.getUri().charAt(idx + 1) == '{' && mapping.getUri().endsWith("}")) {
                mapping.setUri(mapping.getUri().substring(0, idx + 1) + ":" + mapping.getUri().substring(idx + 2, mapping.getUri().length() - 1));
            }
        }
        if (httpMethods != null && httpMethods.length > 0) {
            mapping.setMethod(httpMethods[0]);
        }
        if (consumes != null && consumes.length > 0) {
            mapping.setConsume(consumes[0]);
        }
        if (produces != null && produces.length > 0) {
            mapping.setProduce(produces[0]);
        }
        // Now, try to populate the schema
        if (httpMethods[0].equals("POST") || httpMethods[0].equals("PUT") || httpMethods[0].equals("PATCH")) {
            Class[] types = method.getParameterTypes();
            Annotation[][] arr = method.getParameterAnnotations();
            for (int i = 0; i < arr.length; i ++) {
                for (int j = 0; j < arr[i].length; j ++) {
                    if (arr[i][j].annotationType() == RequestBody.class) {
                         mapping.setSchema(types[i].getName());
                    }
                }
            }
        }
        else if (httpMethods[0].equals("GET") || httpMethods[0].equals("DELETE")) {
            Class<?> returnType = method.getReturnType();
            mapping.setSchema(returnType.getName());
        }
        return mapping;
    }
}
