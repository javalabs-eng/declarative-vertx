package org.javalabs.decl.workflow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Serializer class for {@link Chain}.
 *
 * @author Sudiptasish Chanda
 */
public class ChainSerializer extends StdSerializer<Chain> {
    
    public ChainSerializer() {
        this(null);
    }
  
    public ChainSerializer(Class<Chain> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(Chain chain
            , JsonGenerator generator
            , SerializerProvider provider) throws IOException {
        
        generator.writeStartObject();
        generator.writeStringField("name", chain.name());
        
        Command[] list = chain.commands();
        
        generator.writeFieldName("commands");
        generator.writeStartArray();
        for (Command cmd : list) {
            generator.writeStartObject();
            generator.writeStringField("name", cmd.name());
            generator.writeStringField("class", cmd.getClass().getSimpleName());
            generator.writeEndObject();
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }
}
