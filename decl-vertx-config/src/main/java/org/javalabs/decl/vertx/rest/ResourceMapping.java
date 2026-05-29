package org.javalabs.decl.vertx.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * It indicates that a class is a RESTful handler, and its methods will automatically serialize
 * return objects (e.g., to JSON or XML) directly into the HTTP response body.
 * 
 * <p>
 * This annotation is used to map HTTP requests to handler methods in a controller. 
 * It can be applied at the class level to define a base URI for all methods within that controller.
 *
 * <p>
 * Paths are relative. For an annotated class the base URI is the context path
 * Classes and methods may also be annotated with {@link Mapping#consume() } and {@link Mapping#produce() } to filter the requests 
 * they will receive.
 *
 * @author Sudiptasish Chanda
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceMapping {
    
    /**
     * Define the name.
     * @return String
     */
    String name();
    
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
    String path();
    
    /**
     * Indicate whether a failure callback will be associated to it.
     * Possible values: "true" | "false"
     * 
     * @return String
     */
    String failure() default "false";
}
