package org.javalabs.decl.compile;

/**
 * A custom class loader that loads compiled Java classes directly from memory.
 * 
 * <p>This class loader bypasses the traditional disk file system. It reads 
 * compiled class definitions directly from byte arrays stored in RAM. It is 
 * commonly used alongside an in-memory compiler to instantly load and run 
 * dynamically generated Java code at runtime.</p>
 * 
 * <p>Typical workflow usage:</p>
 * <ol>
 *   <li>The compiler generates class byte code in memory.</li>
 *   <li>The byte code is registered into this loader (or an associated holder).</li>
 *   <li>The loader overrides {@link #findClass(String)} to turn those bytes into a usable {@link java.lang.Class}.</li>
 * </ol>
 * 
 * @author Sudiptasish Chanda
 * @see java.lang.ClassLoader
 * @see <a href="https://oracle.com">Java ClassLoader Documentation</a>
 */
public class MemoryClassLoader extends ClassLoader {
    
    private final CompiledClassHolder holder;

    public MemoryClassLoader(ClassLoader parent, CompiledClassHolder holder) {
        super(parent);
        this.holder = holder;
    }

    @Override
    public Class findClass(String className) throws ClassNotFoundException {
        byte[] buff = loadClassFromArray(className);
        return defineClass(className, buff, 0, buff.length);
    }

    /**
     * Get the compiled raw class bytes from this class name
     * 
     * @param className The java class name
     * @return byte[]   The compiled raw bytes.
     */
    private byte[] loadClassFromArray(String className) {
        byte[] buffer = holder.classBytes(className);
        return buffer;
    }
}
