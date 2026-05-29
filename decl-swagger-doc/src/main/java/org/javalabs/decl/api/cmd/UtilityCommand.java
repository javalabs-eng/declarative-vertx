package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;
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
 * A command utility class to generate few utility classes required for the target project.
 *
 * @author Sudiptasish Chanda
 */
public class UtilityCommand implements Command {
    
    private final Map<String, String> templateMapping = Map.of(
            // "DateUtil.java", "date_util.template",
            // "MapperUtil.java", "mapper_util.template",
            // "StopWatch.java", "stop_watch.template",
            "QueryParams.java", "query_params.template",
            "SearchCriteria.java", "search_criteria.template",
            "SearchCriteriaImpl.java", "search_criteria_impl.template"
    );
    
    private final String name;
    
    public UtilityCommand(String name) {
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
                    + project.srcDir()
                    + File.separator
                    + project.utilPkg().replace('.', '/');
            
            String utilDir = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "util";
            
            for (Map.Entry<String, String> me : templateMapping.entrySet()) {
                String template = utilDir + File.separator + me.getValue();
                write(project, destDir, template, me.getKey().substring(0, me.getKey().indexOf(".")));
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void write(Project project, String destDir, String template, String className) throws IOException {
        byte[] buff = StreamUtil.read(template);
            
        // Now, look for the following customizer sequentially and start replacing the code
        String content = new String(buff);
        content = content.replace("{PACKAGE}", project.utilPkg());

        File file = new File(destDir + File.separator + className + ".java");
        ClassWriter.write(file, content, project.verbose());
        
        if (project.verbose() <= 2) {
            ConsoleWriter.timingPrintln("Created utility file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
