package org.javalabs.decl.container.spi;

/**
 * Interface to represent a http web server.
 * 
 * <p>
 * A web server is a runtime that accepts requests via HTTP or its secure variant HTTPS.
 * On the software side, a web server includes several parts that control how web users access
 * hosted files. At a minimum, this is an HTTP server. An HTTP server is software that understands
 * URLs (web addresses) and HTTP (the protocol your browser uses to view webpages).
 * An HTTP server can be accessed through the domain names of the websites it stores, and it
 * delivers the content of these hosted websites to the end user's device.
 *
 * @author Sudiptasish Chanda
 */
public interface EmbeddedHttpServer {
    
    /**
     * Starts this server.
     * @param arg
     */
    void start(Object arg);
    
    /**
     * Stops this server by closing the listening socket and disallowing
     * any new exchanges from being processed. 
     * 
     * <p>
     * The method will then block until all current exchange handlers have completed or else when
     * approximately <i>delay</i> seconds have elapsed (whichever happens sooner).
     * Then, all open TCP connections are closed, the background thread created by start() exits,
     * and the method returns. Once stopped, a HttpServer cannot be re-used.
     *
     * @param delay the maximum time in seconds to wait until exchanges have finished.
     * @throws IllegalArgumentException if delay is less than zero.
     */
    void stop(int delay);
}
