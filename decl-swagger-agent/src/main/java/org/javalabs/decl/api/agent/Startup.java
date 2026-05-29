package org.javalabs.decl.api.agent;

import java.lang.instrument.Instrumentation;

/**
 *
 * @author Sudiptasish Chanda
 */
public interface Startup {
    
    /**
     * 
     * @param _inst
     * @param args 
     */
    void start(Instrumentation _inst, String[] args);
}
