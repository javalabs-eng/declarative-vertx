package org.javalabs.decl.visitor;

/**
 * A class visitor.
 * 
 * <p>
 * ClassVisitor is not thread-safe. One has to call {@link #reset() } method before reusing this visitor.
 *
 * @author Sudiptasish Chanda
 */
public interface ClassVisitor extends Visitor {
    
    /**
     * Reset the current state of this visitor.
     */
    void reset();
}
