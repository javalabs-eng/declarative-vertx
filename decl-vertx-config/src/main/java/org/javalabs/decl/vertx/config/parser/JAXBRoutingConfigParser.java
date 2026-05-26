package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.vertx.config.internal.ConfigStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JAXB compliant parser class for parsing the vertx routing config xml file.
 * 
 * 
 * <p>
 * JAXB (Java Architecture for XML Binding) simplifies XML parsing by automatically converting XML 
 * documents into Java objects and vice versa. It works by mapping XML schemas to Java classes and 
 * using annotations to control the mapping.
 *
 * @author schan280
 */
public final class JAXBRoutingConfigParser implements RoutingConfigParser {
    
    private static final String DEFAULT_CONFIG = "routing-config.xml";
    private static final String DEFAULT_SCHEMA = "schema/routing-config.xsd";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JAXBRoutingConfigParser.class);
    
    JAXBRoutingConfigParser() {}
    
    @Override
    public RoutingConfig read() {
        return read(DEFAULT_CONFIG);
    }
    
    @Override
    public RoutingConfig read(String xmlConfig) {
        try {
            // Read the entire content of the file.
            byte[] xmlBuff = StreamUtil.read(xmlConfig);
            RoutingConfig routingConfig = read(xmlBuff);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Parsed http routing configuration file: {}", xmlConfig);
            }
            ConfigStorage.get().store(routingConfig);
            
            return routingConfig;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RoutingConfig read(byte[] blob) {
        try {
            JAXBContext jaxb = JAXBContext.newInstance(RoutingConfig.class);
            
            // Read the schema file.
            byte[] xsdBuff = StreamUtil.read(DEFAULT_SCHEMA);
            
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new ByteArrayInputStream(xsdBuff)));
            
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(schema);
            
            RoutingConfig routingConfig = (RoutingConfig) unmarshaller.unmarshal(new ByteArrayInputStream(blob));
            verify(routingConfig);
            
            return routingConfig;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void verify(RoutingConfig routingConfig) throws IllegalArgumentException, IllegalAccessException {
        setupDefaults(routingConfig);
    }
    
    private void setupDefaults(Object obj) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isSynthetic()
                || field.getName().equals("serialVersionUID")
                || Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isInterface(field.getModifiers())
                || Modifier.isNative(field.getModifiers())) {

                continue;
            }
            if (isPrimitive(field)) {
                XmlElement ann = field.getAnnotation(XmlElement.class);
                if (ann != null) {
                    field.setAccessible(true);
                    String val = ann.defaultValue();
                    if (field.get(obj) == null && val != null && ! val.equals("\u0000")) {
                        typeSafeSetup(obj, field, val);
                    }
                }
            }
        }
    }
    
    private void typeSafeSetup(Object obj, Field field, String val) throws IllegalArgumentException, IllegalAccessException {
        if (field.getType() == Byte.class) {
            field.set(obj, Byte.valueOf(val));
        }
        else if (field.getType() == Short.class) {
            field.set(obj, Short.valueOf(val));
        }
        else if (field.getType() == Integer.class) {
            field.set(obj, Integer.valueOf(val));
        }
        else if (field.getType() == Long.class) {
            field.set(obj, Long.valueOf(val));
        }
        else if (field.getType() == Float.class) {
            field.set(obj, Float.valueOf(val));
        }
        else if (field.getType() == Double.class) {
            field.set(obj, Double.valueOf(val));
        }
        else if (field.getType() == Boolean.class) {
            field.set(obj, Boolean.valueOf(val));
        }
        else if (field.getType() == String.class) {
            field.set(obj, val);
        }
    }
    
    private Boolean isPrimitive(Field field) {
        return field.getType() == Byte.class
                || field.getType() == Short.class
                || field.getType() == Integer.class
                || field.getType() == Long.class
                || field.getType() == Float.class
                || field.getType() == Double.class
                || field.getType() == Boolean.class
                || field.getType() == String.class;
    }
}
