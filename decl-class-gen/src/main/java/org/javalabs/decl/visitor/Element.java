package org.javalabs.decl.visitor;

/**
 * A visitable class.
 * 
 * <p>
 * A Visitor design pattern consists of several key components that work together to enable its functionality.
 * This interface defines an accept method that takes a visitor as an argument. This method allows the visitor
 * to visit the concrete elements.
 * 
 * <p>
 * Concrete classes implement the Element interface and represent the various types of objects in the structure.
 * Each concrete element defines how it accepts a visitor by calling the corresponding method on the visitor.
 *
 * @author Sudiptasish Chanda
 */
public interface Element {
    
    /**
     * This method accepts a {@link Visitor} to perform the required operation.
     * @param visitor   A class visitor.
     */
    void accept(ClassVisitor visitor);
}
