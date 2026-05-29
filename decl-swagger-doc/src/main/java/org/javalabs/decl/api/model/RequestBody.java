package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"description", "content", "required"})
public class RequestBody {
    
    private String description;
    private Boolean required;
    private Map<String, Media> content;
    
    public RequestBody() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Map<String, Media> getContent() {
        return content;
    }

    public void setContent(Map<String, Media> content) {
        this.content = content;
    }
}
