package org.javalabs.decl.container;

import java.util.ArrayList;
import java.util.List;

/**
 * Container configuration object.
 * 
 * <p>
 * Application can leverage the instance of this class to pass initialization or other tuning parameters
 * to the underlying container. A container configuration file defines the settings and specifications for 
 * a container, allowing for consistent and repeatable deployments. These files, often in YAML or JSON format,
 * determine aspects like environment variables, network settings, and volume mounts.
 * 
 * <p>
 * Currently the container configuration is not being utilized. However, it is kept for future provision.
 *
 * @author Sudiptasish Chanda
 */
public class ContainerConfig {
    
    private final List<ConfigFile> files = new ArrayList<>();
    
    /**
     * Add the set of files that define the container's behavior.
     * 
     * <p>
     * Refer to {@link ConfigFile} to learn more about container configuration file format.
     * 
     * @param file 
     */
    public void addConfig(ConfigFile file) {
        files.add(file);
    }
    
    /**
     * Return the configuration file(s) that define the container's behavior.
     * @return List
     */
    public List<ConfigFile> files() {
        return files;
    }
}
