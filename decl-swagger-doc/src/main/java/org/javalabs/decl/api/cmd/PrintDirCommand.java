package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command utility that displays a visual folder tree structure for a given directory path.
 * 
 * <p>This class travels recursively through a target folder on the hard drive. 
 * It lists every folder and file it finds. It then prints out a tree diagram 
 * using text lines, making it easy to see how the files are organized.</p>
 * 
 * <p>Typical display layout example:</p>
 * <pre>
 * my-project/
 * ├── src/
 * │   └── Main.java
 * ├── resources/
 * │   └── config.properties
 * └── README.md
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
public class PrintDirCommand implements Command {
    
    private final String name;
    
    public PrintDirCommand(String name) {
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
                ConsoleWriter.timingPrintln("\nProject hierarchy:\n");
                hierarchy(new File(project.dir() + File.separator + project.name()), "");
                ConsoleWriter.println("");
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public void hierarchy(File directory, String indent) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            ConsoleWriter.timingPrintln(indent + "+-- " + ConsoleWriter.ANSI_GREEN + file.getName() + ConsoleWriter.ANSI_RESET);
            if (file.isDirectory()) {
                hierarchy(file, indent + "|  ");
            }
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
    
}
