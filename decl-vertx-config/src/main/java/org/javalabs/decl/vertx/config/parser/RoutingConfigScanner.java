package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import org.javalabs.decl.vertx.rest.Mapping;
import org.javalabs.decl.vertx.rest.ResourceMapping;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.javalabs.decl.util.AnnotationClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class RoutingConfigScanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingConfigScanner.class);
    
    public RoutingConfig scan() {
        return scan(new String[] {"."});
    }
 
    public RoutingConfig scan(String[] packages) {
        RoutingConfig rc = new RoutingConfig();
        
        List<Class<?>> handlers = AnnotationClassScanner
                .findAnnotatedClasses(ResourceMapping.class, packages);
        
        if (handlers.isEmpty()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("No vert.x handler class found");
            }
            return rc;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Found {} vert.x handler class(s). Handler(s): {}", handlers.size(), handlers);
        }
        List<org.javalabs.decl.vertx.jaxb.ResourceMapping> list = new ArrayList<>();
        for (Class<?> handler : handlers) {
            ResourceMapping rm = handler.getAnnotation(ResourceMapping.class);
            
            org.javalabs.decl.vertx.jaxb.ResourceMapping resource
                    = new org.javalabs.decl.vertx.jaxb.ResourceMapping();
            
            resource.setName(rm.name());
            resource.setFailure(Boolean.valueOf(rm.failure()));
            resource.setPath(rm.path());
            resource.setResource(handler.getName());
            
            Method[] methods = handler.getDeclaredMethods();
            List<org.javalabs.decl.vertx.jaxb.Mapping> mappings = new ArrayList<>();
            
            for (Method method : methods) {
                if (method.isAnnotationPresent(Mapping.class)) {
                    Mapping mp = method.getAnnotation(Mapping.class);
                    
                    org.javalabs.decl.vertx.jaxb.Mapping mapping
                            = new org.javalabs.decl.vertx.jaxb.Mapping();
                    
                    mapping.setUri(mp.uri());
                    mapping.setMethod(mp.method().name());
                    mapping.setApi(method.getName());
                    mapping.setConsume(mp.consume());
                    mapping.setProduce(mp.produce());
                    
                    mappings.add(mapping);
                }
            }
            resource.setMapping(mappings);
            
            list.add(resource);
        }
        rc.setResourceMapping(list);
        
        return rc;
    }
}
