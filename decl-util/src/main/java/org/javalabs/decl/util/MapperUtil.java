package org.javalabs.decl.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;

/**
 * Utility class to keep the {@link ObjectMapper}.
 *
 * @author Sudiptasish Chanda
 */
public class MapperUtil {
    
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    private static final ObjectWriter JSON_PRETTY_MAPPER = new ObjectMapper().writerWithDefaultPrettyPrinter();
    
    private static final ObjectMapper YML_MAPPER = new ObjectMapper(new YAMLFactory());
    
    /**
     * {@link #decode(byte[], java.lang.Object) } 
     * 
     * @param <T>       The expected type of the deserialized object
     * @param buff      The raw byte array to decode, must not be null
     * @param clazz     the class or array type to convert the data into.
     * @return T    New object 
     */
    public static <T> T decode(byte[] buff, Class<T> clazz) {
        try {
            return (T)mapper().readValue(buff, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Deserializes a byte array back into a Java object.
     * 
     * <p>This method takes a raw array of bytes and rebuilds the original 
     * object structure. It reverses the serialization process. The input 
     * byte array must have been created by a matching encoder or serialization 
     * process.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * byte[] data = ... // bytes fetched from network or disk
     * MyObject obj = decode(data, MyObject.class);
     * }</pre>
     *
     * @param <T>       The expected type of the deserialized object
     * @param buff      The raw byte array to decode, must not be null
     * @param type      the class or array type to convert the data into
     * @return the fully reconstructed object instance
     * 
     * @throws java.lang.RuntimeException if the data cannot be read or is corrupted
     */
    public static <T> T decode(byte[] buff, T type) {
        try {
            return (T)mapper().readValue(buff, new TypeReference<T>() {});
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serializes a Java object into a byte array.
     * 
     * <p>This method takes a structured object and converts its data and fields 
     * into a flat stream of bytes. This byte array can then be saved to a 
     * disk file, stored in a database, or transmitted across a network. It 
     * is the direct counterpart to the {@code decode} method.</p>
     * 
     * <p>Example usage:</p>
     * <pre>
     * {@code
     * MyObject obj = new MyObject("data");
     * byte[] rawData = encode(obj);
     * }
     * </pre>
     *
     * @param obj the object instance to convert, must not be null
     * @return a raw byte array representing the serialized object state
     * 
     * @throws java.lang.RuntimeException if the object cannot be serialized
     */
    public static byte[] encode(Object obj) {
        try {
            return mapper().writeValueAsBytes(obj);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return the standard object mapper.
     * @return ObjectMapper
     */
    public static ObjectMapper mapper() {
        return JSON_MAPPER;
    }

    /**
     * Return the platform default yml mapper.
     * @return ObjectMapper
     */
    public static ObjectMapper ymlMapper() {
        return YML_MAPPER;
    }

    /**
     * Converts a Java object into a pretty-printed, human-readable JSON string.
     * 
     * <p>This method serializes the given object into JSON format. It configures 
     * the underlying Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} 
     * to add visual formatting, such as line breaks and indentation. This makes 
     * the output much easier for humans to read compared to a compact JSON string.</p>
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * User user = new User("Alice", 30);
     * String prettyJson = jsonUtil.prettyWrite(user);
     * // Output will include proper indents and line breaks
     * }</pre>
     *
     * @param obj       the object instance to format into JSON, may be null
     * @return byte[]   a formatted, indentation-rich JSON string, or an empty JSON string/null depending on configuration
     */
    public static byte[] prettyWrite(Object obj) {
        try {
            return JSON_PRETTY_MAPPER.writeValueAsBytes(obj);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
