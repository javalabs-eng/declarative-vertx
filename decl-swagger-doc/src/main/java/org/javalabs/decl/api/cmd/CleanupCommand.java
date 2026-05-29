package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * This utility command is used to cleanup an existing project.
 * 
 * <p>
 * Calling this method will typically remove all generated build artifacts like source codes, configuration file,
 * class files and temporary files, effectively resetting the project to a clean state.
 *
 * @author Sudiptasish Chanda
 */
public class CleanupCommand implements Command {
    
    private final String name;
    
    public CleanupCommand(String name) {
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
            if (projectRoot.exists()) {
                Path path = Paths.get(projectRoot.toURI());

                Files.walkFileTree(path, 
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    }
                );
                if (project.verbose() <= 2) {
                    ConsoleWriter.timingPrintln("Deleted existing project: " + ConsoleWriter.ANSI_RED + projectRoot.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
                }
            }
            else {
                if (project.verbose() <= 2) {
                    ConsoleWriter.timingPrintln("No project workspace found. Creating new ...");
                }
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
