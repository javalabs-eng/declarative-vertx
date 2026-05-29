package org.javalabs.decl.api.agent;

/**
 * Swagger agent.
 *
 * @author Sudiptasish Chanda
 */
public abstract class SwaggerAgent {
    
    public abstract void crawl(String packageName, String outputFile);
}
