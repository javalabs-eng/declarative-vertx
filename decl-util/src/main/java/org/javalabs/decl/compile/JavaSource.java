package org.javalabs.decl.compile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * A container that stores Java source code as a string in memory.
 * 
 * <p>This class represents a source file (a {@code .java} file) in RAM. 
 * It allows you to feed raw Java code strings directly into the system compiler 
 * without writing text files to your hard drive.</p>
 * 
 * <p>This object is typically created right before compilation. You give it 
 * the full class name and the text code, and then pass it to the 
 * {@code InMemoryCompiler}.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see javax.tools.SimpleJavaFileObject
 * @see <a href="https://oracle.com">Java File Object Content Types</a>
 */
public class JavaSource extends SimpleJavaFileObject {
    
    private final String content;

    public JavaSource(String javaFile, String content) {
        // Creates a URI representing the virtual file path
        super(URI.create("string:///" + javaFile), JavaFileObject.Kind.SOURCE);
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        // This method will be automatically called at the initial stage by the JavaCompiler.
        // Typically, at the time of parsing the source file content. 
        // JavaCompiler.parseFile -> JavaCompiler.parse -> JavaCompiler.readSource
        return content;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        InputStream in = new ByteArrayInputStream(content.getBytes());
        return in;
    }
}
