package org.javalabs.decl.api.cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JaxbJpaConverterBridge;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;

/**
 * A command utility that automatically generates JPA entity classes by reading an orm.xml file.
 * 
 * <p>This class acts as a template-based generation tool that reverse-engineers 
 * XML configuration into concrete Java code. It parses standard object-relational mapping 
 * files (like {@code orm.xml}), extracts metadata definitions for entity structures, and 
 * merges that information with preset templates to write complete Java source files.</p>
 * 
 * <p>The command reads mapping instructions from the XML file including:</p>
 * <ul>
 *   <li>Entity definitions and corresponding database table names.</li>
 *   <li>Field classifications, database column constraints, and primary key identifiers.</li>
 *   <li>Entity-to-entity relationship hierarchies such as one-to-many or many-to-one maps.</li>
 * </ul>
 * 
 * <p>Using this command allows development teams to maintain an XML-first configuration 
 * structure while instantly updating the physical Java source code layer automatically.</p>
 * 
 * @author Sudiptasish Chanda
 * 
 * @see jakarta.persistence.Entity
 * @see <a href="https://jakarta.ee">Jakarta Persistence API Specification</a>
 * @see <a href="https://oracle.com">Java XML DocumentBuilder API</a>
 */
public class OrmEntityModelCommand implements Command {
    
    private static final String ORM_XML = "orm.xml";
    private final String name;
    private final JaxbJpaConverterBridge bridge;
    
    public OrmEntityModelCommand(String name) {
        this.name = name;
        this.bridge = new JaxbJpaConverterBridge();
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
                    + project.modelPkg().replace('.', '/');

            String ormXml = readOrmXml(ctx, project);
            
            Map<String, JavaClass> classes = bridge.toJavaClass(ormXml);
            ctx.add("resource.names", classes);
            
            LinkedHashMap<String, String> rawContents = new LinkedHashMap<>();
            for (Map.Entry<String, JavaClass> me : classes.entrySet()) {
                String raw = bridge.serialize(me.getValue());
                rawContents.put(me.getKey(), raw);
            }
            write(project, destDir, rawContents);
            
            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Generated " + rawContents.size() + " jpa entities from orm.xml" + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public void write(Project project, String destDir, Map<String, String> classMapping) throws IOException {
        for (Map.Entry<String, String> me : classMapping.entrySet()) {
            Files.write(
                    Paths.get(destDir + File.separator + me.getKey() + ".java")
                    , me.getValue().getBytes()
                    , StandardOpenOption.CREATE_NEW);
        }
    }
    
    private String readOrmXml(Context ctx, Project project) throws IOException {
        String ormXml = (String)ctx.get("orm.xml");
        if (ormXml == null) {
            // Generate routing-config.xml file
            byte[] buff = StreamUtil.read(project.ormPath());
            ormXml = new String(buff);
            ormXml = ormXml.replace("{MODEL_PACKAGE}", project.modelPkg());
        }
        return ormXml;
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
