package org.javalabs.decl.gen;

/**
 * Interface that supports code generation.
 *
 * @author Sudiptasish Chanda
 */
public interface CodeGenSupport {
    
    String PUBLIC = "public";
    String VOID = "void";
    String NEW_LINE = "\n";
    String SPACE = " ";
    String ANN = "@";
    String NOT = "!";
    String EQUALS = "=";;
    String TAB = "    ";
    String SEMICOLON = ";";
    String COLON = ":";
    String COMMA = ",";
    String STOP = ".";
    String NULL = "null";
    
    /**
     * Method that generate the code snippet of a given component.
     * @return String
     */
    String snippet();
}
