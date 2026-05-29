package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Parameter;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger doc generator for a delete method call.
 * 
 * <p>
 * POST operation format.
 * 
 * <pre>
 * {@code
 * get:
 *   summary: Delete an employee by its id
 *   description: Api to delete an employee by its id
 *   parameters:
 *     - name: id
 *       in: path
 *       description: Employee id to be deleted
 *       required: true
 * 
 *   responses:
 *     204:
 *       description: No Content
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/no_content'
 *     404:
 *       description: Not Found
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/not_found'
 *     500:
 *       description: Internal Server Error
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/server_error'
 * 
 * components:
 *   schemas:
 *     no_content:
 *       description: Resource deleted successfully
 * }
 * </pre>
 * 
 * @author Sudiptasish Chanda
 */
public class DeleteApiDoc extends AbstractDoc {
    
    @Override
    public Operation generate(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Operation operation = new Operation();
        operation.setTags(Arrays.asList(name));
        
        // Set basic attributes
        operation.setSummary("Delete " + name + " by its id");
        operation.setDescription("Api to delete " + name + " by its id");
        
        // Add path params
        List<Parameter> params = pathParam();
        operation.setParameters(params);
        
        // Prepare responses
        Map<Object, Response> responses = new LinkedHashMap<>();
        
        // Success
        Response response = success(name, contentType);
        responses.put(204, response);
        
        // Failures
        response = unAuthorized(contentType);
        responses.put(401, response);
        
        response = notFound(contentType);
        responses.put(404, response);
        
        response = serverError(contentType);
        responses.put(500, response);
        
        operation.setResponses(responses);
        
        return operation;
    }
    
    private List<Parameter> pathParam() {
        List<Parameter> params = new ArrayList<>(2);
        
        Parameter param = new Parameter();
        param.setName("id");
        param.setIn("path");
        param.setDescription("Id of element");
        param.setRequired(Boolean.TRUE);
        param.setSchema(new Schema("integer", "int32"));
        params.add(param);
        
        return params;
    }
    
    private Response success(String name, String contentType) {
        Response response = new Response();
        response.setDescription("No Content");
        
        return response;
    }
    
    @Override
    public Schema response(String pojo, String uri) {
        return null;        // No schema for 204
    }
}
