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
 * A command utility that automatically generates authentication-related classes.
 * 
 * <p>This command automates the creation of core authentication components, 
 * such as security configurations, user services, and token handlers. It also 
 * updates and builds the necessary Swagger or OpenAPI models so that login, 
 * logout, and token registration endpoints show up correctly in your API docs.</p>
 * 
 * <p>This utility is typically run as a code generation tool during development 
 * or as part of a setup script to bootstrap security structures inside the project.</p>
 * 
 * @author Sudiptasish Chanda
 */
public class AuthCommand implements Command {
    
    private final Map<String, String> templateMapping = Map.of(
            "AppUser.java", "app_user.template",
            "AppUserImpl.java", "app_user_impl.template",
            "Principal.java", "principal.template",
            "UserPrincipal.java", "user_principal.template",
            "AuthToken.java", "auth_token.template"
    );
    
    private final String name;
    
    public AuthCommand(String name) {
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
                    + project.authPkg().replace('.', '/');
            
            String authDir = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + "common" + File.separator
                    + "auth";
            
            for (Map.Entry<String, String> me : templateMapping.entrySet()) {
                String template = authDir + File.separator + me.getValue();
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
        content = content.replace("{PACKAGE}", project.authPkg());

        File file = new File(destDir + File.separator + className + ".java");
        ClassWriter.write(file, content, project.verbose());
        
        if (project.verbose() <= 2) {
            ConsoleWriter.timingPrintln("Created auth file: " + ConsoleWriter.ANSI_GREEN + file.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
