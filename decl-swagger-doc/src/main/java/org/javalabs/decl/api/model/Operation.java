package org.javalabs.decl.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
@JsonPropertyOrder({"tags", "summary", "description", "operationId", "externalDocs", "parameters", "requestBody", "responses"})
public class Operation {
    
    private List<String> tags;
    private String summary;
    private String description;
    private String operationId;
    private ExternalDoc externalDocs;
    private List<Parameter> parameters;
    private RequestBody requestBody;
    private Map<Object, Response> responses;
    
    public Operation() {}

    public Operation(String summary, String description, String operationId, List<String> tags, ExternalDoc externalDocs, List<Parameter> parameters) {
        this.summary = summary;
        this.description = description;
        this.operationId = operationId;
        this.tags = tags;
        this.externalDocs = externalDocs;
        this.parameters = parameters;
    }

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

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ExternalDoc getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalDoc externalDocs) {
        this.externalDocs = externalDocs;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public Map<Object, Response> getResponses() {
        return responses;
    }

    public void setResponses(Map<Object, Response> responses) {
        this.responses = responses;
    }
    
}
