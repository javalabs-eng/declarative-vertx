package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"description", "headers", "content"})
public class Response {
    
    private String description;
    private Map<String, Parameter> headers;
    private Map<String, Media> content;
    
    public Response() {}

    public Response(String description, Map<String, Parameter> headers, Map<String, Media> content) {
        this.description = description;
        this.headers = headers;
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Parameter> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Parameter> headers) {
        this.headers = headers;
    }

    public Map<String, Media> getContent() {
        return content;
    }

    public void setContent(Map<String, Media> content) {
        this.content = content;
    }
}
