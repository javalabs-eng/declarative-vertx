package org.javalabs.decl.vertx.config;

import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that adds all the routers and sub routers.
 * 
 * <p>
 * In Vert.x, a router is an object that receives requests from an HTTP server and routes them to
 * the first matching route. A sub-router is defined by a class that defines a set of endpoints,
 * which can be mounted to add endpoints for various services.
 * 
 * <p>
 * A Router is one of the core concepts of Vert.x-Web. It’s an object which maintains zero or more Routes.
.* A router will pause the incoming HttpServerRequest, to ensure that the request body or any protocol
 * upgrades are not lost. Second, it will find the first matching route for that request, and passes
 * the request to that route.
 * The route can have a handler associated with it, which then receives the request. One then do something
 * with the request, and then, either end it or pass it to the next matching handler.
 *
 * @author Sudiptasish Chanda
 */
public class Routematic {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Routematic.class);
    
    private int index = 0;
    private final StringBuilder buff = new StringBuilder(256);
    
    public Routematic() {}
    
    /**
     * Add the resource path/uri to vertx router.
     * 
     * <p>
     * A router receives request from an HttpServer and routes it to the first matching Route that it contains.
     * A router can contain many routes. The routes having a handler method associated with it to do something
     * with the request.
     * 
     * @param vertx     Current Vert.x instance
     * @param root      Root router
     * @param rc        Routing configuration object 
     */
    public void addPath(Vertx vertx, Router root, RoutingConfig rc) {
        Api api = rc.getApi();
        List<ResourceMapping> resources = rc.getResourceMapping();
        
        String basePath = "";
        if (api.getBasePath() != null && api.getBasePath().trim().length() > 0) {
            basePath = api.getBasePath();
        }
        
        try {
            for (ResourceMapping resource : resources) {
                String resourcePath = resource.getPath().trim();
                
                Router router = Router.router(vertx);
                Object handler = handler(resource, vertx);
                
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodType methodType = MethodType.methodType(void.class, RoutingContext.class);
                
                try {
                    if (resource.getMapping().isEmpty()) {
                        CallSite site = LambdaMetafactory.metafactory(
                                lookup,
                                "handle",
                                MethodType.methodType(Handler.class, handler.getClass()),
                                methodType.erase(),
                                lookup.findVirtual(handler.getClass(), "handle", methodType),
                                methodType);

                        Handler<RoutingContext> h = (Handler<RoutingContext>)site.getTarget().invoke(handler);

                        if (resource.isFailure()) {
                            // Any resource with path ends with asterisk (*) has to be added to the root Router.
                            if (resourcePath.endsWith("*")) {
                                root.route(basePath + resourcePath).failureHandler(h);
                            }
                            else {
                                // router.route(basePath + resource.getPath()).failureHandler(h);
                                router.route().failureHandler(h);
                            }
                        }
                        else {
                            // Any resource with path ends with asterisk (*) has to be added to the root Router.
                            if (resourcePath.endsWith("*")) {
                                root.route(basePath + resourcePath).handler(h);
                            }
                            else {
                                // router.route(basePath + resource.getPath()).handler(h);
                                router.route().handler(h);
                            }
                        }
                    }
                    else {
                        for (Mapping mapping : resource.getMapping()) {
                            CallSite site = LambdaMetafactory.metafactory(
                                    lookup,
                                    "handle",
                                    MethodType.methodType(Handler.class, handler.getClass()),
                                    methodType.erase(),
                                    lookup.findVirtual(handler.getClass(), mapping.getApi(), methodType),
                                    methodType);

                            Handler<RoutingContext> h = (Handler<RoutingContext>)site.getTarget().invoke(handler);
                            
                            Route route = mapping.getUri().trim().length() > 0
                                    ? router.route(mapping.getUri().trim())
                                    : router.route();
                            
                            HttpMethod httpMethod = method(mapping.getMethod());
                            route = route.method(httpMethod);
                            
                            // For Accept header, override the default produce attribute with the resource specific one.
                            // Refer to the section <api version="1.0" basePath="..." produce="..." consume="..."/>
                            // of routing-config.xml
                            route = route.produces(produce(api, mapping.getProduce()));
                            
                            // For Content-Type header, do the following:
                            // If the http method is read-only, then use the resource specific consume attribute.
                            // Otherwse, override the default consume attribute with the resource specific one.
                            if (httpMethod == HttpMethod.OPTIONS || httpMethod == HttpMethod.HEAD
                                    || httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
                                
                                if (mapping.getConsume() != null && mapping.getConsume().trim().length() > 0) {
                                    route = route.consumes(mapping.getConsume());
                                }
                            }
                            else {
                                route = route.consumes(consume(api, mapping.getConsume()));
                            }
                            route.handler(h);
                        }
                    }
                    if (! router.getRoutes().isEmpty()) {
                        String tmp = "";
                        if (resourcePath.equals("/")) {
                            // A single slash character indicates that the router will be added to the root,
                            // thereby ignoring the basePath
                            tmp = resourcePath;
                        }
                        else {
                            tmp = basePath + resourcePath;
                        }
                        append(tmp, router);
                        root.route(tmp + "*").subRouter(router);
                        // root.mountSubRouter(tmp, router);
                        // root.route(basePath + resource.getPath().trim() + "*").subRouter(router);
                    }
                }
                catch (IllegalArgumentException e) {
                    LOGGER.error("Error adding route. Resource: {}, Path: {}"
                            , resource.getName(), resourcePath);
                    throw new RuntimeException(e);
                }
                /*Route route = root.getRoutes().get(1);
                Field field = route.getClass().getDeclaredField("state");
                field.setAccessible(Boolean.TRUE);
                Object state = field.get(route);        // Object of type RouteState
                
                Method method = state.getClass().getDeclaredMethod("getRoute", new Class[] {});
                route = (Route)method.invoke(state, new Object[] {});
                
                Field field = route.getClass().getDeclaredField("state");
                field.setAccessible(Boolean.TRUE);
                Object state = field.get(route);        // Object of type RouteState
                
                method = state.getClass().getDeclaredMethod("getContextHandlers", new Class[] {});
                List<Handler<RoutingContext>> handlers = method.invoke(state, new Object[] {});
                
                Handler<RoutingContext> handler = handlers.get(0);*/
                
                
            }
            // Print the different routes
            // print(root);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Route:\n{}", buff.toString());
            }
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
        finally {
            index = 0;
            buff.delete(0, buff.length());
        }
    }
    
    private Object handler(ResourceMapping resource, Vertx vertx) {
        try {
            Object handler = null;
            Object argVal = null;

            if ("true".equals(resource.isProvider())) {
                // Load the argument.
                if (resource.getArg() != null) {
                    String[] tmp = resource.getArg().split("#");
                    handler = ObjectCreator.create(tmp[0]
                            , new Class[] {Vertx.class}
                            , new Object[] {vertx});
                    
                    if (tmp.length == 2) {
                        argVal = ReflectionUtil.invoke(handler, tmp[1], new Object[] {});
                    }
                }
                
                Class<?> clazz = Class.forName(resource.getResource());
                Object[] param = new Object[] {};
                if (argVal != null) {
                    param = new Object[] {argVal};
                }
                Object provider = ReflectionUtil.invokeStatic(clazz, resource.getAccessor(), param);
                if (provider == null) {
                    return null;
                }
                if (resource.getConstruct() != null) {
                    handler = ReflectionUtil.invoke(provider, resource.getConstruct(), new Object[] {});
                }
                else {
                    handler = provider;
                }
            }
            else {
                handler = ObjectCreator.create(resource.getResource()
                        , new Class[] {Vertx.class}
                        , new Object[] {vertx});
            }
            return handler;
        }
        catch (ClassNotFoundException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void append(String path, Router root) {
        buff.append("(").append(++ index).append(") ").append(path).append("\n");
        
        List<Route> routes = root.getRoutes();
        for (Route route : routes) {
            Set<HttpMethod> methods = route.methods();
            if (methods != null) {
                Iterator<HttpMethod> itr = methods.iterator();
                if (itr.hasNext()) {
                    buff.append("\t")
                            .append("[").append(itr.next().name()).append("]")
                            .append("  ").append(route.getPath() != null ? route.getPath() : " ")
                            .append("  ").append(route.isExactPath())
                            .append("\n");
                }
            }
            else {
                buff.append("[").append(" ").append("]")
                        .append("  ").append(route.getPath())
                        .append("  ").append(route.isExactPath())
                        .append("\n");
            }
        }
        buff.append("\n");
    }
    
    private String produce(Api api, String produce) {
        if (produce != null && produce.trim().length() > 0) {
            return produce;
        }
        return api.getProduce();
    }
    
    private String consume(Api api, String consume) {
        if (consume != null && consume.trim().length() > 0) {
            return consume;
        }
        return api.getConsume();
    }
    
    private HttpMethod method(String method) {
        try {
            Field field = HttpMethod.class.getField(method);
            HttpMethod val = (HttpMethod) field.get(null);
            return val;
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        /*if (method.equalsIgnoreCase("GET")) {
            return HttpMethod.GET;
        }
        else if (method.equalsIgnoreCase("POST")) {
            return HttpMethod.POST;
        }
        else if (method.equalsIgnoreCase("PUT")) {
            return HttpMethod.PUT;
        }
        else if (method.equalsIgnoreCase("DELETE")) {
            return HttpMethod.DELETE;
        }
        else if (method.equalsIgnoreCase("PATCH")) {
            return HttpMethod.PATCH;
        }
        else if (method.equalsIgnoreCase("HEAD")) {
            return HttpMethod.HEAD;
        }
        else if (method.equalsIgnoreCase("OPTIONS")) {
            return HttpMethod.OPTIONS;
        }
        else {
            throw new RuntimeException("Invalid http method name: " + method);
        }*/
    }
}
