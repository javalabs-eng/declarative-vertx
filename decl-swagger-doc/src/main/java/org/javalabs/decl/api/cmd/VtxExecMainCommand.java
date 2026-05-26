package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.CharUtil;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.FileHandlerUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 *
 * @author schan280
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
                    
            byte[] buff = FileHandlerUtil.read(template);
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
