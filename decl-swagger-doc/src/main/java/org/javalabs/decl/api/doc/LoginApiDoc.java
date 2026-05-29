package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Media;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Parameter;
import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class LoginApiDoc extends AbstractDoc {
    
    @Override
    public Operation generate(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Operation operation = new Operation();
        operation.setTags(Arrays.asList(name));

        operation.setSummary("Obtain a jwt token by authenticating yourself.");

        Parameter header = new Parameter();
        header.setIn("header");
        header.setName("Authorization");
        header.setDescription("The basic authentication token. base64 (user:password).");
        header.setSchema(new Schema("string", null));
        header.setRequired(Boolean.TRUE);
        header.setExample("Basic feg639A/dge632jmcx=");

        operation.setParameters(Arrays.asList(header));

        // Prepare responses
        Map<Object, Response> responses = new LinkedHashMap<>();

        // Success
        Response response = success(name, contentType);
        responses.put(200, response);

        response = unAuthorized(contentType);
        responses.put(401, response);

        operation.setResponses(responses);
        
        return operation;
    }
    
    private Response success(String name, String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/" + name + "-created"));
        
        Response response = new Response();
        response.setDescription("Ok");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }

    @Override
    public Schema response(String pojo, String uri) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Schema schema = new Schema();
        schema.setProperties(new LinkedHashMap<>());
        
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(pojo);
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isSynthetic()
                    || field.getName().equals("serialVersionUID")
                    || Modifier.isTransient(field.getModifiers())
                    || Modifier.isStatic(field.getModifiers())
                    || Modifier.isInterface(field.getModifiers())
                    || Modifier.isNative(field.getModifiers())) {

                    continue;
                }
                if (field.getType() == String.class) {
                    Property prop = new Property();
                    prop.setType("string");
                    prop.setExample("Any Name");
                    schema.getProperties().put(field.getName(), prop);
                }
                else if (Date.class.isAssignableFrom(field.getType())) {
                    Property prop = new Property();
                    prop.setType("string");
                    prop.setFormat("date");
                    prop.setExample(new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSS").format(new Date()));
                    schema.getProperties().put(field.getName(), prop);
                }
                else if (Number.class.isAssignableFrom(field.getType())) {
                    Property prop = new Property();
                    if (field.getType() == Long.class || field.getType() == long.class || field.getType() == BigInteger.class) {
                        prop.setType("number");
                        prop.setFormat("int64");
                        prop.setExample("3591");
                    }
                    else if (field.getType() == Float.class || field.getType() == float.class
                            || field.getType() == Double.class || field.getType() == double.class
                            || field.getType() == BigDecimal.class) {
                        prop.setType("number");
                        prop.setFormat("double");
                        prop.setExample("2045.75");
                    }
                    else {
                        prop.setType("integer");
                        prop.setExample("2");
                    }
                    schema.getProperties().put(field.getName(), prop);
                }
                else {
                    Property prop = new Property();
                    prop.setType(field.getType().getSimpleName().toLowerCase());
                    schema.getProperties().put(field.getName(), prop);
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        schema.setKey(name + "-created");
        return schema;
    }
}
