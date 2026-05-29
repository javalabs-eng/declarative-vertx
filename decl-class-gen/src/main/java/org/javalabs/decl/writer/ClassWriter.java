package org.javalabs.decl.writer;

import org.javalabs.decl.util.ConsoleWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.visitor.JavaClassVisitor;

/**
 * Java file writer class.
 *
 * @author Sudiptasish Chanda
 */
public class ClassWriter {
    
    public static void write(String destDir, List<JavaClass> classes, String extn) {
        write(destDir, classes, extn, 2);
    }
    
    public static void write(String destDir, List<JavaClass> classes, String extn, Integer verbose) {
        JavaClassVisitor visitor = new JavaClassVisitor();

        for (JavaClass cl : classes) {
            cl.accept(visitor);

            // Check if the package/directory exists.
            File file = new File(destDir);
            if (! file.exists()) {
                file.mkdirs();
            }
            // Generate and write the java file
            File javaFile = new File(file, cl.name() + extn);
            write(javaFile, visitor.result(), verbose);
            
            visitor.reset();
        }
    }
    
    public static void write(File file, String content) {
        write(file, content, 2);
    }
    
    public static void write(File file, String content, Integer verbose) {
        try {
            if (verbose == 1) {
                // Lowest level trace logging
                ConsoleWriter.timingPrintln(content);
            }
            try (RandomAccessFile writer = new RandomAccessFile(file, "rw");
                FileChannel channel = writer.getChannel()) {  
                channel.write(ByteBuffer.wrap(content.getBytes()));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
