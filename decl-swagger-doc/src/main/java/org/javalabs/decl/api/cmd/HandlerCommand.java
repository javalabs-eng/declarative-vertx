package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.cust.Customization;
import org.javalabs.decl.api.cust.DeleteMethodCustomization;
import org.javalabs.decl.api.cust.GetMethodCustomization;
import org.javalabs.decl.api.cust.PutMethodCustomization;
import org.javalabs.decl.api.cust.Result;
import org.javalabs.decl.api.project.CommonHelperSupport;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * A command utility that automatically generates Vert.x handler classes.
 * 
 * <p>This class uses a template-based approach to build the routing and request-handling 
 * layer for an asynchronous Vert.x web application. It takes endpoint metadata (such as 
 * HTTP methods, paths, and payload models), merges that data with preset templates, and 
 * generates functional {@link io.vertx.core.Handler} implementations.</p>
 * 
 * <p>The generated classes handle standard incoming network tasks such as:</p>
 * <ul>
 *   <li>Extracting path parameters and query strings from a {@code RoutingContext}.</li>
 *   <li>Parsing and validating the JSON request body.</li>
 *   <li>Routing the processing to a background service or event bus address.</li>
 * </ul>
 * 
 * <p>Using this command keeps API routing declarations uniform and removes repetitive 
 * request-parsing boilerplate code across your reactive services.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see io.vertx.core.Handler
 * @see io.vertx.ext.web.RoutingContext
 */
public class HandlerCommand implements Command {
    
    private static final String AUTH_HANDLER_TEMPLATE = "authn_handler.template";
    private static final String HANDLER_TEMPLATE = "handler.template";
    private static final String ABS_HANDLER_TEMPLATE = "abstract_handler.template";
    
    private final Customization getCustomizer = new GetMethodCustomization();
    private final Customization putCustomizer = new PutMethodCustomization();
    private final Customization deleteCustomizer = new DeleteMethodCustomization();
    private final CommonHelperSupport helper = new CommonHelperSupport();
    private final String name;
    
    public HandlerCommand(String name) {
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
            
            // Create AuthenticationHandler class.
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + "handler" + File.separator
                    + AUTH_HANDLER_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            String content = helper.analyze(project, project.handlerPkg(), new String(buff), null);
            
            File file = new File(destDir + File.separator + "AuthenticationHandler" + ".java");

            ClassWriter.write(file, content, project.verbose());
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created authn handler file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
            }
            
            // Create AbstractHandler class.
            template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + "handler" + File.separator
                    + ABS_HANDLER_TEMPLATE;
                    
            buff = StreamUtil.read(template);
            content = helper.analyze(project, project.handlerPkg(), new String(buff), null);
            
            file = new File(destDir + File.separator + "AbstractHandler" + ".java");

            ClassWriter.write(file, content, project.verbose());
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created abstract handler file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
            }

            // Prepare the other handlers file content.
            template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + "handler" + File.separator
                    + HANDLER_TEMPLATE;

            buff = StreamUtil.read(template);
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            for (Map.Entry<String, JavaClass> me: classes.entrySet()) {
                write(project, destDir, me.getValue(), buff, "Handler");
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (ReflectiveOperationException | IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void write(Project project, String dir, JavaClass model, byte[] buff, String suffix) throws ReflectiveOperationException {
        String content = helper.analyze(project, project.handlerPkg(), new String(buff), model);
        content = applyCustomization(content, project, model);
        
        File file = new File(dir + File.separator + model.name() + suffix + ".java");
        ClassWriter.write(file, content, project.verbose());
        if (project.verbose() <= 2) {
            ConsoleWriter.timingPrintln("Created " + suffix.toLowerCase() + " file: "
                    + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath()
                    + ConsoleWriter.ANSI_RESET);
        }
    }
    
    private String applyCustomization(String content, Project project, JavaClass model) throws ReflectiveOperationException {
        final Customization[] csArr = {getCustomizer, putCustomizer, deleteCustomizer};
        String tmp = content;
        String entryPoint = "handlerEntry";
        
        for (Customization cs : csArr) {
            String exp = "{" + cs.getClass().getName() + "." + entryPoint + "(${Project}, ${JavaClass})" + "}";
        
            if (tmp.indexOf(exp) > 0) {
                Method method = cs.getClass().getDeclaredMethod(entryPoint, Project.class, JavaClass.class);
                Result res = (Result)method.invoke(cs, project, model);
                tmp = tmp.replace(exp, res.toString());
            }
        }
        return tmp;
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
