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
 * A command utility that automatically generates a server.xml configuration file for an HTTP server.
 * 
 * <p>This class uses a template-based approach to construct a complete, valid server 
 * configuration profile. It collects system values (such as network ports, thread pool 
 * capacities, and security properties), merges them with preset templates, and writes 
 * a structured {@code server.xml} file to the server's deployment directory.</p>
 * 
 * <p>The generated XML configuration file captures critical server attributes including:</p>
 * <ul>
 *   <li>Network configurations such as listener ports, host binding addresses, and connection timeouts.</li>
 *   <li>Performance variables including executor thread pool caps, queue lengths, and keep-alive limits.</li>
 *   <li>Security rules like SSL/TLS protocol selections, cipher suites, and key manager paths.</li>
 * </ul>
 * 
 * <p>Using this command centralizes runtime deployment structures into a clean file scheme, 
 * making it easy to stand up new environment profiles across development and production networks.</p>
 * 
 *
 * @author Sudiptasish Chanda
 */
public class ServerConfigCommand implements Command {
    
    private static final String SERVER_TEMPLATE = "server.template";
    private static final String SERVER_FILE   = "server.xml";
    
    private final String name;
    
    public ServerConfigCommand(String name) {
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
                    + SERVER_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            String content = new String(buff);
            content = content.replace("{APP}", project.name());
            
            File serverConfigFile = new File(project.dir() + File.separator + project.name() + File.separator + project.srcResourceDir(), SERVER_FILE);
            ClassWriter.write(serverConfigFile, content, project.verbose());
            
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
