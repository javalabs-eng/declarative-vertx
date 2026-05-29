package org.javalabs.decl.compile;

import java.util.HashMap;
import java.util.Map;

/**
 * A container that holds references to compiled Java classes in memory.
 * 
 * <p>This class acts as a registry or cache for classes that have been 
 * dynamically compiled at runtime. It stores compiled byte code or loaded 
 * class definitions, making it easy to look up and instantiate dynamic 
 * objects without recompiling them.</p>
 * 
 * <p>This holder is typically populated by the {@code InMemoryCompiler} 
 * or a custom class loader once the compilation process completes successfully.</p>
 * 
 * @author Sudiptasish Chanda
 * @see java.lang.ClassLoader
 * 
 */
public class CompiledClassHolder {
    
    private final Map<String, JavaCompiledClass> compiledClasses = new HashMap<>();

    /**
     * Registers a map of dynamically compiled Java classes into this container or loader.
     * 
     * <p>This method updates the internal cache by taking a map of compiled class structures. 
     * The keys of the map must be the fully qualified names of the classes (such as 
     * {@code "com.example.service.UserService"}), and the values must be the corresponding 
     * {@link JavaCompiledClass} byte content wrappers.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * Map<String, JavaCompiledClass> freshCode = ... // obtained from the InMemoryCompiler
     * myLoader.compiledClasses(freshCode);
     * }</pre>
     *
     * @param compiledClasses a map pairing fully qualified class names with their compiled memory objects, must not be null.
     */
    public void compiledClasses(Map<String, JavaCompiledClass> compiledClasses) {
        this.compiledClasses.putAll(compiledClasses);
    }
    
    /**
     * Return the class file bytes (binary) from the memory store.
     * 
     * @param className The class name whose compiled bytecode is to be returned
     * @return byte[]   The compiled bytecode.
     */
    public byte[] classBytes(String className) {
        JavaCompiledClass compiledClass = compiledClasses.get(className);
        if (compiledClass != null) {
            return compiledClass.compiled();
        }
        return null;
    }
}
