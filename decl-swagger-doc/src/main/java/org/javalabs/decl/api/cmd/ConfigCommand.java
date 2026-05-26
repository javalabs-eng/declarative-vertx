package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.FileHandlerUtil;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;
import org.javalabs.jpa.dialect.Dialect;
import org.javalabs.jpa.dialect.SQLDialect;

/**
 *
 * @author schan280
 */
public class ConfigCommand implements Command {
    
    private static final String CFG_TEMPLATE = "app_config.template";
    private static final String JSON_TEMPLATE = "app.json.template";
    
    private final String name;
    
    public ConfigCommand(String name) {
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
            // Create ApplicationConfiguration class.
            
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "config" + File.separator
                    + CFG_TEMPLATE;
                    
            write(project, projectRoot, template, "ApplicationConfiguration");
            
            template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + JSON_TEMPLATE;
            
            byte[] buff = FileHandlerUtil.read(template);
            
            Map<String, Object> map = MapperUtil.decode(buff, HashMap.class);
            Map<String, Object> dbConfig = (Map)map.get("db.config");
            Dialect dialect = Enum.valueOf(Dialect.class, project.dbDialect().toUpperCase());
            SQLDialect sqlDialect = ObjectCreator.create(dialect.dialectClass());
            
            dbConfig.put("url", sqlDialect.db_url(project.dbHost(), project.dbPort(), project.dbName()));
            dbConfig.put("user", project.dbUser());
            dbConfig.put("password", project.dbPassword());
            
            buff = MapperUtil.prettyWrite(map);
            
            // Create an empty json file "app.json" 
            String destDir = projectRoot.getAbsolutePath()
                + File.separator
                + project.srcResourceDir();
            
            File appJson = new File(destDir, "app.json");
            ClassWriter.write(appJson, new String(buff), project.verbose());
            
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void write(Project project, File projectRoot, String template, String className) throws IOException {
        byte[] buff = FileHandlerUtil.read(template);
            
        // Now, look for the following customizer sequentially and start replacing the code
        String content = new String(buff);
        content = content.replace("{PACKAGE}", project.configPkg());

        String destDir = projectRoot.getAbsolutePath()
                + File.separator
                + project.srcDir()
                + File.separator
                + project.configPkg().replace('.', '/');

        File file = new File(destDir + File.separator + className + ".java");
        ClassWriter.write(file, content, project.verbose());
        
        if (project.verbose() <= 2) {
            ConsoleWriter.timingPrintln("Created app config file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
