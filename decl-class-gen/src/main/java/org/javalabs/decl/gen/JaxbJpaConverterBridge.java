package org.javalabs.decl.gen;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.visitor.JavaClassVisitor;
import org.javalabs.orm.jaxb.EntityMappingsType;
import org.javalabs.orm.jaxb.EntityType;

/**
 *
 * @author Sudiptasish Chanda
 */
public class JaxbJpaConverterBridge {
    
    private final EntityConverter converter;
    
    public JaxbJpaConverterBridge() {
        this.converter = new EntityConverter();
    }
    
    public Map<String, JavaClass> toJavaClass(String ormXml) {
        try {
            LinkedHashMap<String, JavaClass> classes = new LinkedHashMap<>();

            JAXBContext jContext = JAXBContext.newInstance(EntityMappingsType.class);
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            InputStream in = new ByteArrayInputStream(ormXml.getBytes());

            EntityMappingsType mapping = (EntityMappingsType) unmarshaller.unmarshal(in);
            in.close();

            List<EntityType> entities = mapping.getEntity();
            for (EntityType entity : entities) {
                // If any entity has composite primary key, it will be ignored.
                if (entity.getAttributes().getId() != null && entity.getAttributes().getId().size() >= 2) {
                    ConsoleWriter.timingPrintln(ConsoleWriter.ANSI_RED + "Skipped class generation for " + entity.getName() + " due to presence of composite key. Generate them manually" + ConsoleWriter.ANSI_RESET);
                    continue;
                }
                if (entity.getAttributes().getId() == null || entity.getAttributes().getId().isEmpty()) {
                    ConsoleWriter.timingPrintln(ConsoleWriter.ANSI_RED + "Skipped class generation for " + entity.getName() + " due to absense of primary key. Generate them manually" + ConsoleWriter.ANSI_RESET);
                    continue;
                }
                JavaClass jClass = converter.toJavaClass(entity, mapping.getPackage());
                jClass.freeze();
                
                classes.put(entity.getName(), jClass);
            }
            return classes;
        }
        catch (IOException | JAXBException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map<String, String> toRawClass(String ormXml) {
        Map<String, JavaClass> classes = toJavaClass(ormXml);
        
        LinkedHashMap<String, String> rawContents = new LinkedHashMap<>();
        for (Map.Entry<String, JavaClass> me : classes.entrySet()) {
            String raw = serialize(me.getValue());
            rawContents.put(me.getKey(), raw);
        }
        return rawContents;
    }
    
    public String serialize(JavaClass jClass) {
        JavaClassVisitor visitor = new JavaClassVisitor();
        jClass.accept(visitor);
        return visitor.result();
    }
}
