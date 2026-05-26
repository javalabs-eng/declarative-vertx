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
 * Calling this method will create an empty project workspace.
 *  
 * <p>
 * Along with that it will create the following directory hierarchies.
 * <ul>
 *   <li>Source directory</li>
 *   <li>Source Resource directory</li>
 *   <li>Test directory</li>
 *   <li>Test Resource directory</li>
 *   <li>Target directory for generated class files</li>
 * </ul>
 * 
 * @author schan280
 */
public class WorkspaceCommand implements Command {
    
    private final String name;
    
    public WorkspaceCommand(String name) {
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
        
            // Create project root folder.
            projectRoot.mkdirs();
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created project: " + ConsoleWriter.ANSI_GREEN + project.name() + ConsoleWriter.ANSI_RESET);
            }

            // Create source and target path.
            File srcDir = new File(projectRoot, project.srcDir());
            srcDir.mkdirs();

            File testDir = new File(projectRoot, project.testDir());
            testDir.mkdirs();

            // Create source resource directory
            File srcResourceDir = new File(projectRoot, project.srcResourceDir());
            srcResourceDir.mkdirs();

            // Create test resource directory
            File testResourceDir = new File(projectRoot, project.testResourceDir());
            testResourceDir.mkdirs();
            
            // Create test integ directory
            File testIntegDir = new File(projectRoot, project.testDir()
                    + File.separator + project.basePkg().replace('.', '/')
                    + File.separator + "integ");
            testIntegDir.mkdirs();

            File targetDir = new File(projectRoot, project.targetDir());
            targetDir.mkdirs();
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created source and test directories");
            }
            
            File generatedDir = new File(project.generated());
            if (! generatedDir.exists()) {
                generatedDir.mkdirs();
                if (project.verbose() <= 2) {
                    ConsoleWriter.timingPrintln("Created generated directory for classes");
                }
            }
            
            // Create the handler package
            File srcHDir = new File(srcDir, project.handlerPkg().replace('.', '/'));
            srcHDir.mkdirs();
            
            // Create the model package
            File srcMDir = new File(srcDir, project.modelPkg().replace('.', '/'));
            srcMDir.mkdirs();
            
            // Create the main package
            File mainDir = new File(srcDir, project.mainPkg().replace('.', '/'));
            mainDir.mkdirs();
            
            // Following packages are needed only if it is an end-to-end project.
            if (ctx.currentChain().name().equals("java_project_e2e") || ctx.currentChain().name().equals("java_project_e2e_db")) {
                // Create db schema directory.
                File dbSchemaDir = new File(projectRoot, project.dbDir());
                dbSchemaDir.mkdirs();

                // Create source packages
                File coreMDir = new File(srcDir, project.corePkg().replace('.', '/'));
                coreMDir.mkdirs();

                File utilMDir = new File(srcDir, project.utilPkg().replace('.', '/'));
                utilMDir.mkdirs();

                File cfgMDir = new File(srcDir, project.configPkg().replace('.', '/'));
                cfgMDir.mkdirs();

                File srcBDir = new File(srcDir, project.boPkg().replace('.', '/'));
                srcBDir.mkdirs();

                File srcDDir = new File(srcDir, project.daoPkg().replace('.', '/'));
                srcDDir.mkdirs();

                File srcADir = new File(srcDir, project.authPkg().replace('.', '/'));
                srcADir.mkdirs();
            }
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created package hierarchy");
            }

            File docDir = new File(projectRoot, project.docDir());
            docDir.mkdirs();
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created api doc directory");
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
