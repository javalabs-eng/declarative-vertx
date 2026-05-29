package org.javalabs.decl.visitor;

/**
 * Visitor interface.
 * 
 * <p>
 * Visitor pattern is used when we have to perform an operation on a group of similar kind of Objects. 
 * With the help of visitor pattern, we can move the operational logic from the objects to another class.
 * 
 * <p>
 * The Visitor design pattern is a behavioral pattern that allows you to add new operations to a group of
 * related classes without modifying their structures. It is particularly useful when you have a stable
 * set of classes but need to perform various operations on them, making it easy to extend functionality
 * without altering the existing codebase.
 *
 * @author Sudiptasish Chanda
 */
public interface Visitor {
    
    /**
     * The conventional visit method exposed by this visitor.
     * 
     * <p>
     * It will visit the {@link Element} and perform necessary operation.
     * 
     * @param element 
     */
    void visit(Element element);
}
