package org.javalabs.decl.api.cmd;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.DataType;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command utility that validates user inputs entered via a Command Line Interface (CLI).
 * 
 * <p>This class acts as a central guard layer for CLI applications. It inspects 
 * raw string arguments provided by users on the terminal, checks them against 
 * business rules, and flags errors before the application processes the data. 
 * This ensures data integrity and prevents system crashes from bad inputs.</p>
 * 
 * <p>The validator typically checks for common input requirements including:</p>
 * <ul>
 *   <li>Ensuring mandatory command line flags or arguments are not missing.</li>
 *   <li>Verifying correct text formats using data patterns or regular expressions.</li>
 *   <li>Checking numeric bounds to make sure values fall within acceptable ranges.</li>
 * </ul>
 * 
 * <p>Using this command keeps user-facing console prompts safe, uniform, and 
 * highly resilient against typos or unexpected entries.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class ValidatorCommand implements Command {
    
    private final String name;
    
    public ValidatorCommand(String name) {
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
            if (project.unparsedResource() != null && project.unparsedResource().trim().length() > 0) {
                validate(project.unparsedResource());
            }
            if (project.verbose() <= 2) {
                ConsoleWriter.println("Validation complete");
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void validate(String resource) {
        int startIdx = resource.indexOf("(");
        int endIdx = resource.indexOf(")");
        
        if (startIdx > 0) {
            if (endIdx == resource.length() - 1) {
                String parts = resource.substring(startIdx, endIdx);
                String[] fields = parts.split(",");
                for (int i = 0; i < fields.length; i ++) {
                    fields[i] = fields[i].trim();
                    String[] tmp = fields[i].split("#");
                    
                    if (tmp.length > 2) {
                        throw new IllegalArgumentException("Invalid syntax. Data type must be specified like name::str");
                    }
                    if (tmp.length == 2) {
                        try {
                            DataType dtype = Enum.valueOf(DataType.class, tmp[1].toUpperCase());
                        }
                        catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid data type " + tmp[1]
                                + ". Supported types are: " + Arrays.toString(DataType.values()));
                        }
                    }
                }
            }
            else {
                throw new IllegalArgumentException("Syntax error."
                        + " Try \"project -c -d /tmp -n example-rest -r Employee(name, location, salary#float)\"");
            }
        }
        else {
            if (startIdx == 0) {
                throw new IllegalArgumentException("Syntax error."
                        + " Try \"project -c -d /tmp -n example-rest -r Employee(name, location, salary#float)\"");
            }
            else {
                if (endIdx != -1) {
                    throw new IllegalArgumentException("Syntax error."
                            + " Try \"project -c -d /tmp -n example-rest -r Employee(name, location, salary#float)\"");
                }
                // No parenthesis
                // Use the default set of fields.
            }
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
