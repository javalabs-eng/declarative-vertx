package org.javalabs.decl.vertx.container;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerFormatter;
import io.vertx.ext.web.handler.LoggerHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.javalabs.decl.container.spi.EmbeddedHttpServer;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.vertx.config.Routematic;
import org.javalabs.decl.vertx.config.internal.ConfigStorage;
import org.javalabs.decl.vertx.config.parser.RoutingConfigParser;
import org.javalabs.decl.vertx.config.parser.RoutingConfigScanner;
import org.javalabs.decl.vertx.container.handler.Filter;
import org.javalabs.decl.vertx.jaxb.AccessLog;
import org.javalabs.decl.vertx.jaxb.AuthConstraint;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import org.javalabs.decl.vertx.jaxb.SecurityConstraint;
import org.javalabs.decl.vertx.jaxb.WebServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A vertx implementation of http web server.
 * 
 * <p>
 * Vert.x specific implementation of {@link EmbeddedHttpServer}.
 * Vert.x makes it easy to create an HTTP server for your application to receive HTTP requests.
 * To manage incoming HTTP requests you must set the request handler on the HTTP server. This is 
 * usually done with the help of {@link io.vertx.ext.web.Router} before starting the server. 
 *
 * @author Sudiptasish Chanda
 */
public class VertxHttpServer extends ServerConfigSupport implements EmbeddedHttpServer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxHttpServer.class);
    
    private final String defaultAuthHandler = "org.javalabs.decl.vertx.container.handler.AuthorizationHandler";
    
    // Internal Http Server.
    private HttpServer internalServer = null;

    // Default Port
    protected static final int DEFAULT_PORT = 8080;
    protected static final int DEFAULT_PAYLOAD_SIZE = 10 * 1024 * 1024;      // 4 MB
    
    private final WebServerConfig config;
    private final RoutingConfigParser parser;
    private final RoutingConfigScanner scanner;
    private final Routematic rmatic;
    
    public VertxHttpServer(WebServerConfig config) {
        this.config = config;
        this.parser = RoutingConfigParser.parser();
        this.scanner = new RoutingConfigScanner();
        this.rmatic = new Routematic();
    }

    @Override
    public void start(Object arg) {
        if (! (arg instanceof Vertx)) {
            throw new RuntimeException("Expecting a Vert.x instance. Cannot initialize Vert.x http server");
        }
        Vertx vertx = (Vertx)arg;
        
        // Prepare the configuration for http server.
        HttpServerOptions options = setupOptions(config);
        
        // Initialize the router.
        Router router = initRouter(vertx);
        
        // Read routing configuration and add the resource handler(s).
        if (config.getRoutingConfig() != null) {
            try {
                RoutingConfig rc = parser.read();
                // ConfigStorage.get().store(rc);
            }
            catch (RuntimeException e) {
                LOGGER.error(e.getMessage());
            }
        }
        // Look for any resource handler(s) with vert.x specific annotation.
        RoutingConfig rc = scanner.scan();
        if (! rc.getResourceMapping().isEmpty()) {
            RoutingConfig existing = ConfigStorage.get().routingConfig();
            if (existing == null) {
                ConfigStorage.get().store(rc);
            }
            else {
                existing.getResourceMapping().addAll(rc.getResourceMapping());
            }
        }
        // Finally, add the handlers to the router.
        rmatic.addPath(vertx, router, ConfigStorage.get().routingConfig());
        
        internalServer = vertx.createHttpServer(options);

        // In order to handle incoming HTTP requests a request handler must be set
        // on the HTTP server. This is normally done before starting the server.
        internalServer.requestHandler(router);

        // Once the HTTP server is created, it can be started using its listen() method.
        internalServer.listen(overridePort(config.getServerOpts().getPort()));
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Started Http Server. Listening to port: {}", internalServer.actualPort());
        }
    }
    
    /**
     * Initialize the router for http server.
     * @return Router
     */
    private Router initRouter(Vertx vertx) {
        // Create a Router object.
        // The router is the cornerstone of Vert.x Web.
        // This object is responsible for dispatching the HTTP requests to the right handler.
        Router router = Router.router(vertx);
        
        // Disable file-upload.
        router.route("/*").handler(BodyHandler.create(false).setBodyLimit(DEFAULT_PAYLOAD_SIZE));
        
        // Add the common filter.
        Set<String> allowedHeaders = new HashSet<>();
        Set<HttpMethod> allowedMethods = new HashSet<>();
        
        if (config.getCrossOrigin() != null) {
            if (config.getCrossOrigin().getAllowedHeaders() != null) {
                allowedHeaders = config.getCrossOrigin().getAllowedHeaders().getAllowedHeader();
            }
            if (config.getCrossOrigin().getAllowedMethods()!= null) {
                Set<String> set = config.getCrossOrigin().getAllowedMethods().getAllowedMethod();
                for (String m : set) {
                    allowedMethods.add(HttpMethod.valueOf(m));
                }
            }
        }
        Filter filter = new Filter(allowedHeaders, allowedMethods);
        router.route("/*").handler(filter);
        
        // It enables the reading of the request body for any kind of URIs.
        AccessLog accessLog = config.getAccessLog();
        
        if (accessLog != null) {
            String[] routes = null;
            
            if (accessLog.getRoute() != null) {
                routes = accessLog.getRoute().split(",");
                for (int i = 0; i < routes.length; i ++) {
                    routes[i] = routes[i].trim().replace("{CTX_ROOT}", config.getContextRoot());
                }
            }
            if (accessLog.getLogFormat() != null) {
                LoggerFormatter format = ObjectCreator.create(accessLog.getLogFormat());
                if (routes != null) {
                    for (String uri : routes) {
                        router.route(uri + "/*")
                                .handler(LoggerHandler
                                .create(LoggerFormat.CUSTOM).customFormatter(format));
                    }
                }
                else {
                    router.route(config.getContextRoot() + "/*")
                            .handler(LoggerHandler
                            .create(LoggerFormat.CUSTOM).customFormatter(format));
                }
            }
            else {
                if (routes != null) {
                    for (String uri : routes) {
                        router.route(uri + "/*")
                                .handler(LoggerHandler.create(LoggerFormat.DEFAULT));
                    }
                }
                else {
                    router.route(config.getContextRoot() + "/*")
                            .handler(LoggerHandler.create(LoggerFormat.DEFAULT));
                }
            }
        }
        
        // Add the authentication handler.
        SecurityConstraint secConstraint = config.getSecurityConstraint();
        if (secConstraint != null) {
            List<String> excludes = new ArrayList<>();
            
            AuthConstraint authConstraint = secConstraint.getAuthConstraint();
            if (authConstraint != null && "NO_AUTH".equals(authConstraint.getAuthType())) {
                excludes = authConstraint.getUrlPatterns().getUrlPattern();
                for (int i = 0; i < excludes.size(); i ++) {
                    excludes.set(i, excludes.get(i).trim().replace("{CTX_ROOT}", config.getContextRoot()));
                }
                
            }
            // vertx.auth.provider
            Handler<RoutingContext> handler = null;
            
            if (defaultAuthHandler.equals(secConstraint.getAuthHandler())) {
                KeyStoreOptions keyOpts = new KeyStoreOptions();
                JWTOptions jwtOpts = new JWTOptions();
                
                if (config.getKeystoreConfig() != null) {
                    if (config.getKeystoreConfig().getStoreName() != null) {
                        keyOpts.setPath(config.getKeystoreConfig().getStoreName())
                                .setPassword(config.getKeystoreConfig().getStorePassword());
                    }
                }
                if (secConstraint.getJwtOpts() != null) {
                    if (secConstraint.getJwtOpts().getAlgorithm() != null) {
                        jwtOpts.setAlgorithm(secConstraint.getJwtOpts().getAlgorithm());
                    }
                    if (secConstraint.getJwtOpts().getIssuer() != null) {
                        jwtOpts.setIssuer(secConstraint.getJwtOpts().getIssuer());
                    }
                    if (secConstraint.getJwtOpts().getAudience() != null) {
                        jwtOpts.setAudience(Arrays.asList(secConstraint.getJwtOpts().getAudience().split(",")));
                    }
                    jwtOpts.setExpiresInMinutes(secConstraint.getJwtOpts().getExpiry());
                }
                JWTAuth jwtAuth = JWTAuth.create(vertx
                        , new JWTAuthOptions()
                                .setJWTOptions(jwtOpts)
                                .setKeyStore(keyOpts));
                
                handler = ObjectCreator.create(
                        secConstraint.getAuthHandler()
                        , new Class[] {JWTAuth.class, List.class, List.class, String.class}
                        , new Object[] {jwtAuth, null, excludes, ""});
            }
            else {
                handler = ObjectCreator.create(secConstraint.getAuthHandler());
            }
            router.route(config.getContextRoot() + "/*").handler(handler);
        }
        
        preSet(router);
        
        return router;
    }
    
    /**
     * Any handler to be added at the root
     * @param router
     */
    protected void preSet(Router router) {
        Set<String> allowedHeaders = new HashSet<>();
        Set<HttpMethod> allowedMethods = new HashSet<>();
        
        if (config.getCrossOrigin() != null) {
            if (config.getCrossOrigin().getAllowedHeaders() != null) {
                allowedHeaders = config.getCrossOrigin().getAllowedHeaders().getAllowedHeader();
            }
            if (config.getCrossOrigin().getAllowedMethods()!= null) {
                Set<String> set = config.getCrossOrigin().getAllowedMethods().getAllowedMethod();
                for (String m : set) {
                    allowedMethods.add(HttpMethod.valueOf(m));
                }
            }
        }
        router.route("/*").handler(CorsHandler.create()
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods)
                .allowCredentials(true)
                .exposedHeader("*"));
    }
    
    protected Integer overridePort(Integer port) {
        String s = System.getenv("HTTP_PORT");
        if (s == null) {
            s = System.getProperty("http.port");
            if (s == null && port == null) {
                s = String.valueOf(DEFAULT_PORT);
            }
        }
        if (s != null) {
            port = Integer.valueOf(s);
        }
        return port;
    }
    
    /**
     * Return the provider name.
     * 
     * @param name      Provider name to search for
     * @return String   Provider
     */
    String provider(String name) {
        String val = System.getProperty(name, "vertx.auth");
        if (val != null) {
            if (! val.endsWith(".provider")) {
                val += ".provider";
            }
        }
        return val;
    }

    @Override
    public void stop(int delay) {
        internalServer.close();
    }
    
}
