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
 * A command utility that automatically generates a JUnit test platform configuration and sample JUnit extension.
 * 
 * <p>This class automates the setup of testing architecture for a Java project. It creates a standardized 
 * JUnit test engine platform configuration file and generates a custom JUnit 5 extension class template. 
 * This generated template acts as a blueprint, providing reusable lifecycle hooks for other test cases 
 * across the application.</p>
 * 
 * <p>The generated components typically manage common test operations including:</p>
 * <ul>
 *   <li>Configuring test suite behaviors in a configuration file.</li>
 *   <li>Creating custom extensions by implementing BeforeEachCallback}.</li>
 *   <li>Providing reusable test fixtures for database transaction rollback, system mocking, or memory cleanup.</li>
 * </ul>
 * 
 * <p>Using this command establishes a uniform testing foundation across all modules and removes the need 
 * to rewrite lifecycle setup logic manually for separate test classes.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see <a href="https://junit.org">JUnit 5 User Guide and Architecture Documentation</a>
 * @see <a href="https://apache.org">Apache FreeMarker Template Engine</a>
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
                    
            byte[] buff = StreamUtil.read(templateDir + File.separator + "db_extension.template");
            String tmp = new String(buff);
            tmp = tmp.replace("{PACKAGE}", project.basePkg());
            
            File testExtentionFile = new File(outDir, "DBExtension.java");
            ClassWriter.write(testExtentionFile, tmp, project.verbose());
            
            buff = StreamUtil.read(templateDir + File.separator + "abstract_rest_api.template");
            tmp = new String(buff);
            tmp = tmp.replace("{PACKAGE}", project.basePkg());
            
            testExtentionFile = new File(outDir, "AbstractRestApiTest.java");
            ClassWriter.write(testExtentionFile, tmp, project.verbose());
            
            buff = StreamUtil.read(templateDir + File.separator + "abstract_bo_test.template");
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