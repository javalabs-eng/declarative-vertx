package org.javalabs.decl.compile;

import org.javalabs.decl.util.ConsoleWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * A compiler utility that compiles Java source code using the disk file system.
 * 
 * <p>This class provides methods to compile Java source code by reading and writing 
 * physical files on the hard drive. Unlike in-memory tools, it saves the compiled 
 * {@code .class} files to a specified output folder on the disk. This is useful 
 * for building persistent plugins, generating permanent build artifacts, or debugging 
 * compiler outputs.</p>
 * 
 * <p>Typical workflow usage:</p>
 * <ol>
 *   <li>Specify the input source files or directories on the disk.</li>
 *   <li>Set the target output directory for the compiled class files.</li>
 *   <li>Invoke the compiler task to generate the physical class files.</li>
 * </ol>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see javax.tools.JavaCompiler
 * @see javax.tools.StandardJavaFileManager
 * @see <a href="https://oracle.com">Java Compilation API</a>
 */
public final class DiscCompiler {
    
    /**
     * Generates and compiles a Java class from a source template file.
     * 
     * <p>This method reads a configuration model file, merges it with a preset text 
     * template to create raw Java code, and writes the source file to disk. It then 
     * invokes the compiler to turn that code into a usable {@code .class} file inside 
     * the target output directory.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * compiler.compile("build/generated", "com.example.model", "User", "templates/user_model.json");
     * }</pre>
     *
     * @param generatedDir the physical disk folder path where the source and class files should be saved
     * @param pkgName      the full Java package name for the new class (e.g., "com.example.service")
     * @param className    the name of the Java class to build (e.g., "MyCustomObject")
     * @param modelFile    the disk path to the template or schema definition file used to build the class fields
     * 
     * @throws java.io.IOException if the template file cannot be read or the class cannot be written to disk
     * @throws java.lang.ClassNotFoundException If the required class cannot be loaded.
     * @throws java.lang.RuntimeException if the Java compiler encounters syntax errors during compilation
     * 
     * @see javax.tools.JavaCompiler
     * @see <a href="https://oracle.com">Java Compilation Tool API</a>
     */
    public static void compile(String generatedDir
            , String pkgName
            , String className
            , String modelFile) throws IOException, ClassNotFoundException {
        
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        
        try (StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> unit
                    = manager.getJavaFileObjectsFromFiles(
                            Arrays.asList(new File(modelFile)));
            
            Boolean result = compiler.getTask(null
                    , manager
                    , diagnostics
                    , Arrays.asList("-d", generatedDir)
                    , null
                    , unit).call();
            
            if (result) {
                // Compilation is successful
                File outDir = new File("generated");
                URL[] urls = new URL[1];

                if (! outDir.exists()) {
                    throw new IllegalArgumentException("Generated directory " + outDir + " does not exist");
                }
                urls[0] = outDir.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(loader);
                
                Class<?> clazz = loader.loadClass(pkgName + "." + className);
                ConsoleWriter.timingPrintln("Loaded model class " + clazz.getName() + " into memory");
            }
            else {
                ConsoleWriter.timingPrintln("Unable to load model class " + pkgName + "." + className);
                
                StringBuilder buff = new StringBuilder(64);
                diagnostics.getDiagnostics()
                    .forEach(d -> buff.append(String.valueOf(d)).append("\n"));
                 
                throw new RuntimeException(buff.toString());
            }
        }
    }
}
