package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Media;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract doc snippet generator.
 *
 * @author Sudiptasish Chanda
 */
public abstract class AbstractDoc {
    
    /**
     * Generate the swagger doc snippet for a given operation.
     * 
     * @param pojo  Fully qualified name of the pojo (java class)
     * @param contentType   Content type supported by the api.
     * 
     * @return Operation    Post, Get, etc
     */
    public abstract Operation generate(String pojo, String contentType);
    
    /**
     * Generate elaborate swagger doc for response schema in components section.
     * @param pojo  Fully qualified name of the pojo (java class). It can be empty or null.
     * @param uri   The uri
     * 
     * @return Schema
     */
    public abstract Schema response(String pojo, String uri);
    
    public Schema schema(String pojo, Boolean selective) {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(pojo);
            Map<String, Property> map = new LinkedHashMap<>();
            
            introspect(map, clazz, selective);
            
            Schema schema = new Schema();
            schema.setType("object");
            schema.setProperties(map);
            return schema;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void introspect(Map<String, Property> map, Class<?> clazz, Boolean selective) {
        while (clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic()
                    || field.getName().equals("serialVersionUID")
                    || Modifier.isTransient(field.getModifiers())
                    || Modifier.isStatic(field.getModifiers())
                    || Modifier.isInterface(field.getModifiers())
                    || Modifier.isNative(field.getModifiers())) {

                    continue;
                }
                // If the field contains any id (which is most likely to be auto-generated),
                // skip that.
                if (selective) {
                    if (field.getName().equals("canonicalLink")) {
                        continue;
                    }
                    if (isPrimitive(field.getType()) &&
                            (field.getName().toLowerCase().endsWith("id")
                            || (field.getName().toLowerCase().endsWith("identifier"))
                            || (field.getName().contains("update") && Date.class.isAssignableFrom(field.getType()))
                            || (field.getName().contains("upd") && Date.class.isAssignableFrom(field.getType()))
                            || (field.getName().contains("board") && Date.class.isAssignableFrom(field.getType()))
                            || (field.getName().contains("create") && Date.class.isAssignableFrom(field.getType())))) {

                        continue;
                    }
                }

                // For nested object.
                if (isPrimitive(field.getType())) {
                    Property prop = new Property();
                    if (field.getType().isEnum()) {
                        Object[] vals = field.getType().getEnumConstants();
                        String[] sVals = new String[vals.length];
                        for (int i = 0; i < vals.length; i ++) {
                            sVals[i] = vals[i].toString();
                        }
                        prop.set_enum(Arrays.asList(sVals));
                        prop.setType("string");
                    }
                    else if (Date.class.isAssignableFrom(field.getType())) {
                        prop.setType("string");
                        prop.setFormat("date");
                    }
                    else if (field.getType() == Byte.class || field.getType() == byte.class
                            || field.getType() == Short.class || field.getType() == short.class
                            || field.getType() == Integer.class || field.getType() == int.class) {
                        
                        prop.setType("integer");
                        prop.setFormat("int32");
                    }
                    else if (field.getType() == Long.class || field.getType() == long.class
                            || field.getType() == BigInteger.class) {
                        prop.setType("integer");
                        prop.setFormat("int64");
                    }
                    else if (field.getType() == Float.class || field.getType() == float.class) {
                        prop.setType("number");
                        prop.setFormat("float");
                    }
                    else if (field.getType() == Double.class || field.getType() == double.class
                            || field.getType() == BigDecimal.class) {
                        
                        // For other double, etc
                        prop.setType("number");
                        prop.setFormat("double");
                    }
                    else {
                        prop.setType(field.getType().getSimpleName().toLowerCase());
                    }
                    map.put(field.getName(), prop);
                }
                else if (Collection.class.isAssignableFrom(field.getType())) {
                    Property prop = new Property();
                    prop.setType("array");
                    map.put(field.getName(), prop);

                    Type type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                        Class<?> child = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
                        Map<String, Property> childMap = new LinkedHashMap<>();
                        
                        introspect(childMap, child, selective);
                        
                        Schema schema = new Schema();
                        schema.setProperties(childMap);
                        prop.setItems(schema);
                    }
                }
                else if (field.getType().isArray()) {
                    Property prop = new Property();
                    prop.setType("array");
                    map.put(field.getName(), prop);

                    Class<?> compType = field.getType().getComponentType();
                    if (isPrimitive(compType)) {
                        Schema schema = new Schema();
                        schema.setType(compType.getSimpleName().toLowerCase());
                        prop.setItems(schema);
                    }
                    else {
                        Map<String, Property> childMap = new LinkedHashMap<>();
                        
                        introspect(childMap, compType, selective);
                        
                        Schema schema = new Schema();
                        schema.setProperties(childMap);
                        prop.setItems(schema);
                    }
                }
                else {
                    // Another POJO
                    Property prop = new Property();
                    prop.setType("object");
                    map.put(field.getName(), prop);
                    
                    Map<String, Property> childMap = new HashMap<>();
                    introspect(childMap, field.getType(), selective);
                    
                    Schema schema = new Schema();
                    schema.setProperties(childMap);
                    prop.setItems(schema);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
    
    protected Boolean isPrimitive(Class<?> clazz) {
        return clazz == byte.class
                || clazz == short.class
                || clazz == int.class
                || clazz == long.class
                || clazz == float.class
                || clazz == double.class
                || clazz == Boolean.class || clazz == boolean.class
                || clazz == String.class
                || Date.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz)
                || clazz.isEnum();
    }
    
    protected Response conflict(String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/conflict"));
        
        Response response = new Response();
        response.setDescription("Conflict");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    protected Response badRequest(String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/bad_request"));
        
        Response response = new Response();
        response.setDescription("Bad Request");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    protected Response unAuthorized(String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/un_authorized"));
        
        Response response = new Response();
        response.setDescription("UnAuthorized");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    protected Response notFound(String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/not_found"));
        
        Response response = new Response();
        response.setDescription("Not Found");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    protected Response invalidMedia(String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/invalid_media"));
        
        Response response = new Response();
        response.setDescription("Invalid Media Type");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    protected Response serverError(String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/server_error"));
        
        Response response = new Response();
        response.setDescription("Internal Server Error");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
}
