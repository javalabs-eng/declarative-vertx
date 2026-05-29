package org.javalabs.decl.gen;

import org.javalabs.decl.visitor.ClassVisitor;
import org.javalabs.decl.visitor.Element;

/**
 * Representation of a java import statement.
 *
 * @author Sudiptasish Chanda
 */
public class JavaComment implements CodeGenSupport, Element {
    
    private static final String COMMENT_START = "/**";
    private static final String COMMENT_BODY  = " *";
    private static final String COMMENT_END   = " */";
    
    private final JavaClass jClass;
    private String author;
    private String comment;
    
    public JavaComment(JavaClass jClass) {
        this.jClass = jClass;
    }

    public JavaComment author(String author) {
        this.author = author;
        return this;
    }

    public JavaComment comment(String comment) {
        this.comment = comment;
        return this;
    }
    
    @Override
    public String snippet() {
        StringBuilder buff = new StringBuilder(100);
        
        buff.append(jClass.decorator().classIndent())
                .append(COMMENT_START)
                .append(NEW_LINE)
                .append(jClass.decorator().classIndent())
                .append(COMMENT_BODY)
                .append(SPACE).append(comment)
                .append(NEW_LINE)
                .append(jClass.decorator().classIndent())
                .append(COMMENT_BODY)
                .append(NEW_LINE)
                .append(jClass.decorator().classIndent())
                .append(COMMENT_BODY)
                .append(SPACE)
                .append("@author")
                .append(SPACE)
                .append(author)
                .append(NEW_LINE)
                .append(jClass.decorator().classIndent())
                .append(COMMENT_END);
        
        return buff.toString();
    }

    @Override
    public void accept(ClassVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return this.comment;
    }
}
