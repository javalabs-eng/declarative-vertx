package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
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
 * A command utility that automatically generates Data Access Object (DAO) interfaces and implementation classes.
 * 
 * <p>This class uses a template-based approach to build the data access layer for a Java project. 
 * It reads a schema definition or database table structure, merges that metadata with preset 
 * templates, and outputs two primary Java source files for each entity:</p>
 * <ul>
 *   <li>A DAO interface specifying standard CRUD (Create, Read, Update, Delete) methods.</li>
 *   <li>A DAO implementation class (DAOImpl) containing the actual database query logic.</li>
 * </ul>
 * 
 * <p>Using this command ensures consistent data-access patterns across the application 
 * and eliminates repetitive database connection and mapping boilerplate code.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class DAOCommand implements Command {
    
    private static final String DAO_TEMPLATE = "dao.template";
    private static final String DAO_IMPL_TEMPLATE = "dao_impl.template";
    
    private final CommonHelperSupport helper = new CommonHelperSupport();
    private final String name;
    
    public DAOCommand(String name) {
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
                    + project.daoPkg().replace('.', '/');

            // Prepare dao file content
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "dao" + File.separator
                    + DAO_TEMPLATE;

            byte[] dao_buff = StreamUtil.read(template);
            
            // Prepare daoImpl file content
            template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "dao" + File.separator
                    + DAO_IMPL_TEMPLATE;

            byte[] daoimpl_buff = StreamUtil.read(template);
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            
            for (Map.Entry<String, JavaClass> me: classes.entrySet()) {
                write(project, destDir, me.getValue(), dao_buff, "DAO");
                write(project, destDir, me.getValue(), daoimpl_buff, "DAOImpl");
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void write(Project project, String dir, JavaClass model, byte[] buff, String suffix) {
        String content = helper.analyze(project, project.daoPkg(), new String(buff), model);
        
        File file = new File(dir + File.separator + model.name() + suffix + ".java");
        ClassWriter.write(file, content, project.verbose());
        
        if (project.verbose() <= 2) {
            ConsoleWriter.timingPrintln("Created " + suffix + " file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
