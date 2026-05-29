package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"type", "format", "description", "_enum", "example", "_default", "items"})
public class Property {
    
    private String type;
    private String format;
    private String description;
    private Object example;
    @JsonProperty("enum")
    private List<String> _enum;
    @JsonProperty("default")
    private String _default;
    private Schema items;
    
    public Property() {}

    public Property(String type, String description, Object example) {
        this.type = type;
        this.description = description;
        this.example = example;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = example;
    }

    @JsonProperty("enum")
    @JsonGetter("enum")
    public List<String> get_enum() {
        return _enum;
    }

    @JsonProperty("enum")
    @JsonSetter("enum")
    public void set_enum(List<String> _enum) {
        this._enum = _enum;
    }

    @JsonGetter("default")
    public String get_default() {
        return _default;
    }

    public void set_default(String _default) {
        this._default = _default;
    }

    public Schema getItems() {
        return items;
    }

    public void setItems(Schema items) {
        this.items = items;
    }
}
