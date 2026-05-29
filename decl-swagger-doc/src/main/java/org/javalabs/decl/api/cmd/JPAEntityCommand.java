package org.javalabs.decl.api.cmd;

import org.javalabs.jpa.entity.EntityGenerator;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command utility that automatically generates JPA entity classes by connecting to a remote database.
 * 
 * <p>This class establishes a connection to a live, remote database and inspects its schema 
 * metadata. It discovers tables, columns, primary keys, and foreign key relationships. It then 
 * merges this database layout with preset templates to write complete, valid Java source files 
 * marked with standard Jakarta or Java Persistence API annotations.</p>
 * 
 * <p>The generated classes include mapping configurations such as:</p>
 * <ul>
 *   <li>{@link jakarta.persistence.Table} and {@link jakarta.persistence.Column} declarations.</li>
 *   <li>Primary key strategies using {@link jakarta.persistence.GeneratedValue}.</li>
 *   <li>Relationship definitions like {@link jakarta.persistence.OneToMany} or {@link jakarta.persistence.ManyToOne}.</li>
 * </ul>
 * 
 * <p>Using this command speeds up project bootstrapping by replacing manual schema-to-code mapping 
 * with automated reverse engineering.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see jakarta.persistence.Entity
 * @see <a href="https://jakarta.ee">Jakarta Persistence API Specification</a>
 * @see <a href="https://oracle.com">Java DatabaseMetaData API</a>
 */
public class JPAEntityCommand implements Command {
    
    private final String name;
    
    public JPAEntityCommand(String name) {
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
                    + project.srcResourceDir();

            Map<String, Object> params = new HashMap<>();
            params.put("model.gen", "true");
            params.put("jpa-lite.dialect", project.dbDialect());
            params.put("jpa-lite.entity.package", project.modelPkg());
            params.put("javax.persistence.jdbc.host", project.dbHost());
            params.put("javax.persistence.jdbc.port", project.dbPort());
            params.put("javax.persistence.jdbc.db", project.dbName());
            params.put("javax.persistence.jdbc.schema", project.dbSchema());
            params.put("javax.persistence.jdbc.user", project.dbUser());
            params.put("javax.persistence.jdbc.password", project.dbPassword());
            
            // params.put("sample.table.name", "fk_");
            params.put("exclude.entity.name.prefix", "fks_");
            params.put("verbose.log", "N");
            
            String ormXml = EntityGenerator.extract(projectRoot.getAbsolutePath(), params);
            ctx.add("orm.xml", ormXml);
            
            Files.write(
                    Paths.get(destDir + File.separator + "orm.xml")
                    , ormXml.getBytes()
                    , StandardOpenOption.CREATE_NEW);
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Generated orm.xml with jpa entities from remote db."
                        + " Size: " + ormXml.length() + " bytes" + ConsoleWriter.ANSI_RESET);
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
