package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"schemas", "responses", "parameters", "examples", "requestBodies", "headers", "securitySchemes"})
public class Components {
    
    private Map<String, Schema> schemas;
    private Map<String, Response> responses;
    private Map<String, Parameter> parameters;
    private Map<String, Example> examples;
    private Map<String, RequestBody> requestBodies;
    private Map<String, Parameter> headers;
    private Map<String, SecurityScheme> securitySchemes;
    
    public Components() {}

    public Components(Map<String, Schema> schemas, Map<String, Response> responses, Map<String, Parameter> parameters, Map<String, Example> examples, Map<String, RequestBody> requestBodies, Map<String, Parameter> headers, Map<String, SecurityScheme> securitySchemes) {
        this.schemas = schemas;
        this.responses = responses;
        this.parameters = parameters;
        this.examples = examples;
        this.requestBodies = requestBodies;
        this.headers = headers;
        this.securitySchemes = securitySchemes;
    }

    public Map<String, Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

    public Map<String, Response> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, Response> responses) {
        this.responses = responses;
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Example> getExamples() {
        return examples;
    }

    public void setExamples(Map<String, Example> examples) {
        this.examples = examples;
    }

    public Map<String, RequestBody> getRequestBodies() {
        return requestBodies;
    }

    public void setRequestBodies(Map<String, RequestBody> requestBodies) {
        this.requestBodies = requestBodies;
    }

    public Map<String, Parameter> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Parameter> headers) {
        this.headers = headers;
    }

    public Map<String, SecurityScheme> getSecuritySchemes() {
        return securitySchemes;
    }

    public void setSecuritySchemes(Map<String, SecurityScheme> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }
}
