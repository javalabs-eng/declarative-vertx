package org.javalabs.decl.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A utility class designed to scan packages and discover Java classes at runtime.
 * 
 * <p>This scanner supports looking up classes from both standard file directories 
 * (such as a local compiled target folder) and compressed fat JAR files (such as a 
 * standalone deployment executable). It uses the current thread's context class 
 * loader to locate and load the classes.</p>
 *
 * @author Sudiptasish Chanda
 */
public class Scanner {

    /**
     * Scans multiple Java packages by name and loads all discovered classes.
     * 
     * <p>This method loops through the provided package names, normalizes their text 
     * paths, and pulls matching resource files. If it detects a JAR-bound environment, 
     * it safely processes the archive entries. Otherwise, it drops back to scanning 
     * physical disk directories recursively.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * String[] packages = {"com.example.api", "com.example.model"};
     * List<Class> activeClasses = Scanner.scan(packages);
     * }</pre>
     *
     * @param packageNames an array of dot-separated package strings to look inside (e.g., {@code "com.example"})
     * @return a mutable list containing all successfully loaded {@link Class} objects, never null
     * 
     * @throws IOException            if an error occurs while opening connection streams or reading files
     * @throws ClassNotFoundException if a discovered class file structure cannot be loaded by the class loader
     * @throws NullPointerException   if the input array or any package element inside it is null
     */
    public static List<Class> scan(String[] packageNames) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<Class> classes = new ArrayList();
        
        for (String packageName : packageNames) {
            String path = (packageName = packageName.trim()).replace('.', '/');

            Enumeration resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList();
            boolean jar = true;
            
            while (resources.hasMoreElements()) {
                URL resource = (URL)resources.nextElement();
                URLConnection urlConn = resource.openConnection();
                
                ConsoleWriter.println("Package URL Connection: " +  urlConn.getClass());
                
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
                            if (name.contains(packageName)) {
                                classes.add(Thread.currentThread().getContextClassLoader().loadClass(name));
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
        ConsoleWriter.println("Scanner scanned total " + classes.size()
                + " classes from package: " + Arrays.toString(packageNames));
        return classes;
    }
    
    /**
     * Recursively travels through a local directory folder to find all class files.
     *
     * @param directory   the physical folder root to search through on disk
     * @param packageName the dot-separated tracking name of the matching package structure
     * @return a list of discovered classes inside the target directory tree
     * @throws ClassNotFoundException if a class file cannot be loaded by reflection lookup
     */
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class")) {
                    Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    classes.add(clazz);
            }
        }
        return classes;
    }
}
