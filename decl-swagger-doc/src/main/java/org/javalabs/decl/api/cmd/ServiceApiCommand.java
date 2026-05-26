package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.ApiHelper;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;
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
public class ServiceApiCommand implements Command {
    
    private static final String SERVICE_API_TEMPLATE = "service_api.template";
    
    private final ApiHelper helper = new ApiHelper();
    private final String name;
    
    public ServiceApiCommand(String name) {
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
                    + project.handlerPkg().replace('.', '/');
            
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + "handler" + File.separator
                    + SERVICE_API_TEMPLATE;
                    
            byte[] buff = FileHandlerUtil.read(template);
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            for (Map.Entry<String, JavaClass> me: classes.entrySet()) {
                String tmp = helper.analyze(project, new String(buff), me.getValue());
            
                File handlerFile = new File(destDir + File.separator + me.getKey() + "Handler.java");
                ClassWriter.write(handlerFile, tmp, project.verbose());

                if (project.verbose() <= 2) {
                    ConsoleWriter.timingPrintln("Created handler file: " + ConsoleWriter.ANSI_GREEN + handlerFile.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
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
