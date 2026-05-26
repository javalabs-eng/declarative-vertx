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
public class TestCaseCommand implements Command {
    
    private final String name;
    
    public TestCaseCommand(String name) {
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
            String outDir = projectRoot + File.separator
                    + project.testDir() + File.separator
                    + project.basePkg().replace('.', '/') + File.separator
                    + "integ";
            
            String templateDir = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "test";
                    
            byte[] buff = FileHandlerUtil.read(templateDir + File.separator + "db_extension.template");
            String tmp = new String(buff);
            tmp = tmp.replace("{PACKAGE}", project.basePkg());
            
            File testExtentionFile = new File(outDir, "DBExtension.java");
            ClassWriter.write(testExtentionFile, tmp, project.verbose());
            
            buff = FileHandlerUtil.read(templateDir + File.separator + "abstract_rest_api.template");
            tmp = new String(buff);
            tmp = tmp.replace("{PACKAGE}", project.basePkg());
            
            testExtentionFile = new File(outDir, "AbstractRestApiTest.java");
            ClassWriter.write(testExtentionFile, tmp, project.verbose());
            
            buff = FileHandlerUtil.read(templateDir + File.separator + "abstract_bo_test.template");
            tmp = new String(buff);
            tmp = tmp.replace("{PACKAGE}", project.basePkg());
            
            testExtentionFile = new File(outDir, "AbstractBOTest.java");
            ClassWriter.write(testExtentionFile, tmp, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created test case extension file(s)");
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