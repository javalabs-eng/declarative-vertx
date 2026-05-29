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
 *
 * @author Sudiptasish Chanda
 */
public class AdditionalTemplateCommand implements Command {
    
    private static final String VERTICLE_TEMPLATE = "http_verticle.template";
    private static final String VERTICLE_NAME  = "HttpServerVerticle.java";
    
    private final String name;
    
    public AdditionalTemplateCommand(String name) {
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
                    + VERTICLE_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            
            if (buff != null && buff.length > 0) {
                String destDir = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.srcDir()
                        + File.separator
                        + project.basePkg().replace('.', '/');

                File file = new File(destDir + File.separator + VERTICLE_NAME);
                ClassWriter.write(file, new String(buff), project.verbose());

                if (project.verbose() <= 2) {
                    ConsoleWriter.timingPrintln("Created http server verticle: " + file.getAbsolutePath());
                }
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
