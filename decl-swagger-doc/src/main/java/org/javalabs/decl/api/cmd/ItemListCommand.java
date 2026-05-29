package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaAnnotation;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaImport;
import org.javalabs.decl.gen.JavaPackage;
import org.javalabs.decl.gen.JavaVariable;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.visitor.JavaClassVisitor;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * A utility command to generate the item list class needed for rest based interaction.
 *
 * @author Sudiptasish Chanda
 */
public class ItemListCommand implements Command {
    
    private static final String ITEM_LIST_TEMPLATE = "item_list.template";
    private static final String ITEM_LIST  = "ItemList.java";
    
    private final String name;
    
    public ItemListCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        return fromTemplate(ctx);
    }

    public Future<?> generate(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        
        try {
            File projectRoot = new File(project.dir(), project.name());
            
            JavaClass jClass = new JavaClass("ItemList");
            jClass.pkg(new JavaPackage(project.handlerPkg()));
            jClass.addImport(new JavaImport(Class.forName("com.fasterxml.jackson.annotation.JsonInclude")));
            jClass.addImport(new JavaImport(Class.forName("java.util.List")));
            
            jClass.addAnnotation(new JavaAnnotation(jClass)
                .typeName("jakarta.persistence.IdClass")
                .props(new LinkedHashMap<>() {{ put("value", "JsonInclude.Include.NON_NULL"); }}));
            
            jClass.autoGenConstructor(Boolean.TRUE, JavaClass.CONSTRUCTOR_BOTH);
            
            jClass.addVar(new JavaVariable(jClass).type(Integer.class).name("total"));
            jClass.addVar(new JavaVariable(jClass).type(List.class).name("items"));
            jClass.addVar(new JavaVariable(jClass).type(String.class).name("canonicalLink"));
            jClass.addVar(new JavaVariable(jClass).type(Boolean.class).name("nextLink"));
            jClass.addVar(new JavaVariable(jClass).type(String.class).name("previousLink"));
            
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
            
            File itemFile = new File(destDir + File.separator + ITEM_LIST);
            ClassWriter.write(itemFile, content, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created item list file: " + ConsoleWriter.ANSI_GREEN + itemFile.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (ClassNotFoundException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public Future<?> fromTemplate(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        
        try {
            File projectRoot = new File(project.dir(), project.name());
            
            // Generate routing-config.xml file
            String template = "template" + File.separator
                    + project.platform().name().toLowerCase() + File.separator
                    + ITEM_LIST_TEMPLATE;
                    
            byte[] buff = StreamUtil.read(template);
            String content = new String(buff);
            content = content.replace("{MODEL_PACKAGE}", project.modelPkg());
            
            String destDir = projectRoot.getAbsolutePath()
                    + File.separator
                    + project.srcDir()
                    + File.separator
                    + project.modelPkg().replace('.', '/');
            
            File itemFile = new File(destDir + File.separator + ITEM_LIST);
            ClassWriter.write(itemFile, content, project.verbose());
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created item list file: " + ConsoleWriter.ANSI_GREEN + itemFile.getAbsolutePath() + ConsoleWriter.ANSI_RESET);
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
