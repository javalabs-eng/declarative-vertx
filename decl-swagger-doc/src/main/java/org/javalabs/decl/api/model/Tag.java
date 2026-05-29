package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({ "name", "description", "externalDocs" })
public class Tag {
    
    private String name;
    private String description;
    private ExternalDoc externalDocs;
    
    public Tag() {}

    public Tag(String name, String description, ExternalDoc externalDocs) {
        this.name = name;
        this.description = description;
        this.externalDocs = externalDocs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExternalDoc getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalDoc externalDocs) {
        this.externalDocs = externalDocs;
    }
}
