package org.javalabs.decl.container;

/**
 * The deployment container.
 * 
 * <p>
 * Containers are the interface between a component and the low-level platform-specific functionality
 * that supports the component. J2EE/Java EE applications aren't self contained. In order to be executed,
 * they need to be deployed in a container. In other words, the container provides an execution environment
 * on top of the JVM.
 * 
 * <p>
 * It provides run time support for J2EE application components. J2EE application components use the protocols
 * and methods of the container to access other application components and services provided by the server.
 *
 * @author Sudiptasish Chanda
 */
public interface Container {
    
    /**
     * Deploy the application.
     * 
     * <p>
     * Software deployment is all of the activities that make a software system available for use.
     * The general deployment process consists of several interrelated activities with possible transitions
     * between them.
     * 
     * @param config    Container configuration.
     */
    void setup(ContainerConfig config);
    
    /**
     * Stop the container.
     * 
     * <p>
     * Stopping the container will undeploy the application and all it's components. It will also release
     * any resources once held by the container.
     */
    void destroy();
}
