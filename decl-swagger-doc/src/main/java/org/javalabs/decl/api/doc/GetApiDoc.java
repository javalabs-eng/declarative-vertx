package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.model.Media;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Parameter;
import org.javalabs.decl.api.model.Property;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.Response;
import org.javalabs.decl.api.model.Schema;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger doc generator for a get method call.
 * 
 * <p>
 * POST operation format.
 * 
 * <pre>
 * {@code
 * get:
 *   summary: View an employee by its id
 *   description: Api to view an employee by its id
 *   parameters:
 *     - name: id
 *       in: path
 *       description: Employee id
 *       required: true
 * 
 *   responses:
 *     200:
 *       description: Ok
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Employee'
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
 *     Employee:
 *       type: object
 *         properties:
 *           id:
 *             type: integer
 *             format: int64
 *             example: 3
 *           name:
 *             type: string
 *             example: Socretes
 *           location:
 *             type: string
 *             example: USA
 *           createdOn:
 *             type: date
 *             example: 1631514251920
 *           updatedOn:
 *             type: date
 *             example: 1631514251920
 *           devices:
 *             type: array
 *             description: Set of devices allocated
 *             items:
 *               type: object
 *               properties:
 *                 id:
 *                   type: string
 *                   example: ln_368239191
 *                 type:
 *                   enum:
 *                     - Laptop
 *                     - Mobile
 *                     - HeadSet
 *                   example: Laptop
 *                 key:
 *                   type: string
 *                   example: 247973892372972372932
 *           dept:
 *             type: object
 *             description: Department that this employee belongs to
 *             properties:
 *               deptId:
 *                 type: integer
 *                 format: int64
 *                 example: 28
 *               name:
 *                 type: string
 *                 example: Research And Development
 *               active:
 *                 type: boolean
 *                 example: true
 * }
 * </pre>
 * 
 * @author Sudiptasish Chanda
 */
public class GetApiDoc extends AbstractDoc {
    
    @Override
    public Operation generate(String pojo, String contentType) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Operation operation = new Operation();
        operation.setTags(Arrays.asList(name));
        
        // Set basic attributes
        operation.setSummary("View " + name + " by its id");
        operation.setDescription("Api to view " + name + " by its id");
        
        // Add path params
        List<Parameter> params = pathParam(pojo);
        operation.setParameters(params);
        
        // Prepare responses
        Map<Object, Response> responses = new LinkedHashMap<>();
        
        // Success
        Response response = success(name, contentType);
        responses.put(200, response);
        
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
    
    private Response success(String name, String contentType) {
        Media media = new Media();
        media.setSchema(new Reference("#/components/schemas/" + name));
        
        Response response = new Response();
        response.setDescription("Ok");
        response.setContent(Map.of(contentType, media));
        
        return response;
    }
    
    @Override
    public Schema response(String pojo, String uri) {
        int idx = pojo.lastIndexOf(".");
        String name = idx != -1 ? pojo.substring(idx + 1) : pojo;
        
        Schema schema = schema(pojo, Boolean.FALSE);
        schema.getProperties().forEach((k, v) -> {
            if ((k.endsWith("id") || k.endsWith("Id") || k.endsWith("ID"))
                    && (v.getType().equals("string") || v.getType().equals("integer"))) {
                
                if (v.getType().equals("string")) {
                    v.setExample("100");
                }
                else if (v.getType().equals("integer")) {
                    v.setExample(100);
                }
                // Set the canonical link for id
                Property prop = schema.getProperties().get("canonicalLink");
                if (prop != null) {
                    prop.setExample(uri + "/" + v.getExample());
                }
            }
            else if (v.getType().equals("number") && v.getFormat() != null
                    && (v.getFormat().equals("float") || v.getFormat().equals("double"))) {
                v.setExample(720000.45);
            }
            else if (v.getType().equals("integer")) {
                if (v.getFormat() != null) {
                    if (v.getFormat().equals("int32")) {
                        v.setExample(15000);
                    }
                    else if (v.getFormat().equals("int64")) {
                        v.setExample(8600000);
                    }
                    else {
                        v.setExample(2700);
                    }
                }
            }
            else if (v.getType().equals("string") && v.getFormat() != null && v.getFormat().equals("date")) {
                v.setExample(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
        });
        // Now, exclude the canonicalLink attribute.
        // For GET by Id call, we are not supposed to display the canonicalLink, because user follows the
        // canonicalLink only to identify the resource.
        if (schema.getProperties().containsKey("canonicalLink")) {
            schema.getProperties().remove("canonicalLink");
        }
        schema.setKey(name);
        
        return schema;
    }
}
