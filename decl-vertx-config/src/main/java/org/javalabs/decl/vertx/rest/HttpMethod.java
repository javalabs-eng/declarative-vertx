package org.javalabs.decl.vertx.rest;

/**
 * Set of http methods.
 * 
 * <p>
 * HTTP defines a set of request methods to indicate the purpose of the request and what is expected if the
 * request is successful. Although they can also be nouns, these request methods are sometimes referred to
 * as HTTP verbs. Each request method has its own semantics, but some characteristics are shared across multiple
 * methods, specifically request methods can be safe, idempotent, or cacheable.
 *
 * @author Sudiptasish Chanda
 */
public enum HttpMethod {
    
    /**
     * GET Method.
     * 
     * <p>
     * The GET method requests a representation of the specified resource.
     * Requests using GET should only retrieve data and should not contain a request content.
     */
    GET,
    
    /**
     * POST Method.
     * 
     * <p>
     * The POST method submits an entity to the specified resource, 
     * often causing a change in state or side effects on the server.
     */
    POST,
    
    /**
     * PUT Method.
     * 
     * <p>
     * The PUT method replaces all current representations of the target resource with the request content.
     */
    PUT,
    
    /**
     * PATCH Method.
     * 
     * <p>
     * Similar to PUT method. The PATCH method applies partial modifications to a resource.
     */
    PATCH,
    
    /**
     * DELETE Method.
     * 
     * <p>
     * The DELETE method deletes the specified resource.
     */
    DELETE,
    
    /**
     * HEAD Method.
     * 
     * <p>
     * The HEAD method asks for a response identical to a GET request, but without a response body.
     */
    HEAD,
    
    /**
     * OPTIONS Method.
     * 
     * The OPTIONS method describes the communication options for the target resource.
     */
    OPTIONS,
    
    /**
     * TRACE Method.
     * 
     * <p>
     * The TRACE method performs a message loop-back test along the path to the target resource.
     */
    TRACE,
    
    /**
     * CONNECT Method.
     * 
     * <p>
     * The CONNECT method establishes a tunnel to the server identified by the target resource.
     */
    CONNECT
}
