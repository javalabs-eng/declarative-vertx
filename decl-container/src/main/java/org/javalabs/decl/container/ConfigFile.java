package org.javalabs.decl.container;

/**
 * Configuration file for starting up the container.
 * 
 * <p>
 * The configuration file of a container has the following kep aspects:
 * 
 * <ul>
 *   <li>URL Mapping - It defines how incoming URLs are mapped to specific handlers or other 
 *       resources within the vert.x application.</li>
 *   <li>Access Control - Configuration files can define security constraints, specifying which 
 *       users or roles have access to specific resources.</li>
 *   <li>Resource Allocation - The configuration can control resource allocation like thread pools 
 *       and connection pools for optimal performance.</li>
 *   <li>Context Parameters - Parameters defined in the configuration file can be used by the vert.x
 *       application for various purposes, such as setting database connection details or other 
 *       environment-specific settings.</li>
 *   <li>Custom Properties - Some containers allow defining custom properties to configure specific 
 *       aspects of the container's behavior beyond the standard settings.</li>
 * </ul>
 *
 * @author Sudiptasish Chanda
 */
public class ConfigFile {
    
    private final String file;
    private final Integer order;

    public ConfigFile(String file, Integer order) {
        this.file = file;
        this.order = order;
    }

    /**
     * Return the name of the configuration file.
     * @return String
     */
    public String file() {
        return file;
    }

    /**
     * Return the file reading order.
     * 
     * <p>
     * If there are multiple configuration files, user can optionally define the order in which the
     * individual configuration file will be read. Otherwise the files are picked in their order of
     * addition.
     * 
     * @return Integer
     */
    public Integer order() {
        return order;
    }
}
