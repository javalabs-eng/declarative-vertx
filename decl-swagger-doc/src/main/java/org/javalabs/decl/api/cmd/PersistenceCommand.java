package org.javalabs.decl.api.cmd;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.orm.jaxb.PersistenceType;
import org.javalabs.orm.jaxb.PersistenceUnitType;
import org.javalabs.orm.jaxb.PropertiesType;
import org.javalabs.orm.jaxb.PropertyType;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * Create the jpa compliant persistence configuration file.
 *
 * @author Sudiptasish Chanda
 */
public class PersistenceCommand implements Command {
    
    private static final String PERSISTENCE_CONFIG  = "persistence.xml";
    
    private final String name;
    
    public PersistenceCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        return fromEntities(ctx);
    }
    
    public Future<?> fromEntities(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        
        try {
            // Generate persistence.xml
            PersistenceType persistence = new PersistenceType();
            persistence.setVersion(new BigDecimal(2));
            
            PersistenceUnitType unitType = new PersistenceUnitType();
            unitType.setName(project.name().toLowerCase() + "-" + "pu");
            unitType.setDescription("Persistence Unit for " + project.name());
            unitType.setProvider("org.javalabs.jpa.LitePersistenceProvider");
            unitType.setSharedCacheMode("NONE");
            persistence.setPersistenceUnit(unitType);
            
            PropertiesType propsType = new PropertiesType();
            unitType.setProperties(propsType);
            
            PropertyType propType = new PropertyType();
            propType.setName("jpa-lite.entity.package");
            propType.setValue(project.modelPkg());
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.jdbc.url");
            propType.setValue("jdbc:h2:mem:" + project.dbName() + ";DB_CLOSE_DELAY=-1");
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.jdbc.user");
            propType.setValue(project.dbUser());
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.jdbc.password");
            propType.setValue(project.dbPassword());
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.schema-generation.scripts.action");
            propType.setValue("drop-and-create");
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.schema-generation.scripts.create-target");
            propType.setValue("db/schema.sql");
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.sql-load-script-source");
            propType.setValue("db/user.sql");
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("javax.persistence.schema-generation.create-database-schemas");
            propType.setValue("true");
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("jpa-lite.logging.level");
            propType.setValue("TRACE");
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("jpa-lite.dao.package");
            propType.setValue(project.daoPkg());
            propsType.getProperty().add(propType);
            
            propType = new PropertyType();
            propType.setName("jpa-lite.post.construct.listener");
            propType.setValue("org.javalabs.jpa.util.CreateScriptListener");
            propsType.getProperty().add(propType);
            
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            for (Map.Entry<String, JavaClass> me : classes.entrySet()) {
                JavaClass jClass = me.getValue();
                unitType.getClazz().add(jClass.pkg() + "." + jClass.name());
            }
            
            JAXBContext context = JAXBContext.newInstance(PersistenceType.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
            marshaller.marshal(persistence, out);
            out.flush();
            
            String xml = new String(out.toByteArray());
            
            File projectRoot = new File(project.dir(), project.name());
            File cfgFile = new File(projectRoot.getAbsolutePath() + File.separator + project.srcResourceDir(), PERSISTENCE_CONFIG);
            ClassWriter.write(cfgFile, xml, project.verbose());

            if (project.verbose() <= 2) {
                ConsoleWriter.timingPrintln("Created JPA " + ConsoleWriter.ANSI_GREEN + PERSISTENCE_CONFIG + ConsoleWriter.ANSI_RESET);
            }
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (JAXBException | IOException | RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
