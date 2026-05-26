package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.jaxb.AddressResolverOptions;
import org.javalabs.decl.vertx.jaxb.ContextParams;
import org.javalabs.decl.vertx.jaxb.EventBusOptions;
import org.javalabs.decl.vertx.jaxb.FileSystemOptions;
import org.javalabs.decl.vertx.jaxb.MetricsOptions;
import org.javalabs.decl.vertx.jaxb.Verticle;
import org.javalabs.decl.vertx.jaxb.VertxWeb;
import org.javalabs.decl.vertx.jaxb.DeployOptions;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.javalabs.decl.util.StreamUtil;
import org.javalabs.decl.vertx.config.internal.ConfigStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JAXB compliant parser class for parsing the vertx web config file.
 * 
 * 
 * <p>
 * JAXB (Java Architecture for XML Binding) simplifies XML parsing by automatically converting XML 
 * documents into Java objects and vice versa. It works by mapping XML schemas to Java classes and 
 * using annotations to control the mapping.
 *
 * @author schan280
 */
public final class JAXBWebConfigParser implements WebConfigParser {
    
    private static final String DEFAULT_CONFIG = "vertx-web.xml";
    private static final String DEFAULT_SCHEMA = "schema/vertx-web.xsd";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JAXBWebConfigParser.class);
    
    JAXBWebConfigParser() {}
    
    @Override
    public VertxWeb read() {
        return read(DEFAULT_CONFIG);
    }
    
    public VertxWeb read(String xmlConfig) {
        try {
            JAXBContext jaxb = JAXBContext.newInstance(VertxWeb.class);
            
            // Read the schema file.
            byte[] xsdBuff = StreamUtil.read(DEFAULT_SCHEMA);
            
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new ByteArrayInputStream(xsdBuff)));
            
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(schema);
            
            // Read the entire content of the file.
            byte[] xmlBuff = StreamUtil.read(xmlConfig);
            
            VertxWeb webConfig = (VertxWeb) unmarshaller.unmarshal(new ByteArrayInputStream(xmlBuff));
            verify(webConfig);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Parsed vertx web configuration file: {}", xmlConfig);
            }
            ConfigStorage.get().store(webConfig);
            
            return webConfig;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void verify(VertxWeb webConfig) throws IllegalArgumentException, IllegalAccessException {
        if (webConfig.getContextParams() == null) {
            webConfig.setContextParams(new ContextParams());
        }
        ContextParams params = webConfig.getContextParams();
        setupDefaults(params);
        
        if (params.getEventBusOptions() == null) {
            params.setEventBusOptions(new EventBusOptions());
        }
        setupDefaults(params.getEventBusOptions());
        
        if (params.getAddressResolverOptions() == null) {
            params.setAddressResolverOptions(new AddressResolverOptions());
        }
        setupDefaults(params.getAddressResolverOptions());
        
        if (params.getMetricsOptions() == null) {
            params.setMetricsOptions(new MetricsOptions());
        }
        setupDefaults(params.getMetricsOptions());
        
        if (params.getFileSystemOptions() == null) {
            params.setFileSystemOptions(new FileSystemOptions());
        }
        setupDefaults(params.getFileSystemOptions());
        
        List<Verticle> verticles = webConfig.getVerticles().getVerticle();
        for (Verticle verticle : verticles) {
            if (verticle.getDeployOptions() == null) {
                verticle.setDeployOptions(new DeployOptions());
            }
            setupDefaults(verticle.getDeployOptions());
            if (verticle.getDeployOptions().isWorker()) {
                verticle.getDeployOptions().setThreadingModel("WORKER");
            }
        }
        
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
