package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.RoutingConfig;

/**
 * Interface for a routing config parser.
 * 
 * <p>
 * A configuration parser in is a component that reads settings from external configuration files, primarily xml, 
 * enabling applications to change the behavior without recompiling.
 *
 * @author Sudiptasish Chanda
 */
public interface RoutingConfigParser {
    
    static RoutingConfigParser parser() {
        return new JAXBRoutingConfigParser();
    }
    
    /**
     * Read the default routing configuration file, routing-config.xml.
     * See {@link #read(java.lang.String) }
     * 
     * @return RoutingConfig
     */
    RoutingConfig read();
    
    /**
     * Read the in-memory routing configuration.
     * See {@link #read(java.lang.String) } for more details.
     * 
     * @param blob
     * @return RoutingConfig
     */
    RoutingConfig read(byte[] blob);
    
    /**
     * Read the custom routing file.
     * 
     * <p>
     * The custom routing config file is the main configuration file for initializing the routes of the embedded Vert.x http server.
     * This file has to be placed in the classpath of the application or bundled in the executable jar.
     * The parser class will parse this file and construct the {@link RoutingConfig} object that encapsulates
     * the routing related attributes for the http server.
     * 
     * @param routingFile
     * @return RoutingConfig
     */
    RoutingConfig read(String routingFile);
}
