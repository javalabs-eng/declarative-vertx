package org.javalabs.decl.vertx.container;

import io.vertx.core.json.Json;
import org.javalabs.decl.container.spi.EmbeddedHttpServer;
import org.javalabs.decl.container.spi.HttpServerProvider;
import org.javalabs.decl.vertx.config.parser.ServerConfigParser;
import org.javalabs.decl.vertx.jaxb.WebServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider class for Vert.x http server.
 * 
 * <p>
 * A specific implementation of the SPI. The Service Provider contains one or more concrete classes that 
 * extend the {@link HttpServerProvider}. It is configured and identified through a provider configuration 
 * file which we put in the resource directory META-INF/services. The file name is the fully-qualified name
 * of the SPI and its content is the fully-qualified name of the SPI implementation.
 * 
 * <p>
 * This provider implements the {@link #create(java.lang.String) } method and returns a full-fledged
 * Vert.x http server.
 *
 * @author Sudiptasish Chanda
 */
public class VertxHttpServerProvider extends HttpServerProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxHttpServerProvider.class);
    
    private final ServerConfigParser parser = ServerConfigParser.parser();
    
    public VertxHttpServerProvider() {
        super("vertx.http.server.provider", 1);
    }

    @Override
    public EmbeddedHttpServer create(String configFile) {
        WebServerConfig httpConfig = null;
        
        // If a custom server configuration file is passed, then read that file.
        if (configFile != null && configFile.trim().length() > 1) {
            httpConfig = parser.read(configFile);
        }
        else {
            httpConfig = parser.read();
        }
        // ConfigStorage.get().store(httpConfig);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Read default server configuration file");
            LOGGER.info("Configurations:\n" + Json.encodePrettily(httpConfig));
        }
        return new VertxHttpServer(httpConfig);
    }
}
