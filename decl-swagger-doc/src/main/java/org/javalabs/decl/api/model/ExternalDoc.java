package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({ "url", "description" })
public class ExternalDoc {
    
    private String url;
    private String description;
    
    public ExternalDoc() {}

    public ExternalDoc(String url, String description) {
        this.url = url;
        this.description = description;
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
}
