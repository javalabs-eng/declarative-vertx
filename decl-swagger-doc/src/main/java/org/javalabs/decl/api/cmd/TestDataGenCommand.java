package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TestDataGenCommand implements Command {
    
    private final String name;
    
    public TestDataGenCommand(String name) {
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
                        + project.dbDir();
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            String[] fileNames = new String[classes.size()];
            int idx = 0;
            
            for (Map.Entry<String, JavaClass> me : classes.entrySet()) {
                JavaClass jClass = me.getValue();
                
                String modelFile = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.srcDir()
                        + File.separator
                        + project.modelPkg().replace('.', '/')
                        + File.separator
                        + jClass.name() + ".java";
                
                fileNames[idx] = jClass.name() + ".java";
                idx ++;
            }
            for (String fileName : fileNames) {
                // Do something
            }
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Generated table script " + destDir + File.separator + "schema.sql" + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public void write(Project project, Map<String, String> classMapping) throws IOException {
        for (Map.Entry<String, String> me : classMapping.entrySet()) {
            Files.write(
                    Paths.get(project.dir()
                            , project.srcDir()
                                    + File.separator + project.modelPkg().replace('.', File.separatorChar)
                                    + File.separator + me.getKey() + ".java")
                    , me.getValue().getBytes()
                    , StandardOpenOption.CREATE_NEW);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
