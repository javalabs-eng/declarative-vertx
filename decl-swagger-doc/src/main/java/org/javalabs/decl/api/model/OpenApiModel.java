package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({ "openapi", "info", "externalDocs", "servers", "tags", "security", "paths", "components" })
public class OpenApiModel {
    
    private String openapi;
    private Info info;
    private ExternalDoc externalDocs;
    private List<Server> servers;
    private List<Tag> tags;
    private List<Map<String, List>> security;
    private Map<String, Path> paths;
    private Components components;

    public String getOpenapi() {
        return openapi;
    }

    public void setOpenapi(String openapi) {
        this.openapi = openapi;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public ExternalDoc getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalDoc externalDocs) {
        this.externalDocs = externalDocs;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Map<String, List>> getSecurity() {
        return security;
    }

    public void setSecurity(List<Map<String, List>> security) {
        this.security = security;
    }

    public Map<String, Path> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Path> paths) {
        this.paths = paths;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }
}
