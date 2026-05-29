package org.javalabs.decl.vertx.container;

/**
 * The standard container for vert.x server deployment.
 * 
 * <p>
 * A standard vert.x container is a runtime environment for vert.x applications that provides services 
 * like life cycle management, security, and resource pooling, but is tailored to specific needs beyond 
 * what standard containers offer. Developers create custom containers to control how applications are 
 * deployed, managed, and accessed, often for specialized requirements or performance optimizations.
 * 
 * <p>
 * The advantage of {@link StandardContainer} is that it is designed for high-performance scenarios, 
 * optimizing resource allocation and minimizing overhead for specific workloads. It can also 
 * facilitate integration with legacy systems by providing custom interfaces and data access mechanisms.
 * 
 * <p>
 * If we are to move away from Vert.x and use a different deployment runtime, it can easily be achieved
 * by implementing {@link org.javalabs.decl.container.Container} interface.
 *
 * @author Sudiptasish Chanda
 */
public class StandardContainer extends VertxContainer {
    
    public StandardContainer() {}

    @Override
    protected void preDeploy() {
        // Add pre initialization code for vertx.
    }

    @Override
    protected void postDeploy() {
        // Add post deployment code for vertx.
    }

    
}
