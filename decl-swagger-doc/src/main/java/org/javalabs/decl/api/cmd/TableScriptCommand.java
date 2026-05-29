package org.javalabs.decl.api.cmd;

import jakarta.persistence.PersistenceConfiguration;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.compile.CompiledClassHolder;
import org.javalabs.decl.compile.InMemoryCompiler;
import org.javalabs.decl.compile.MemoryClassLoader;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command utility that automatically generates SQL table creation scripts from JPA entities.
 * 
 * <p>This class reverse-engineers Java Persistence API (JPA) or Jakarta Persistence model 
 * definitions into physical database structural code. It analyzes entity class decorations 
 * and maps them directly to standard SQL data definitions, allowing you to instantly 
 * create schema scripts without writing SQL code by hand.</p>
 * 
 * <p>The generated script handles common schema conversions including:</p>
 * <ul>
 *   <li>Translating {@link jakarta.persistence.Table} and {@link jakarta.persistence.Column} declarations into table and column names.</li>
 *   <li>Mapping basic Java data types (like {@code String}, {@code int}, or {@code LocalDateTime}) to database-specific types.</li>
 *   <li>Configuring constraint definitions such as primary keys, unique fields, and nullability flags.</li>
 * </ul>
 * 
 * <p>Using this command helps development teams generate clear SQL scripts for database 
 * migration, initial schema building, or deployment tasks straight from their Java models.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see jakarta.persistence.Entity
 * @see <a href="https://jakarta.ee">Jakarta Persistence Specification</a>
 * @see <a href="https://oracle.com">Java Reflection Field API</a>
 */
public class TableScriptCommand implements Command {
    
    private final String name;
    
    public TableScriptCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        
        try {
            CompiledClassHolder ccHolder = new CompiledClassHolder();
            MemoryClassLoader loader = new MemoryClassLoader(current, ccHolder);
            
            Thread.currentThread().setContextClassLoader(loader);
            
            File projectRoot = new File(project.dir(), project.name());
            String destDir = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.dbDir();
            
            PersistenceConfiguration config = new PersistenceConfiguration(project.name().toLowerCase() + "-" + "pu");
            config.property("javax.persistence.schema-generation.scripts.action", "drop-and-create");
            config.property("javax.persistence.schema-generation.scripts.create-target", destDir + File.separator + "schema.sql");
            config.property("jpa-lite.dialect", "h2");
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            String[] fileNames = new String[classes.size()];
            String[] contents = new String[classes.size()];
            int idx = 0;
            
            for (Map.Entry<String, JavaClass> me : classes.entrySet()) {
                JavaClass jClass = me.getValue();
                
                String modelFile = projectRoot.getAbsolutePath()
                        + File.separator
                        + project.srcDir()
                        + File.separator
                        + project.modelPkg().replace('.', '/')
                        + File.separator
                        + jClass.name() + ".java";
                
                byte[] buff = StreamUtil.read(modelFile);
                
                fileNames[idx] = jClass.name() + ".java";
                contents[idx] = new String(buff);
                
                idx ++;
            }
            Boolean succes = InMemoryCompiler.compile(fileNames, contents, ccHolder);
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Compiled " + fileNames.length + " java file(s) in the memory" + ConsoleWriter.ANSI_RESET);
            }
            for (String fileName : fileNames) {
                Class<?> clazz = loader.loadClass(project.modelPkg() + "." + fileName.substring(0, fileName.indexOf(".")));
                config.managedClass(clazz);
            }
            Class<?> generator = loader.loadClass("org.javalabs.jpa.schema.SchemaGenerator");
            Method method = generator.getDeclaredMethod("generate", PersistenceConfiguration.class);
            method.invoke(null, config);
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Generated table script " + destDir + File.separator + "schema.sql" + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (IOException | ReflectiveOperationException | RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
    
    public void write(Project project, Map<String, String> classMapping) throws IOException {
        for (Map.Entry<String, String> me : classMapping.entrySet()) {
            Files.write(
                    Paths.get(project.dir()
                            , project.srcDir()
                                    + File.separator + project.modelPkg().replace('.', File.separatorChar)
                                    + File.separator + me.getKey() + ".java")
                    , me.getValue().getBytes()
                    , StandardOpenOption.CREATE_NEW);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
