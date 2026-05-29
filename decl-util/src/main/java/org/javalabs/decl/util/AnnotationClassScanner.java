package org.javalabs.decl.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class scanner to load all classes with specific annotation.
 *
 * @author Sudiptasish Chanda
 */
public final class AnnotationClassScanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationClassScanner.class);
    
    /**
     * Finds all classes within a specific package that use the given annotation.
     * 
     * <p>This method scans a target package path and looks for classes marked with 
     * the specified annotation type. It is useful for runtime classpath scanning, 
     * automatic component registration, or custom dependency injection tools.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * List<Class<?>> components = findAnnotatedClasses("com.example.app", MyComponent.class);
     * }</pre>
     *
     * @param annot         the annotation type class to look for
     * @param packageNames  the full name of the package to scan (e.g., "com.example")
     * 
     * @return a list of matching classes, or an empty list if no matches are found
     * @throws java.lang.NullPointerException if either the package name or annotation is null
     * 
     * @see java.lang.annotation.Annotation
     * @see <a href="https://oracle.com">Class.isAnnotationPresent Documentation</a>
     */
    public static List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annot, String[] packageNames) {
        List<Class<?>> annotated = new ArrayList<>();
        
        try {
            List<Class> classes = scan(packageNames);
            for (Class<?> clazz : classes) {
                if (! Modifier.isAbstract(clazz.getModifiers())
                        && ! Modifier.isInterface(clazz.getModifiers())
                        && Modifier.isPublic(clazz.getModifiers())
                        && clazz.isAnnotationPresent(annot)) {
                    
                    annotated.add(clazz);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error finding annotated classes of " + annot, e);
        }
        return annotated;
    }
    
    /**
     * Scans all JAR files present in the classpath for classes with a specific annotation.
     * 
     * <p>This method inspects every JAR archive file defined in the current 
     * Java classpath. It opens each archive, reads the class entries, and checks 
     * if they are marked with the target annotation type. This is commonly 
     * used during application startup for plugin discovery or framework configuration.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * Set<Class<?>> controllers = classpathScanner.scan(MyController.class);
     * }</pre>
     *
     * @param packageNames  the full name of the package to scan (e.g., "com.example")
     * @return a set of matching classes found in the classpath JARs, never null
     * 
     * @throws java.io.IOException if an error occurs while reading the JAR files
     * @throws java.lang.ClassNotFoundException if the specified annotation is null
     * 
     * @see java.util.jar.JarFile
     * @see java.lang.annotation.Annotation
     * @see <a href="https://oracle.com">URLClassLoader.getURLs Documentation</a>
     */
    public static List<Class> scan(String[] packageNames) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<Class> classes = new ArrayList();
        
        for (String packageName : packageNames) {
            String path = ".";
            if (! packageName.equals(".")) {
                path = (packageName = packageName.trim()).replace('.', '/');
            }

            Enumeration resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList();
            boolean jar = true;
            
            while (resources.hasMoreElements()) {
                URL resource = (URL)resources.nextElement();
                URLConnection urlConn = resource.openConnection();
                
                // For FAT jar.
                if (urlConn instanceof JarURLConnection) {
                    final JarFile jarFile = ((JarURLConnection)urlConn).getJarFile();
                    final Enumeration<JarEntry> entries = jarFile.entries();
                    String name = null;
                    
                    for (JarEntry jarEntry = null; entries.hasMoreElements()
                            && ((jarEntry = entries.nextElement()) != null);) {
                        
                        name = jarEntry.getName();

                        if (name.contains(".class")) {
                            name = name.substring(0, name.length() - 6).replace('/', '.');
                            if (name.startsWith(packageName)) {
                                classes.add(Class.forName(name));
                            }
                        }
                    }
                }
                else {
                    // Assuming it's a File URLConnection.
                    jar = false;
                    dirs.add(new File(resource.getFile()));
                }
            }
            if (! jar) {
                for (File directory : dirs) {
                    classes.addAll(findClasses(directory, packageName));
                }
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scanner scanned total {} classes from package: {}"
                , classes.size(), Arrays.toString(packageNames));
        }
        return classes;
    }
    
    /**
     * Recursive method used to find all classes in a given directory and sub directories.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return List The classes
     * @throws ClassNotFoundException
     */
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName.equals(".") ? file.getName() : (packageName + "." + file.getName())));
            }
            else if (file.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classes.add(clazz);
            }
        }
        return classes;
    }
}
