package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;
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
 * A command utility that automatically generates internal and container classes for a target project.
 * 
 * <p>This class acts as a foundational code generator for bootstrapping a project's core framework. 
 * It uses preset text templates to create internal system files and data containers. This ensures 
 * uniform class hierarchies, standardized collection wrappers, and clean data-transfer mechanisms 
 * across the entire application ecosystem.</p>
 * 
 * <p>The generated components typically include structural objects such as:</p>
 * <ul>
 *   <li>Internal engine utilities, base exception structures, and common system constants.</li>
 *   <li>Custom data holder classes, context containers, and registry records for resource state handling.</li>
 *   <li>Dependency resolution objects and runtime lookup catalogs for internal modular management.</li>
 * </ul>
 * 
 * <p>Using this command establishes a uniform structural pattern for new modules and eliminates 
 * repetitive manual coding of architectural boilerplate code.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class CoreCommand implements Command {
    
    private final Map<String, String> templateMapping = Map.of(
            "AppContainer.java", "app_container.template",
            "AppHttpServer.java", "app_httpserver.template",
            "AppProcessor.java", "app_processor.template",
            "DefaultTimer.java", "app_timer.template"
    );
    
    private final String name;
    
    public CoreCommand(String name) {
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
            String destDir = projectRoot.getAbsolutePath()
                    + File.separator
                    + project.srcDir()
                    + File.separator
                    + project.corePkg().replace('.', '/');
            
            String coreDir = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + "core";
            
            for (Map.Entry<String, String> me : templateMapping.entrySet()) {
                String template = coreDir + File.separator + me.getValue();
                write(project, destDir, template, me.getKey().substring(0, me.getKey().indexOf(".")));
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void write(Project project, String destDir, String template, String className) throws IOException {
        byte[] buff = StreamUtil.read(template);
            
        // Now, look for the following customizer sequentially and start replacing the code
        String content = new String(buff);
        content = content.replace("{PACKAGE}", project.corePkg());
        content = content.replace("{CONFIG_PACKAGE}", project.configPkg());
        content = content.replace("{PU_NAME}", project.name().toLowerCase() + "-" + "pu");

        File file = new File(destDir + File.separator + className + ".java");
        
        ClassWriter.write(file, content, project.verbose());
        if (project.verbose() <= 2) {
            ConsoleWriter.timingPrintln("Created container related file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
