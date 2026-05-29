package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"type", "items", "format", "properties", "externalDocs", "required"})
public class Schema {
    
    // This key is not printed.
    @JsonIgnore
    private String key;
    
    private String type;
    private String format;
    private Map<String, Property> properties;
    private ExternalDoc externalDocs;
    private List<String> required;
    
    // For nested object or array.
    private Schema items;
    
    public Schema() {}

    public Schema(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public ExternalDoc getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalDoc externalDocs) {
        this.externalDocs = externalDocs;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public Schema getItems() {
        return items;
    }

    public void setItems(Schema items) {
        this.items = items;
    }
}
