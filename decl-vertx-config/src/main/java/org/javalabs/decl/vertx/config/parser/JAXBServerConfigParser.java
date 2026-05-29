package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.CrossOrigin;
import org.javalabs.decl.vertx.jaxb.HttpOpts;
import org.javalabs.decl.vertx.jaxb.NetworkOpts;
import org.javalabs.decl.vertx.jaxb.WebServerConfig;
import org.javalabs.decl.vertx.jaxb.ServerOpts;
import org.javalabs.decl.vertx.jaxb.SslOpts;
import org.javalabs.decl.vertx.jaxb.TcpOpts;
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
 * JAXB compliant parser class for parsing the vertx server xml file.
 * 
 * 
 * <p>
 * JAXB (Java Architecture for XML Binding) simplifies XML parsing by automatically converting XML 
 * documents into Java objects and vice versa. It works by mapping XML schemas to Java classes and 
 * using annotations to control the mapping.
 *
 * @author Sudiptasish Chanda
 */
public final class JAXBServerConfigParser implements ServerConfigParser {
    
    private static final String DEFAULT_CONFIG = "server.xml";
    private static final String DEFAULT_SCHEMA = "schema/server.xsd";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JAXBServerConfigParser.class);
    
    JAXBServerConfigParser() {}
    
    @Override
    public WebServerConfig read() {
        return read(DEFAULT_CONFIG);
    }
    
    @Override
    public WebServerConfig read(String xmlConfig) {
        try {
            JAXBContext jaxb = JAXBContext.newInstance(WebServerConfig.class);
            
            // Read the schema file.
            byte[] xsdBuff = StreamUtil.read(DEFAULT_SCHEMA);
            
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new ByteArrayInputStream(xsdBuff)));
            
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(schema);
            
            // Read the entire content of the file.
            byte[] xmlBuff = StreamUtil.read(xmlConfig);
            
            WebServerConfig serverConfig = (WebServerConfig) unmarshaller.unmarshal(new ByteArrayInputStream(xmlBuff));
            verify(serverConfig);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Parsed http server configuration file: {}", xmlConfig);
            }
            ConfigStorage.get().store(serverConfig);
            
            return serverConfig;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void verify(WebServerConfig config) throws IllegalArgumentException, IllegalAccessException {
        setupDefaults(config);
        
        ServerOpts serverOpts = config.getServerOpts();
        setupDefaults(serverOpts);
        
        if (config.getTcpOpts()== null) {
            config.setTcpOpts(new TcpOpts());
        }
        setupDefaults(config.getTcpOpts());
        
        if (config.getTcpOpts().getSslOpts() == null) {
            config.getTcpOpts().setSslOpts(new SslOpts());
        }
        setupDefaults(config.getTcpOpts().getSslOpts());
        
        if (config.getNetworkOpts() == null) {
            config.setNetworkOpts(new NetworkOpts());
        }
        setupDefaults(config.getNetworkOpts());
        
        if (config.getHttpOpts() == null) {
            config.setHttpOpts(new HttpOpts());
        }
        setupDefaults(config.getHttpOpts());
        
        if (config.getCrossOrigin() == null) {
            config.setCrossOrigin(new CrossOrigin());
        }
        setupDefaults(config.getCrossOrigin());
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
