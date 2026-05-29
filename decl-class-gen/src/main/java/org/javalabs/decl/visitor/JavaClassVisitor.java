package org.javalabs.decl.visitor;

import org.javalabs.decl.gen.CodeGenSupport;

/**
 * A concrete visitor class.
 *
 * @author Sudiptasish Chanda
 */
public class JavaClassVisitor implements ClassVisitor {
    
    private final StringBuilder buff = new StringBuilder(4096);

    @Override
    public void visit(Element element) {
        if (element instanceof CodeGenSupport) {
            String s = ((CodeGenSupport)element).snippet();
            buff.append(s);
        }
    }
    
    public String result() {
        return buff.toString();
    }

    @Override
    public void reset() {
        buff.delete(0, buff.length());
    }
}
