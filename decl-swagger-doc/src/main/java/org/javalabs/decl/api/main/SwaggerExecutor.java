package org.javalabs.decl.api.main;

import org.javalabs.decl.api.doc.DocOption;
import org.javalabs.decl.api.project.Platform;
import org.javalabs.decl.api.project.TechStack;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * Swagger api doc generation command.
 *
 * @author Sudiptasish Chanda
 */
public class SwaggerExecutor implements ExecutorBase {
    
    private final String name = "swagger";
    private final String description = "Generate swagger api documentation of a project";
    private final List<String> longOptions = Arrays.asList("--create", "--routing-file", "--model-lib", "--out-file", "--verbose");
    private final List<String> shortOptions = Arrays.asList("-c", "-r", "-m", "-o", "-v");
    
    public SwaggerExecutor() {}

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }
    
    @Override
    public void start(String[] options) {
        Boolean create = Boolean.FALSE;
        Integer verbose = 2;
        Platform platform = Platform.JAVA;
        TechStack stack = TechStack.VERTX;
        String[] packageNames = null;
        String routingFile = null;
        String modelLib = null;     // /path/to/example-rest.jar
        String outFile = null;
        
        for (int i = 0; i < options.length; i ++) {
            if (options[i].equals("-c") || options[i].equals("--create")) {
                create = Boolean.TRUE;
            }
            else if (options[i].equals("-p") || options[i].equals("--platform")) {
                try {
                    verifyArg(options[i], options[i + 1]);
                    platform = Enum.valueOf(Platform.class, options[i + 1].toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid platform " + options[i + 1]
                            + ". Valid values are: " + Arrays.toString(Platform.values()));
                }
            }
            else if (options[i].equals("-t") || options[i].equals("--tech-stack")) {
                try {
                    verifyArg(options[i], options[i + 1]);
                    stack = Enum.valueOf(TechStack.class, options[i + 1].toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid tech-stack " + options[i + 1]
                            + ". Valid values are: " + Arrays.toString(TechStack.values()));
                }
            }
            else if (options[i].equals("-k") || options[i].equals("--package-name")) {
                verifyArg(options[i], options[i + 1]);
                packageNames = options[i + 1].split(",");
            }
            else if (options[i].equals("-r") || options[i].equals("--routing-config")) {
                verifyArg(options[i], options[i + 1]);
                routingFile = options[i + 1];
            }
            else if (options[i].equals("-m") || options[i].equals("--model-lib")) {
                verifyArg(options[i], options[i + 1]);
                modelLib = options[i + 1];
            }
            else if (options[i].equals("-o") || options[i].equals("--out-file")) {
                verifyArg(options[i], options[i + 1]);
                outFile = options[i + 1];
            }
            else if (options[i].equals("-v") || options[i].equals("--verbose")) {
                verifyArg(options[i], options[i + 1]);
                verbose = Integer.valueOf(options[i + 1]);
            }
        }
        if (stack == TechStack.VERTX && routingFile == null) {
            routingFile = "routing-config.xml";
        }
        if (stack == TechStack.SPRING && packageNames == null) {
            throw new IllegalArgumentException("Missing -p [--package-name] option."
                    + " Pass comma(,) separated package name(s) of spring controller file(s)");
        }
        if (outFile == null) {
            outFile = "./openapi.yaml";
        }
        if (modelLib == null) {
            throw new IllegalArgumentException("Missing -m [--model-lib] option. Pass colon(:) separated jar file(s)");
        }
        if (! create) {
            throw new IllegalArgumentException("Did you miss the -c [--create] flag?");
        }
        // Start ...
        // Load the jar in the memory.
        try {
            String[] files = modelLib.split(":");
            URL[] urls = new URL[files.length];

            for (int i = 0; i < files.length; i ++) {
                File jarFile = new File(files[i]);
                if (! jarFile.exists()) {
                    throw new IllegalArgumentException("Model jar file " + files[i] + " does not exist");
                }
                urls[i] = jarFile.toURI().toURL();
            }
            URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);

            DocOption docOpt = new DocOption();
            docOpt.setVerbose(verbose);
            docOpt.setPackages(packageNames);
            docOpt.setConfigFile(routingFile);
            docOpt.setModelLib(modelLib);
            docOpt.setOutFile(outFile);
            
            String className = "org.javalabs.decl.api.doc.SwaggerDoc";
            if (stack == TechStack.SPRING) {
                className = "org.javalabs.decl.api.doc.SpringSwaggerDoc";
            }
            
            Class<?> clazz = loader.loadClass(className);
            Constructor cons = clazz.getDeclaredConstructor(new Class[] {});
            Object obj = cons.newInstance(new Object[] {});

            Method method = clazz.getDeclaredMethod("generate", new Class[] {DocOption.class});
            method.invoke(obj, new Object[] {docOpt});
            
        }
        catch (MalformedURLException | ClassNotFoundException
                | InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException e) {
            
            throw new RuntimeException(e);
        }
    }
    
    private void verifyArg(String param, String val) {
        if (longOptions.contains(val) || shortOptions.contains(val)) {
            throw new IllegalArgumentException("Missing value for option [" + param + "]");
        }
    }
}
