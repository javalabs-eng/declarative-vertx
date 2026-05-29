package org.javalabs.decl.util;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Abstract provider that uses java's {@link ServiceLoader} capability to load the providers.
 *
 * @author Sudiptasish Chanda
 */
public abstract class PlatformProvider {
    
    private final String name;
    private final Integer order;
    
    protected PlatformProvider(String name, Integer order) {
        this.name = name;
        this.order = order;
    }
    
    /**
     * Return the scm provider name.
     * @return String
     */
    public String name() {
        return this.name;
    }
    
    /**
     * Return the order of this provider.
     * @return String
     */
    public Integer order() {
        return this.order;
    }
    
    /**
     * Return the provider implementation.
     * 
     * @param name  The provider name, useful when you get choose from a list of providers.
     * @return PlatformProvider
     */
    public static PlatformProvider get(String name) {
        Class<?> provider = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        return get(name, provider);
    }
    
    /**
     * Return the provider implementation whose name matches with the name provided.
     * 
     * <p>
     * If the argument <code>name</code> is null or empty, then the first provider
     * will be returned.
     * 
     * @param name      Name of the provider.
     * @param provider  The provider class to be loaded.
     * 
     * @return PlatformProvider
     */
    public static PlatformProvider get(String name, Class<?> provider) {
        List<PlatformProvider> providers = new ArrayList<>(2);
        
        synchronized (PlatformProvider.class) {
            List<PlatformProvider> list = tryLoad(provider, Boolean.TRUE);
            providers.addAll(list);
        }
        
        if (! providers.isEmpty() && (name == null || name.trim().length() == 0)) {
            // Order them and return the very first provider.
            Collections.sort(providers, (PlatformProvider o1, PlatformProvider o2) -> o1.order() - o2.order());
            return providers.get(0);
        }
        for (PlatformProvider pp : providers) {
            if (name.equals(pp.name())) {
                return pp;
            }
        }
        // if (name == null || name.length() == 0) {
        //     throw new RuntimeException("No default provider found for " + provider.getName());
        // }
        // Purposely returning null, so that caller can choose to use a default provider.
        return null;
    }
    
    /**
     * Load the available providers.
     * 
     * <p>
     * We use ServiceLoader as we want your program to have a “plugin” functionality. It will allow team
     * to customize your application by adding jar files to the classpath which contain implementations
     * of a specific subset of functionality, you can use a ServiceLoader to look for those
     * implementations in the classpath.
     * 
     * <p>
     * The algorithm used to locate the provider subclass to use consists
     * of the following steps:
     * 
     * <ul>
     *   <li>
     *     If a resource with the name of
     *     <code>META-INF/services/org.javalabs.kv.spi.ClientProvider</code>
     *     exists, then its first line, if present, is used as the UTF-8 encoded
     *     name of the implementation class.
     *   </li>
     *   <li>
     *     If a system property with the name <code>io.opns.otl.agent.provier.Provider</code>
     *     is defined, then its value is used as the name of the implementation class.
     *   </li>
     *   <li>
     *     Finally, a default implementation class name is used.
     *   </li>
     * </ul>
     *
     * @param provider          The provider class to be loaded.
     * @param refresh   Specify whether a refresh is required.
     *                  If true, then the service loader will try loading the
     *                  the available provider factories again.
     * @return Provider
     */
    private static List<PlatformProvider> tryLoad(Class<?> provider, boolean refresh) {
        ServiceLoader<PlatformProvider> loader = (ServiceLoader<PlatformProvider>)ServiceLoader.load(provider);
        if (refresh) {
            loader.reload();
        }
        List<PlatformProvider> providers = new ArrayList<>(1);
        for (PlatformProvider pp : loader) {
            providers.add(pp);
        }
        return providers;
    }
}
