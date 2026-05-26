package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
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
public class ReadMeCommand implements Command {
    
    // private static final String README_TEMPLATE = "read_me.template";
    private static final String README_TEMPLATE = "README.md.template";
    private static final String README_MD  = "README.md";
    
    private final String name;
    
    public ReadMeCommand(String name) {
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
                    + README_TEMPLATE;
                    
            byte[] buff = FileHandlerUtil.read(template);
            String tmp = new String(buff);
            tmp = tmp.replace("{name}", project.name());
            tmp = tmp.replace("{URI}", "/api/v1/<resource_name>");
            tmp = tmp.replace("{LOWER{APP}}", project.name());
            
            File readmeFile = new File(projectRoot + File.separator + README_MD);
            ClassWriter.write(readmeFile, tmp, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created readme file: " + ConsoleWriter.ANSI_GREEN + readmeFile.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
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
