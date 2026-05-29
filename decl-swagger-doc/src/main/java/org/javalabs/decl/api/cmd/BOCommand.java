package org.javalabs.decl.api.cmd;

import org.javalabs.decl.api.cust.Customization;
import org.javalabs.decl.api.cust.PostMethodCustomization;
import org.javalabs.decl.api.cust.PutMethodCustomization;
import org.javalabs.decl.api.cust.Result;
import org.javalabs.decl.api.project.CommonHelperSupport;
import org.javalabs.decl.api.project.Project;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.cust.BatchMethodCustomization;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * A command utility that automatically generates Business Object (BO) classes.
 * 
 * <p>This class uses a template-based approach to build business logic layers 
 * for a Java project. It reads a data model definition or database schema, 
 * merges that information with preset text templates, and writes the complete 
 * {@code .java} files to disk. This speeds up development and keeps project 
 * architecture consistent.</p>
 * 
 * <p>Typical workflow usage:</p>
 * <ol>
 *   <li>The command reads structural data from a configuration source.</li>
 *   <li>It loads the required Business Object templates into memory.</li>
 *   <li>It generates clean code structures including fields, getters, setters, 
 *       and validation logic.</li>
 * </ol>
 * 
 * @author Sudiptasish Chanda
 */
public class BOCommand implements Command {
    
    private static final String AUTH_BO_TEMPLATE = "auth_bo.template";
    private static final String BO_TEMPLATE = "bo.template";
    
    private final Customization postCustomizer = new PostMethodCustomization();
    private final Customization putCustomizer = new PutMethodCustomization();
    private final Customization batchCustomizer = new BatchMethodCustomization();
    private final CommonHelperSupport helper = new CommonHelperSupport();
    private final String name;
    
    public BOCommand(String name) {
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
                    + project.boPkg().replace('.', '/');
            
            // First the auth bo
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "bo" + File.separator
                    + AUTH_BO_TEMPLATE;

            byte[] buff = StreamUtil.read(template);
            String content = helper.analyze(project, project.boPkg(), new String(buff), null);
            
            File file = new File(destDir + File.separator + "Auth" + "BO" + ".java");
            ClassWriter.write(file, content, project.verbose());

            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created " + "BO" + " file: "
                        + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath()
                        + ConsoleWriter.ANSI_RESET);
            }

            // Prepare the bo file content.
            template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "bo" + File.separator
                    + BO_TEMPLATE;

            buff = StreamUtil.read(template);
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            for (Map.Entry<String, JavaClass> me: classes.entrySet()) {
                write(project, destDir, me.getValue(), buff, "BO");
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (ReflectiveOperationException | IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void write(Project project, String dir, JavaClass model, byte[] buff, String suffix) throws ReflectiveOperationException {
        String content = helper.analyze(project, project.boPkg(), new String(buff), model);
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
        final Customization[] csArr = {postCustomizer, putCustomizer, batchCustomizer};
        String tmp = content;
        String entryPoint = "boEntry";
        
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
