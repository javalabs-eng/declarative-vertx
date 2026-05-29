package org.javalabs.decl.workflow;

import java.util.List;

/**
 * A Catalog acts as a registry to group multiple {@link Command}s together.
 * 
 * <p>
 * You can think of catalog as a registry to store various commands. The 
 * {@link WorkflowEngine} before starting a chain, will query the the catalog to
 * retrieve the relevant commands and assemble them together in the {@link Chain}
 * before kick-starting the flow.
 *
 * @author Sudiptasish Chanda
 */
public interface Catalog {
    
    /**
     * Add a new chain to the catalog.
     * @param chain   Chain to be added.
     */
    void add(Chain chain);
    
    /**
     * Return the command by it's name.
     * @param name
     * @return Chain
     */
    Chain get(String name);
    
    /**
     * Get all workflow chains registered with the catalog.
     * @return List
     */
    List<Chain> getAll();
    
    /**
     * Clear this catalog.
     */
    void clear();
}
