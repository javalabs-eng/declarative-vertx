package org.javalabs.decl.vertx.container;

import org.javalabs.decl.container.Container;
import org.javalabs.decl.container.ContainerConfig;

/**
 * Entry point for starting a declarative vertx container.
 * 
 * <p>
 * A declarative method to start a container focuses on describing what the container should do, 
 * rather than specifying how it should be done. This approach is common in configuration files 
 * or domain-specific languages where you declare the desired state of the server, and this framework 
 * handles the underlying implementation details.
 * 
 * <p>
 * Here are the benefits that it offers:
 * 
 * <ul>
 *   <li>Reduced Boilerplate: You write less code to achieve the same result.</li>
 *   <li>Improved Readability: Declarative code can be easier to understand and maintain, as it focuses on the what rather than the how.</li>
 *   <li>Flexibility: Frameworks can handle different deployment scenarios and optimizations automatically based on the declarative configuration.</li>
 * </ul>
 * 
 * <p>
 * In essence, the declarative approach to starting an HTTP server allows you to define the server's 
 * behavior through configuration, rather than by writing low-level code. This leads to more concise, 
 * maintainable, and potentially more flexible server setups, often leveraging the power of frameworks 
 * and tools that handle the implementation details.
 *
 * @author Sudiptasish Chanda
 */
public class DeclarativeVertx {
 
    public static void start() {
        Container container = (Container)ContainerProxy.newInstance(new StandardContainer());
        container.setup(new ContainerConfig());
    }
}
