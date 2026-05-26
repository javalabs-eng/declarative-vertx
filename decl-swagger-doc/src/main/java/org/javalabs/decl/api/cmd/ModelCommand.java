package org.javalabs.decl.api.cmd;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaPackage;
import org.javalabs.decl.gen.JavaVariable;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.visitor.JavaClassVisitor;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 *
 * @author schan280
 */
public class ModelCommand implements Command {
    
    private final String name;
    
    public ModelCommand(String name) {
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
            Project.InputResource input = project.inputResource();

            // Prepare the model class.
            JavaClass jClass = new JavaClass(input.resource());
            jClass.pkg(new JavaPackage(project.modelPkg()));

            List<String> fields = input.fields();
            List<Class<?>> dataTypes = input.dateTypes();

            for (int i = 0; i < fields.size(); i ++) {
                String field = fields.get(i);
                Class<?> dataType = dataTypes.get(i);
                JavaVariable jVar = new JavaVariable(jClass).type(dataType).name(field);
                
                String lowerField = field.toLowerCase();
                if (lowerField.endsWith("id") || lowerField.endsWith("identifier")) {
                    jVar.idField(Boolean.TRUE);
                }
                jClass.addVar(jVar);
            }
            // Add the special variable and method canonicalLink
            jClass.addVar(new JavaVariable(jClass).type(String.class).name("canonicalLink"));
            
            jClass.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_BOTH);
            jClass.autoGenMethod(Boolean.TRUE);
            jClass.freeze();
            
            JavaClassVisitor visitor = new JavaClassVisitor();
            jClass.accept(visitor);
            String content = visitor.result();
            
            String destDir = projectRoot.getAbsolutePath()
                    + File.separator
                    + project.srcDir()
                    + File.separator
                    + project.modelPkg().replace('.', '/');
            
            File itemFile = new File(destDir + File.separator + jClass.name() + ".java");
            ClassWriter.write(itemFile, content, project.verbose());

            // Set the JavaClass as it is needed while creating other handler classes.
            // project.model(jClass);
            ctx.add("resource.names", Map.of(jClass.name(), jClass));

            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created model file: " + ConsoleWriter.ANSI_GREEN + (destDir + File.separator + jClass.name() + ".java") + ConsoleWriter.ANSI_RESET);
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
