package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Media;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Parameter;
import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import org.javalabs.decl.api.spec.GenericItemSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger doc generator for a get all method call.
 * 
 * <p>
 * POST operation format.
 * 
 * <pre>
 * {@code
 * get:
 *   summary: View all employees
 *   description: Api to view the employees
 *   parameters:
 *     - name: offset
 *       in: query
 *       description: Start index of the record
 *       required: false
 *       schema:
 *         type: integer
 *         format: int32
 *     - name: limit
 *       in: query
 *       description: How many items to return at one time (max 100)
 *       required: false
 *       schema:
 *         type: integer
 *         format: int32
 * 
 *   responses:
 *     200:
 *       description: Ok
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Employee-paged'
 *     400:
 *       description: Bad Request
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/bad_request'
 *     500:
 *       description: Internal Server Error
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/server_error'
 * 
 * components:
 *   schemas:
 *     Employee-paged:
 *       type: object
 *         properties:
 *           total:
 *             type: integer
 *             format: int64
 *             example: 1
 *           items:
 *             type: array
 *             description: Elements to be displayed per page
 *             items:
 *               type: object
 *               properties:
 *                 id:
 *                   type: integer
 *                   format: int64
 *                   example: 3
 *                 name:
 *                   type: string
 *                   example: Socretes
 *                 createdOn:
 *                   type: date
 *                   example: 1631514251920
 *                 canonicalLink:
 *                   type: string
 *                   description: 
 *                   example: /api/v1/employees/3
 *           nextLink: 
 *             type: string
 *             example: /api/v1/employees?offset=10&limit=10
 *           previousLink: 
 *             type: string
 *             example: /api/v1/employees?offset=0&limit=10
 *           hasMore: true
 * }
 * </pre>
 * 
 * @author Sudiptasish Chanda
 */
public class GetAllApiDoc extends AbstractDoc {
    
    @Override
    public Operation generate(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Operation operation = new Operation();
        operation.setTags(Arrays.asList(name));
        
        // Set basic attributes
        operation.setSummary("View all " + name + "s");
        operation.setDescription("Api to view all " + name + "s");
        
        // Add query params
        List<Parameter> params = pagination();
        operation.setParameters(params);
        operation.getParameters().addAll(query(pojo));
        
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
        
        response = serverError(contentType);
        responses.put(500, response);
        
        operation.setResponses(responses);
        
        return operation;
    }
    
    private List<Parameter> pagination() {
        List<Parameter> params = new ArrayList<>(2);
        
        Parameter offset = new Parameter();
        offset.setName("offset");
        offset.setIn("query");
        offset.setDescription("Start index of the record");
        offset.setRequired(Boolean.FALSE);
        offset.setSchema(new Schema("integer", "int32"));
        params.add(offset);
        
        Parameter limit = new Parameter();
        limit.setName("limit");
        limit.setIn("query");
        limit.setDescription("How many items to return at one time (max 100)");
        limit.setRequired(Boolean.FALSE);
        limit.setSchema(new Schema("integer", "int32"));
        params.add(limit);
        
        return params;
    }
    
    private List<Parameter> query(String pojo) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        List<Parameter> params = new ArrayList<>(2);
        
        Schema schema = schema(pojo, Boolean.TRUE);
        schema.getProperties().forEach((k, v) -> {
            Parameter param = new Parameter();
            param.setName(k);
            param.setIn("query");
            param.setDescription(k + " of the " + name);
            param.setRequired(Boolean.FALSE);
            param.setSchema(new Schema(v.getType(), null));
            
            params.add(param);
        });
        return params;
    }
    
    private Response success(String name, String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/" + name + "-paged"));
        
        Response response = new Response();
        response.setDescription("Ok");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    @Override
    public Schema response(String pojo, String uri) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Schema schema = new GenericItemSpec().schema(pojo, uri);
        
        // Now create the parent schema.
        Map<String, Property> props = new LinkedHashMap<>();
        props.put("total", new Property("integer", "Total number of " + name + "s", "45"));
        
        Property items = new Property("array", null, null);
        items.setItems(schema);
        props.put("items", items);
        
        props.put("nextLink", new Property("string", null, uri + "?offset=10&limit=10"));
        props.put("previousLink", new Property("string", null, uri + "?offset=0&limit=10"));
        props.put("hasMore", new Property("boolean", null, Boolean.TRUE));
        
        Schema parent = new Schema();
        parent.setProperties(props);
        parent.setKey(name + "-paged");
        
        return parent;
    }
}
