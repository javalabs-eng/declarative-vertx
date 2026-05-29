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
 * A command utility that automatically generates a vertx-web.xml configuration file.
 * 
 * <p>This class uses a template-based approach to construct a complete deployment profile 
 * for a reactive Vert.x web application. It aggregates component metadata, merges that 
 * information with preset templates, and writes a structured {@code vertx-web.xml} file 
 * used to bootstrap the system runtime.</p>
 * 
 * <p>The generated configuration file captures critical deployment properties including:</p>
 * <ul>
 *   <li>The registration and instance counts of core web and worker {@link io.vertx.core.Verticle} components.</li>
 *   <li>Environment configuration variables such as database endpoints, encryption secrets, and system flags.</li>
 *   <li>Network routing constraints including default timeout thresholds, file upload directories, and body size limits.</li>
 * </ul>
 * 
 * <p>Using this command centralizes decentralized vertical configurations into a single, uniform manifest 
 * file, simplifying deployment setups across different testing and production environments.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class VertxWebConfigCommand implements Command {
    
    private static final String V_WEB_TEMPLATE = "vertx-web.template";
    private static final String V_WEB_FILE   = "vertx-web.xml";
    
    private final String name;
    
    public VertxWebConfigCommand(String name) {
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
            // Generate pom.xml file
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + "xml" + File.separator
                    + V_WEB_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            String tmp = new String(buff);
            tmp = tmp.replace("{PACKAGE}", project.corePkg());
            
            File serverConfigFile = new File(project.dir() + File.separator + project.name() + File.separator + project.srcResourceDir(), V_WEB_FILE);
            ClassWriter.write(serverConfigFile, tmp, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created " + ConsoleWriter.ANSI_GREEN + serverConfigFile + ConsoleWriter.ANSI_RESET);
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
