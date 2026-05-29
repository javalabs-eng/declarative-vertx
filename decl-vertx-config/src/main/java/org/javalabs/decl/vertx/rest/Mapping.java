package org.javalabs.decl.vertx.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the URI path that a resource class or class method will serve requests for.
 * 
 * <p>
 * These are convenience attributes:
 * <ul>
 *  <li>name - The unique name of the mapping.</li>
 *  <li>uri - Binds a URI template variable (e.g., :id in /users/:id) to a method parameter.</li>
 *  <li>method - The http method name. GET, POST, PUT, DELETE, PATCH, etc.</li>
 *  <li>produce - Specify the MIME media types that a resource can produce and send back to the client in the HTTP response.</li>
 *  <li>consume - Specify the MIME media types that a resource should accept.</li>
 * </ul>
 *
 * @author Sudiptasish Chanda
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {
    
    /**
     * Define the name.
     * @return String
     */
    String name() default "";
    
    /**
     * Defines a URI template for the resource class or method, must not include matrix parameters.
     *
     * <p>
     * Embedded template parameters are allowed and are of the form:
     * </p>
     *
     * <pre>
     *  param = "{" *WSP name *WSP [ ":" *WSP regex *WSP ] "}"
     * name = (ALPHA / DIGIT / "_")*(ALPHA / DIGIT / "." / "_" / "-" ) ; \w[\w\.-]*
     * regex = *( nonbrace / "{" *nonbrace "}" ) ; where nonbrace is any char other than "{" and "}"
     * </pre>
     * 
     * @return URI template.
     */
    String uri();
    
    /**
     * Indicate the http method name.
     * Possible 
     * @return String
     */
    HttpMethod method();
    
    /**
     * This value will reflect in the Accept header
     * @return String
     */
    String produce() default "application/json";
    
    /**
     * The content type. The Content-Type header.
     * @return String
     */
    String consume() default "application/json";
}
