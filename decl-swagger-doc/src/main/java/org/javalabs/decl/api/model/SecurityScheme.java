package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"type", "description", "name", "in", "scheme", "bearerFormat", "flows", "openIdConnectUrl"})
public class SecurityScheme {
    
    private String type;
    private String description;
    private String name;
    private String in;
    private String scheme;
    private String bearerFormat;
    private Object flows;
    private String openIdConnectUrl;
    
    public SecurityScheme() {}

    public SecurityScheme(String type, String description, String name, String in, String scheme, String bearerFormat, Object flows, String openIdConnectUrl) {
        this.type = type;
        this.description = description;
        this.name = name;
        this.in = in;
        this.scheme = scheme;
        this.bearerFormat = bearerFormat;
        this.flows = flows;
        this.openIdConnectUrl = openIdConnectUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getBearerFormat() {
        return bearerFormat;
    }

    public void setBearerFormat(String bearerFormat) {
        this.bearerFormat = bearerFormat;
    }

    public Object getFlows() {
        return flows;
    }

    public void setFlows(Object flows) {
        this.flows = flows;
    }

    public String getOpenIdConnectUrl() {
        return openIdConnectUrl;
    }

    public void setOpenIdConnectUrl(String openIdConnectUrl) {
        this.openIdConnectUrl = openIdConnectUrl;
    }
    
}
