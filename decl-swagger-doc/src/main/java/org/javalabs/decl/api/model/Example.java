package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"summary", "description", "value", "externalValue"})
public class Example {
    
    private String summary;
    private String description;
    private Map<String, Object> value;
    private String externalValue;
    
    public Example() {}

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }

    public String getExternalValue() {
        return externalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }
}
