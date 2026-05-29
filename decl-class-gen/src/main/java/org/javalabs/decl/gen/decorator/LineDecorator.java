package org.javalabs.decl.gen.decorator;

/**
 *
 * @author Sudiptasish Chanda
 */
public class LineDecorator {
    
    private int depth = 0;
    
    public LineDecorator() {
        this(0);
    }
    
    public LineDecorator(int depth) {
        this.depth = depth;
    }
    
    public int depth() {
        return depth;
    }
    
    public String classIndent() {
        return indent(this.depth);
    }
    
    public String variableIndent() {
        return indent(this.depth + 1);
    }
    
    public String constructorIndent() {
        return indent(this.depth + 1);
    }
    
    public String methodIndent() {
        return indent(this.depth + 1);
    }
    
    public String bodyIndent() {
        return indent(this.depth + 2);
    }
    
    public String bodyConditionalIndent() {
        return indent(this.depth + 3);
    }
    
    public String indent(int depth) {
        if (depth == 0) {
            return "";
        }
        char[] indent = new char[depth * 4];
        for (int i = 0; i < indent.length; i ++) {
            indent[i] = ' ';
        }
        return new String(indent);
    }
}
