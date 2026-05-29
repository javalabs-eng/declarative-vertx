package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.CharUtil;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * A command utility that automatically generates the vert.x compliant main application entry point class.
 * 
 * <p>This class uses a template-based approach to construct an executable main class 
 * for a Java application. It merges runtime parameters, configurations, and baseline 
 * imports into a template, then writes a structured {@code .java} source file containing 
 * the standard executable main method directly to disk.</p>
 * 
 * <p>The generated main class sets up core application structures including:</p>
 * <ul>
 *   <li>The required {@code public static void main(String[] args)} launcher signature.</li>
 *   <li>System initialization calls, basic environment variable loading, and logging triggers.</li>
 *   <li>Startup hooks for executing command chains or kicking off dependency injection contexts.</li>
 * </ul>
 * 
 * <p>Using this command speeds up project bootstrapping by replacing manual execution 
 * boilerplate code with an automated, standardized entry point file.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class VtxExecMainCommand implements Command {
    
    private static final String VTX_MAIN_TEMPLATE = "vtx_main_class.template";
    
    private final String name;
    
    public VtxExecMainCommand(String name) {
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
                    + project.stack().name().toLowerCase() + File.separator
                    + VTX_MAIN_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            String content = new String(buff);
            content = content.replace("{PACKAGE}", project.mainPkg());
            content = content.replace("{CORE_PACKAGE}", project.corePkg());
            content = content.replace("{PROJECT}", CharUtil.toCapitalisedCamelCase(project.name()));
            
            String destDir = projectRoot.getAbsolutePath()
                    + File.separator
                    + project.srcDir()
                    + File.separator
                    + project.mainPkg().replace('.', '/');
            
            File file = new File(destDir + File.separator + CharUtil.toCapitalisedCamelCase(project.name()) + "Main" + ".java");
            ClassWriter.write(file, content, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created main class: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
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
