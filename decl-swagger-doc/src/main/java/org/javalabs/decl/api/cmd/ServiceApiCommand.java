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
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * A command utility that automatically generates API access endpoints based on the project's tech stack.
 * 
 * <p>This class analyzes the project's configuration to see what web framework is being used. 
 * It then chooses the right templates to build the correct endpoint files. It can pivot 
 * automatically between different runtime setups:</p>
 * <ul>
 *   <li>Generates standard MVC or REST controllers if the project uses a servlet stack (like Spring Boot).</li>
 *   <li>Generates reactive request handlers if the project uses an asynchronous stack (like Eclipse Vert.x).</li>
 * </ul>
 * 
 * <p>Using this command allows your code generation tools to stay flexible. You can switch or 
 * update your underlying web framework without changing your core API definitions.</p>
 * 
 * @author Sudiptasish Chanda
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
                    
            byte[] buff = StreamUtil.read(template);
            
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
