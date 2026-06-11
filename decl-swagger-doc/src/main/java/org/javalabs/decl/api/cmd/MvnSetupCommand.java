package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.CharUtil;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;
import org.javalabs.jpa.dialect.Dialect;
import org.javalabs.jpa.dialect.SQLDialect;

/**
 * A maven based java project.
 * 
 * <p>
 * Maven is an open-source build automation and project management tool widely used for Java applications.
 * As a build automation tool, it automates the source code compilation and dependency management, assembles
 * binary codes into packages, and executes test scripts.
 *
 * @author Sudiptasish Chanda
 */
public class MvnSetupCommand implements Command {
    
    private static final String POM_TEMPLATE = "pom.xml.template";
    private static final String BUILD_FILE   = "pom.xml";
    
    private final String name;
    
    public MvnSetupCommand(String name) {
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
            // Generate pom.xml file
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + project.stack().name().toLowerCase() + File.separator
                    + POM_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            
            String tmp = new String(buff);
            tmp = tmp.replace("{GROUP}", project.basePkg());
            tmp = tmp.replace("{ARTIFACT}", project.name());
            tmp = tmp.replace("{MAIN_PACKAGE}", project.mainPkg());
            tmp = tmp.replace("{PROJECT}", CharUtil.toCapitalisedCamelCase(project.name()));
            tmp = tmp.replace("{JDK_VERSION}", project.jdkVersion());
            
            if (ctx.currentChain().name().equals("java_project_e2e") || ctx.currentChain().name().equals("java_project_e2e_db")) {
                Dialect dialect = Enum.valueOf(Dialect.class, project.dbDialect().toUpperCase());
                SQLDialect sqlDialect = ObjectCreator.create(dialect.dialectClass());
                String gav = sqlDialect.dependency();
                String[] arr = gav.split(":");

                String dependency = 
                        "<dependency>\n" +
                        "            <groupId>" + arr[0] + "</groupId>\n" +
                        "            <artifactId>" + arr[1] + "</artifactId>\n" +
                        "            <version>" + arr[2] + "</version>\n" +
                        "            <scope>runtime</scope>\n" +
                        "        </dependency>";

                tmp = tmp.replace("{DB_DEPENDENCY}", dependency);
            }
            else {
                tmp = tmp.replace("{DB_DEPENDENCY}", "");
            }
            
            File pomFile = new File(project.dir() + File.separator + project.name(), BUILD_FILE);
            ClassWriter.write(pomFile, tmp, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created " + ConsoleWriter.ANSI_GREEN + BUILD_FILE + ConsoleWriter.ANSI_RESET);
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
