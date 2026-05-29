package org.javalabs.decl.gen;

import org.javalabs.decl.visitor.ClassVisitor;
import org.javalabs.decl.visitor.Element;

/**
 * Representation of a java package.
 *
 * @author Sudiptasish Chanda
 */
public class JavaPackage implements CodeGenSupport, Element {
    
    private String name;
    
    public JavaPackage() {}

    public JavaPackage(String name) {
        this.name = name;
    }

    public void name(String name) {
        this.name = name;
    }
    
    @Override
    public String snippet() {
        return "package " + name + ";";
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
