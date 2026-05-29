package org.javalabs.decl.vertx.config.internal;

import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import org.javalabs.decl.vertx.jaxb.VertxWeb;
import org.javalabs.decl.vertx.jaxb.WebServerConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for keeping the platform/server configuration in the memory.
 *
 * @author Sudiptasish Chanda
 */
public final class ConfigStorage {
    
    private static final ConfigStorage INSTANCE = new ConfigStorage();
    
    private final Map<String, Object> configMapping = new HashMap<>();
    
    private ConfigStorage() {}
    
    public static ConfigStorage get() {
        return INSTANCE;
    }
    
    /**
     * Store the xml configuration object after reading the relevant xml files.
     * 
     * <p>
     * This object is created by reading the <code>routing-config.xml</code> file, which defines
     * all the standard http routes and contracts. Since there is a single xml file, hence there
     * will always be a single instance of {@link RoutingConfig} object.
     * 
     * @param xmlConfig    RoutingConfig object to be stored.
     */
    public void store(Object xmlConfig) {
        if (xmlConfig instanceof VertxWeb) {
            configMapping.put("vertx-web.xml", xmlConfig);
        }
        else if (xmlConfig instanceof WebServerConfig) {
            configMapping.put("server.xml", xmlConfig);
        }
        else if (xmlConfig instanceof RoutingConfig) {
            configMapping.put("routing-config.xml", xmlConfig);
        }
    }
    
    public VertxWeb vertxConfig() {
        return (VertxWeb)configMapping.get("vertx-web.xml");
    }
    
    public WebServerConfig serverConfig() {
        return (WebServerConfig)configMapping.get("server.xml");
    }
    
    public RoutingConfig routingConfig() {
        return (RoutingConfig)configMapping.get("routing-config.xml");
    }
    
    public Object xmlConfig(String xmlFile) {
        return configMapping.get(xmlFile);
    }
    
    public String keystoreFile() {
        WebServerConfig config = (WebServerConfig)configMapping.get("server.xml");
        if (config.getKeystoreConfig() != null) {
            return config.getKeystoreConfig().getStoreName();
        }
        return null;
    }
    
    public String keystorePassword() {
        WebServerConfig config = (WebServerConfig)configMapping.get("server.xml");
        if (config.getKeystoreConfig() != null) {
            return config.getKeystoreConfig().getStorePassword();
        }
        return null;
    }
    
    public String jwtAlgo() {
        WebServerConfig config = (WebServerConfig)configMapping.get("server.xml");
        if (config.getSecurityConstraint() != null && config.getSecurityConstraint().getJwtOpts() != null) {
            return config.getSecurityConstraint().getJwtOpts().getAlgorithm();
        }
        return null;
    }
    
    public String jwtIssuer() {
        WebServerConfig config = (WebServerConfig)configMapping.get("server.xml");
        if (config.getSecurityConstraint() != null && config.getSecurityConstraint().getJwtOpts() != null) {
            return config.getSecurityConstraint().getJwtOpts().getIssuer();
        }
        return null;
    }
    
    public String jwtAudience() {
        WebServerConfig config = (WebServerConfig)configMapping.get("server.xml");
        if (config.getSecurityConstraint() != null && config.getSecurityConstraint().getJwtOpts() != null) {
            return config.getSecurityConstraint().getJwtOpts().getAudience();
        }
        return null;
    }
    
    public Integer jwtExpiry() {
        WebServerConfig config = (WebServerConfig)configMapping.get("server.xml");
        if (config.getSecurityConstraint() != null && config.getSecurityConstraint().getJwtOpts() != null) {
            return config.getSecurityConstraint().getJwtOpts().getExpiry();
        }
        return null;
    }
}
