package org.javalabs.decl.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * A container that stores compiled Java byte code in memory.
 * 
 * <p>This class represents a compiled Java class file in RAM. It avoids 
 * the need to write {@code .class} files to disk during runtime compilation. 
 * It holds the compiled byte code inside a byte array stream, making it 
 * easy for custom class loaders to access and load the class.</p>
 * 
 * <p>This object is typically created by an in-memory file manager when 
 * the Java compiler outputs byte code during runtime compilation.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see javax.tools.SimpleJavaFileObject
 * @see <a href="https://oracle.com">Java File Object API</a>
 */
public class JavaCompiledClass extends SimpleJavaFileObject {
    
    private final String name;
    private CompiledOutputStream out;
    
    private byte[] compiled;

    public JavaCompiledClass(String name) {
        super(URI.create("string:///" + name), Kind.CLASS);
        this.name = name;
    }
    
    /**
     * Return the compiled bytes of this class.
     * @return byte[]   Raw byte array
     */
    public byte[] compiled() {
        return compiled;
    }
    
    /**
     * Set the compiled class file bytes.
     * @param compiled  Compiled class bytes.
     */
    void compiled(byte[] compiled) {
        this.compiled = compiled;
    }

    @Override
    public OutputStream openOutputStream() {
        // At the time of writing the compiled (generated) class bytes, this method will be called.
        // The call is originated from InMemoryFileManager.getJavaFileForOutput()
        if (out == null) {
            out = new CompiledOutputStream(this);
        }
        return out;
    }
    
    /**
     * The output stream associated with the compiled class.
     */
    public static class CompiledOutputStream extends ByteArrayOutputStream {
        
        private final JavaCompiledClass jc;
        
        public CompiledOutputStream(JavaCompiledClass jc) {
            super();
            this.jc = jc;
        }

        @Override
        public void close() throws IOException {
            // Once the entire compiled class bytes have been written to the OutputStream "out",
            // The close method will be called.
            // The compiled class bytes is preserved in the private byte array variable.
            super.close();
            jc.compiled(super.toByteArray());
        }
    }
}
