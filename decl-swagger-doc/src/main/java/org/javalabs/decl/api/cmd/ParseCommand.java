package org.javalabs.decl.api.cmd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.FieldExtractor;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command to parse the option and populate the resource name and it;s data types.
 *
 * @author Sudiptasish Chanda
 */
public class ParseCommand implements Command {
    
    private final FieldExtractor extractor = new FieldExtractor();
    private final String name;
    
    public ParseCommand(String name) {
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
            extractor.extract(project);
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Parsed input and prepared the resource name and data types");
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
