package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({ "url", "description", "variables" })
public class Server {
    
    private String url;
    private String description;
    private Map<String, Variable> variables;
    
    public Server() {}

    public Server(String url, String description, Map<String, Variable> variables) {
        this.url = url;
        this.description = description;
        this.variables = variables;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Variable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Variable> variables) {
        this.variables = variables;
    }
}
