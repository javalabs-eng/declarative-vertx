package org.javalabs.decl.container.spi;

import org.javalabs.decl.util.PlatformProvider;

/**
 * Provider class for http server.
 * 
 * <p>
 * Service provider class for {@link EmbeddedHttpServer}. Sub-classes of {@link HttpServerProvider}
 * provide an implementation of {@link EmbeddedHttpServer} and associated classes. Applications do 
 * not normally use this class. See {@link #get()} for how providers are found and loaded.
 *
 * @author Sudiptasish Chanda
 */
public abstract class HttpServerProvider extends PlatformProvider {
    
    protected HttpServerProvider(String name) {
        this(name, 0);
    }
    
    protected HttpServerProvider(String name, Integer order) {
        super(name, order);
    }
    
    /**
     * Return the default platform provider.
     * 
     * <p>
     * See {@link #get(java.lang.String) } for more details.
     * 
     * @return HttpServerProvider   The system-wide default HttpServerProvider
     */
    public static HttpServerProvider get() {
        HttpServerProvider provider = HttpServerProvider.get(null);
        return provider;
    }
    
    /**
     * Returns the system wide default HttpServerProvider for this invocation of the JVM.
     * 
     * <p>The first invocation of this method locates the default provider object as follows:
     * 
     * 1. If the system property <code>org.javalabs.decl.container.spi.HttpServerProvider</code> is defined 
     *    then it is taken to be the fully-qualified name of a concrete provider class. The class is 
     *    loaded and instantiated; if this process fails then an unspecified unchecked error or exception 
     *    is thrown.
     * 
     * 2. If a provider class has been installed in a jar file that is visible to the system class loader, 
     *    and that jar file contains a provider-configuration file named org.javalabs.decl.container.spi.HttpServerProvider
     *    in the resource directory META-INF/services, then the first class name specified in that file is taken. 
     *    The class is loaded and instantiated; if this process fails then an unspecified unchecked error 
     *    or exception is thrown.
     * 
     * 3. Finally, if no provider has been specified by any of the above means then the system-default 
     *    provider class is instantiated and the result is returned.
     * 
     * Subsequent invocations of this method return the provider that was returned by the first invocation.
     * 
     * @param name  Name of specific service provider.
     * @return KVStoreProvider  The system-wide default HttpServerProvider
     */
    public static HttpServerProvider get(String name) {
        return (HttpServerProvider)PlatformProvider.get(name);
    }
    
    /**
     * Create a new instance of {@link EmbeddedHttpServer}.
     * 
     * @param configFile    The optional configuration file that the http server may require.
     *                      If no configuration file was supplied, then the default config
     *                      file <code>server.xml</code> will be used.
     * @return EmbeddedHttpServer
     */
    public abstract EmbeddedHttpServer create(String configFile);
}
