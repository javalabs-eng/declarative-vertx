package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.WebServerConfig;

/**
 * Interface for a server config parser.
 * 
 * <p>
 * A configuration parser in is a component that reads settings from external configuration files, primarily xml, 
 * enabling applications to change the behavior without recompiling.
 *
 * @author Sudiptasish Chanda
 */
public interface ServerConfigParser {
    
    static ServerConfigParser parser() {
        return new JAXBServerConfigParser();
    }
    
    /**
     * Read the default http server configuration file.
     * 
     * <p>
     * <code>server.xml</code> is the main configuration file for initializing the embedded Vert.x http server.
     * This file has to be placed in the classpath of the application or bundled in the executable jar.
     * The parser class will parse this file and construct the {@link WebServerConfig} object that encapsulates
     * the many configuration attributes for the http server.
     * 
     * @return WebServerConfig     The ServerConfig config object.
     */
    WebServerConfig read();
    
    /**
     * Read the custom http server configuration file.
     * 
     * <p>
     * Same as {@link #read() }. Only thing is instead of reading the default <code>server.xml</code>, the
     * server is initialized with the custom configuration file as specified by <code>configFile</code>.
     * @param configFile
     * 
     * @return WebServerConfig     The ServerConfig config object.
     */
    WebServerConfig read(String configFile);
}
