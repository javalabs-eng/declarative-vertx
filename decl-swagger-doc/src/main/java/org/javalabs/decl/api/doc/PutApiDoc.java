package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Media;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Parameter;
import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.RequestBody;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger doc generator for a put method call.
 * 
 * <p>
 * POST operation format.
 * 
 * <pre>
 * {@code
 * put:
 *   summary: Update an existing employee
 *   description: Api to update an existing employee
 *   requestBody:
 *     content:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             name:
 *               type: string
 *               description: Name of the employee
 *             location:
 *               type: string
 *               description: Location of the employee
 *             devices:
 *               type: array
 *               items: 
 *                 type: object
 *                 properties:
 *                   id:
 *                     type: string
 *                     description: Device id
 *                   type:
 *                     type: string
 *                     description: Device type
 *                   key:
 *                     type: string
 *                     description: Unique serial number of the device
 *             dept:
 *               type: object
 *               properties:
 *                 name:
 *                   type: string
 *                   description: Name of the department
 *   responses:
 *     200:
 *       description: Ok
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Employee-modified'
 *     400:
 *       description: Bad Request
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/bad_request'
 *     404:
 *       description: Not Found
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/not_found'
 *     415:
 *       description: Invalid media type
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/invalid_media'
 *     500:
 *       description: Internal Server Error
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/server_error'
 * 
 * components:
 *   schemas:
 *     Employee-modified:
 *       type: object
 *         
 * }
 * </pre>
 * 
 * @author Sudiptasish Chanda
 */
public class PutApiDoc extends AbstractDoc {
    
    @Override
    public Operation generate(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Operation operation = new Operation();
        operation.setTags(Arrays.asList(name));
        
        // Set basic attributes
        operation.setSummary("Update an existing " + name);
        operation.setDescription("Api to update an existing " + name);
        
        // Add header
        List<Parameter> headers = header(contentType);
        operation.setParameters(headers);
        
        // Add path params
        List<Parameter> params = pathParam(pojo);
        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }
        operation.getParameters().addAll(params);
        
        // Prepare requestBody
        RequestBody body = body(pojo, contentType);
        operation.setRequestBody(body);
        
        // Prepare responses
        Map<Object, Response> responses = new LinkedHashMap<>();
        
        // Success
        Response response = success(name, contentType);
        responses.put(200, response);
        
        // Failures
        response = badRequest(contentType);
        responses.put(400, response);
        
        response = unAuthorized(contentType);
        responses.put(401, response);
        
        response = notFound(contentType);
        responses.put(404, response);
        
        response = invalidMedia(contentType);
        responses.put(415, response);
        
        response = serverError(contentType);
        responses.put(500, response);
        
        operation.setResponses(responses);
        
        return operation;
    }
    
    private List<Parameter> header(String cType) {
        List<Parameter> params = new ArrayList<>(2);
        
        Parameter contentType = new Parameter();
        contentType.setIn("header");
        contentType.setName("Content-Type");
        contentType.setDescription("Media type of the request payload");
        contentType.setSchema(new Schema("string", null));
        contentType.setRequired(Boolean.TRUE);
        contentType.setExample(cType);
        params.add(contentType);
        
        return params;
    }
    
    private List<Parameter> pathParam(String pojo) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        List<Parameter> params = new ArrayList<>(2);
        
        Parameter param = new Parameter();
        param.setName("id");
        param.setIn("path");
        param.setDescription("Id of the " + name);
        param.setRequired(Boolean.TRUE);
        param.setSchema(new Schema("integer", "int32"));
        params.add(param);
        
        return params;
    }
    
    private RequestBody body(String pojo, String contentType) {
        // Selective attributes are chosen
        Schema schema = schema(pojo, Boolean.TRUE);
        
        Media media = new Media();
        media.setSchema(schema);
        
        RequestBody body = new RequestBody();
        body.setContent(Map.of(contentType, media));
        
        return body;
    }
    
    private Response success(String name, String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/" + name + "-modified"));
        
        Response response = new Response();
        response.setDescription("Ok");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }

    @Override
    public Schema response(String pojo, String uri) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        Map<String, Property> map = new HashMap<>();
        
        Property prop = new Property();
        prop.setType("string");
        prop.setExample("app_code");
        map.put("code", prop);
        
        prop = new Property();
        prop.setType("string");
        prop.setExample(name + " modified successfully");
        map.put("message", prop);
        
        Schema schema = new Schema();
        schema.setType("object");
        schema.setProperties(map);
        schema.setKey(name + "-modified");
        
        return schema;
    }
}
