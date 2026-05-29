package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * A command utility that generates a ReDoc HTML interface to render Swagger API documentation.
 * 
 * <p>This class creates or updates an {@code index.html} file inside the web assets directory. 
 * It embeds the Redoc JavaScript library via CDN or local reference and injects the 
 * configuration needed to load a target Swagger or OpenAPI specification file. This provides 
 * a clean, responsive three-column theme for viewing API documentation.</p>
 * 
 * <p>The generated HTML page typically handles operations including:</p>
 * <ul>
 *   <li>Loading the Redoc standalone deployment bundle script.</li>
 *   <li>Configuring visual options such as hiding the search bar or setting standard color themes.</li>
 *   <li>Binding the Redoc renderer component to the path of your project's {@code openapi.yml} file.</li>
 * </ul>
 * 
 * <p>Using this command creates a modern, user-friendly alternative to standard Swagger UI 
 * views automatically during the application build phase.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class ReDocCommand implements Command {
    
    private static final String REDOC_TEMPLATE = "index_html.template";
    private static final String REDOC_NAME  = "index.html";
    
    private final String name;
    
    public ReDocCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        
        File projectRoot = new File(project.dir(), project.name());
        
        try {
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + REDOC_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            
            String destDir = projectRoot.getAbsolutePath()
                    + File.separator
                    + project.docDir();
            
            File file = new File(destDir + File.separator + REDOC_NAME);
            ClassWriter.write(file, new String(buff), project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created redoc file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
