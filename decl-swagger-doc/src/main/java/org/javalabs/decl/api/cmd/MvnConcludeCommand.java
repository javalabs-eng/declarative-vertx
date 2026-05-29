package org.javalabs.decl.api.cmd;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command utility class to instruct the user as to how to compile and execute the target project.
 *
 * @author Sudiptasish Chanda
 */
public class MvnConcludeCommand implements Command {
    
    private final String name;
    
    public MvnConcludeCommand(String name) {
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
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("\nYour project [" + ConsoleWriter.ANSI_GREEN + project.dir() + File.separator + project.name() + ConsoleWriter.ANSI_RESET + "] has been created");
                ConsoleWriter.timingPrintln("Use \"mvn clean install\" to build the project");
                ConsoleWriter.timingPrintln("Run \"java -jar target/" + project.name() + "-0.0.1-SNAPSHOT.jar\" to start the server");
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
    
}
