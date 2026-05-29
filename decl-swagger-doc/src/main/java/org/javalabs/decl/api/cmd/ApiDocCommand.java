package org.javalabs.decl.api.cmd;

import org.javalabs.decl.api.doc.DocOption;
import org.javalabs.decl.api.doc.SwaggerDoc;
import org.javalabs.decl.api.project.Project;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.compile.DiscCompiler;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command class that automatically generates Swagger API documentation.
 * 
 * <p>This command scans the system APIs and endpoints to build a complete 
 * Swagger or OpenAPI description file. It captures paths, query parameters, 
 * request bodies, and expected responses. The output file can be saved to 
 * disk as a JSON or YAML document, making it easy to feed into Swagger UI tools.</p>
 * 
 * <p>This class is typically run as a build task, a command-line utility, or a 
 * lifecycle hook during application startup to keep API documentation up to date.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see <a href="https://swagger.io">OpenAPI Specification (Swagger)</a>
 * @see <a href="https://springdoc.org">Springdoc-OpenAPI Documentation</a>
 */
public class ApiDocCommand implements Command {
    
    private final String name;
    
    public ApiDocCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        
        try {
            File projectRoot = new File(project.dir(), project.name());
            String openApiFile = project.dir()
                    + File.separator + project.name()
                    + File.separator + project.docDir()
                    + File.separator
                    + "openapi.yaml";
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            
            // Prepare the custom classloader.
            URL[] urls = new URL[1];
            File generatedDir = new File(project.generated());
            urls[0] = generatedDir.toURI().toURL();

            URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);

            if (ctx.currentChain().name().equals("java_project_e2e") || ctx.currentChain().name().equals("java_project_e2e_db")) {
                // Compile the AuthToken.java
                String authTokenFile = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.srcDir()
                        + File.separator
                        + project.authPkg().replace('.', '/')
                        + File.separator
                        + "AuthToken.java";
                DiscCompiler.compile(project.generated(), project.authPkg(), "AuthToken", authTokenFile);
            }
            
            // Now, start compiling the other model classes
            for (Map.Entry<String, JavaClass> me: classes.entrySet()) {
                String resource = me.getKey();
                
                String modelFile = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.srcDir()
                        + File.separator
                        + project.modelPkg().replace('.', '/')
                        + File.separator
                        + resource + ".java";
                
                DiscCompiler.compile(project.generated(), project.modelPkg(), resource, modelFile);
            }
            DocOption docOpt = new DocOption();
            docOpt.setVerbose(project.verbose());
            // docOpt.setModelLib(modelFile);
            docOpt.setOutFile(openApiFile);

            String content = (String)ctx.get("routing.config");
            if (content != null && content.length() > 1) {
                docOpt.setCfgContent(content);
            }
            else {
                String cfgFile = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.srcResourceDir()
                        + File.separator
                        + "routing-config.xml";
                docOpt.setConfigFile(cfgFile);
            }
            // Now, generate the swagger doc
            SwaggerDoc doc = new SwaggerDoc();
            doc.generate(docOpt);
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Generated swagger doc: " + ConsoleWriter.ANSI_GREEN + openApiFile + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
    
}
