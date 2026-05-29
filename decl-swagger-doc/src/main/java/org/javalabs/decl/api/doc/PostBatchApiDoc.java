package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Media;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Parameter;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.RequestBody;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.javalabs.decl.api.model.Property;

/**
 * Swagger doc generator for a post method call.
 * 
 * <p>
 * POST operation format.
 * 
 * <pre>
 * {@code
 * post:
 *   summary: Create a new employee
 *   description: Api to create a new employee
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
 *     201:
 *       description: Created
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Employee-Created'
 *     400:
 *       description: Bad Request
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/bad_request'
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
 *     Employee-created:
 *       type: object
 *         properties:
 *           id:
 *             type: integer
 *             format: int64
 *             example: 3
 *           name:
 *             type: String
 *             example: Socretes
 *           createdOn:
 *             type: date
 *             example: 1631514251920
 *           canonicalLink:
 *             type: string
 *             example: /api/v1/employees/3
 * }
 * </pre>
 * 
 * @author Sudiptasish Chanda
 */
public class PostBatchApiDoc extends AbstractDoc {
    
    @Override
    public Operation generate(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Operation operation = new Operation();
        operation.setTags(Arrays.asList(name));
        
        // Set basic attributes
        operation.setSummary("Batch create " + name + "s");
        operation.setDescription("Batch Api to create a new set of " + name + "s");
        
        // Prepare request headers
        Parameter header = header(contentType);
        operation.setParameters(Arrays.asList(header));
        
        // Prepare requestBody
        if (pojo != null) {
            RequestBody body = body(pojo, contentType);
            operation.setRequestBody(body);
        }
        // Prepare responses
        Map<Object, Response> responses = new LinkedHashMap<>();
        
        // Success
        Response response = success(name, contentType);
        responses.put(201, response);
        
        // Failures
        response = conflict(contentType);
        responses.put(409, response);
        
        response = badRequest(contentType);
        responses.put(400, response);
        
        response = unAuthorized(contentType);
        responses.put(401, response);
        
        response = invalidMedia(contentType);
        responses.put(415, response);
        
        response = serverError(contentType);
        responses.put(500, response);
        
        operation.setResponses(responses);
        
        return operation;
    }
    
    private Parameter header(String cType) {
        Parameter contentType = new Parameter();
        contentType.setIn("header");
        contentType.setName("Content-Type");
        contentType.setDescription("Media type of the request payload");
        contentType.setSchema(new Schema("string", null));
        contentType.setRequired(Boolean.TRUE);
        contentType.setExample(cType);
        
        return contentType;
    }
    
    private RequestBody body(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        // Selective attributes are chosen
        Schema schema = schema(pojo, Boolean.TRUE);
        schema.setRequired(schema.getProperties().keySet().stream().collect(Collectors.toList()));
        schema.getProperties().forEach((k, v) -> {
            v.setDescription(Character.toUpperCase(k.charAt(0)) + k.substring(1) + " of the " + name);
        });
        
        Schema parent = new Schema();
        parent.setType("array");
        parent.setItems(schema);
        
        Media media = new Media();
        media.setSchema(parent);
        
        RequestBody body = new RequestBody();
        body.setContent(Map.of(contentType, media));
        
        return body;
    }
    
    private Response success(String name, String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/" + name + "-batch-created"));
        
        Response response = new Response();
        response.setDescription("Created");
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
        prop.setExample("Inserted n " + name + "(s)");
        map.put("message", prop);
        
        Schema schema = new Schema();
        schema.setType("object");
        schema.setProperties(map);
        schema.setKey(name + "-batch-created");
        
        return schema;
    }
}
