package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.VertxWeb;
import io.vertx.core.Verticle;

/**
 * Interface for a config parser.
 * 
 * <p>
 * A configuration parser in is a component that reads settings from external configuration files, primarily xml, 
 * enabling applications to change the behavior without recompiling.
 *
 * @author Sudiptasish Chanda
 */
public interface WebConfigParser {
    
    static WebConfigParser parser() {
        return new JAXBWebConfigParser();
    }
    
    /**
     * Read the default configuration file.
     * 
     * <p>
     * vertx-web.xml is the main configuration file for configuring the different {@link Verticle}s.
     * This file has to be placed in the classpath of the application or bundled in the executable jar.
     * The parser class will parse this file and construct the {@link VertxWeb} object that encapsulates
     * the configuration attributes for individual Verticle.
     * 
     * @return VertxWeb     The VertxWeb config object.
     */
    VertxWeb read();
}
