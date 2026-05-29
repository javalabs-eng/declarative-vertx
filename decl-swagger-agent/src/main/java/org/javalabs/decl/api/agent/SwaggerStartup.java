package org.javalabs.decl.api.agent;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.io.File;
import org.javalabs.decl.api.project.TechStack;

/**
 * Swagger startup class.
 *
 * @author Sudiptasish Chanda
 */
public class SwaggerStartup implements Startup {
    
    public SwaggerStartup() {}

    @Override
    public void start(Instrumentation _inst, String[] args) {
        SwaggerAgent agent = null;
        
        TechStack stack = null;        
        String packageName = "";
        String outFile = null;
        
        for (int i = 0; i < args.length; i ++) {
            if (args[i].equals("-k")) {
                packageName = args[i + 1];
            }
            else if (args[i].equals("-o")) {
                outFile = args[i + 1];
            }
            else if (args[i].equals("-t")) {
                try {
                    stack = Enum.valueOf(TechStack.class, args[i + 1].toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid tech-stack " + args[i + 1]
                            + ". Valid values are: " + Arrays.toString(TechStack.values()));
                }
            }
        }
        if (stack == null) {
            throw new IllegalArgumentException("Must specify the tech stack: " + Arrays.toString(TechStack.values()));
        }
        if (packageName == null) {
            throw new IllegalArgumentException("Must specify the java package name");
        }
        if (outFile == null) {
            outFile = System.getProperty("user.dir") + File.separator + "openapi.yaml";
        }
        // Choose the agent and start crawling ...
        agent = lookup(stack);
        agent.crawl(packageName, outFile);
    }
    
    private SwaggerAgent lookup(TechStack stack) {
        if (stack == TechStack.SPRING) {
            return new SpringSwaggerAgent();
        }
        else if (stack == TechStack.VERTX) {
            return new VertxSwaggerAgent();
        }
        else {
            return new JaxRsSwaggerAgent();
        }
    }
}
