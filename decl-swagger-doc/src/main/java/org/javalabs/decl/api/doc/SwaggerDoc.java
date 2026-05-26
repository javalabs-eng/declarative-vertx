package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.spec.ManyRequestSpec;
import org.javalabs.decl.api.spec.ConflictSpec;
import org.javalabs.decl.api.spec.ServerErrorSpec;
import org.javalabs.decl.api.spec.UnAuthorisedSpec;
import org.javalabs.decl.api.spec.BadRequestSpec;
import org.javalabs.decl.api.spec.NotFoundSpec;
import org.javalabs.decl.api.model.Components;
import org.javalabs.decl.api.model.ExternalDoc;
import org.javalabs.decl.api.model.Info;
import org.javalabs.decl.api.model.OpenApiModel;
import org.javalabs.decl.api.model.Operation;
import org.javalabs.decl.api.model.Path;
import org.javalabs.decl.api.model.Reference;
import org.javalabs.decl.api.model.Schema;
import org.javalabs.decl.api.model.SecurityScheme;
import org.javalabs.decl.api.model.Server;
import org.javalabs.decl.api.model.Tag;
import org.javalabs.decl.api.model.YamlRepresenter;
import org.javalabs.decl.api.spec.InvalidMediaSpec;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.parser.RoutingConfigParser;
import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import org.javalabs.decl.writer.ClassWriter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Swagger doc generator.
 * 
 * <p>
 * In an OpenAPI YAML file, two exclamation marks ("!!") signify a "tag" which is used to explicitly
 * specify the data type of a value, essentially forcing a conversion to a particular type like string,
 * integer, or boolean; essentially acting as a type hint within the YAML structure.
 * 
 * <p>
 * This class is completely thread-safe. Therefore, you must reuse the instance fo this class.
 *
 * @author schan280
 */
public class SwaggerDoc {
    
    private final Map<String, AbstractDoc> docMapping = new HashMap<>();
    
    private final LoginApiDoc loginDoc = new LoginApiDoc();
    
    public SwaggerDoc() {
        docMapping.put("POST", new PostApiDoc());
        docMapping.put("POST$batch", new PostBatchApiDoc());
        docMapping.put("GETALL", new GetAllApiDoc());
        docMapping.put("GET", new GetApiDoc());
        docMapping.put("PUT", new PutApiDoc());
        docMapping.put("PATCH", new PatchApiDoc());
        docMapping.put("DELETE", new DeleteApiDoc());
    }
    
    public void generate(DocOption docOpt) throws Exception {
        RoutingConfigParser parser = RoutingConfigParser.parser();
        RoutingConfig rc = null;
        
        // Read the routing-config.xml and configure the handler(s).
        if (docOpt.getCfgContent() != null) {
            rc = parser.read(docOpt.getCfgContent().getBytes());
        }
        else {
            rc = parser.read(docOpt.getConfigFile());
        }
        generate(docOpt, rc);
    }
    
    public void generate(DocOption docOpt, RoutingConfig rc) throws Exception {
        OpenApiModel model = new OpenApiModel();
        model.setOpenapi("3.0.0");
        
        // Add Info
        info(model);
        
        // Add ExternalDocs
        externalDoc(model);
        
        // Add servers
        servers(model, rc.getApi());
        
        // Add security
        security(model);
        
        model.setSecurity(Arrays.asList(Map.of("BearerAuth", Collections.EMPTY_LIST)));
        
        Map<String, Path> paths = new LinkedHashMap<>();
        Map<String, Schema> schemas = new LinkedHashMap<>();
        List<String> resources = new ArrayList<>();
        
        for (ResourceMapping resourceMapping : rc.getResourceMapping()) {
            if (resourceMapping.isFailure() != null && resourceMapping.isFailure()) {
                continue;
            }
            resources.add(resourceMapping.getName());
            
            for (Mapping mapping : resourceMapping.getMapping()) {
                if (docOpt.getMethods() != null
                        && ! docOpt.getMethods().isEmpty()
                        && ! docOpt.getMethods().contains(mapping.getMethod().toLowerCase())) {
                    continue;
                }
                String pathUrl = resourceMapping.getPath() +  mapping.getUri();
                if (! paths.containsKey(pathUrl)) {
                    paths.put(pathUrl, new Path());
                }
                Boolean getAll = Boolean.TRUE;
                AbstractDoc doc = docMapping.get(mapping.getMethod().toUpperCase());
                
                if (mapping.getMethod().equalsIgnoreCase("GET")) {
                    // Distinguish get() vs getAll()
                    int idx =  mapping.getUri().lastIndexOf("/");
                    if (idx != -1 &&  mapping.getUri().charAt(idx + 1) == ':') {
                        getAll = Boolean.FALSE;
                    }
                    if (getAll) {
                        doc = docMapping.get("GETALL");
                    }
                }
                else if (! docMapping.containsKey(mapping.getMethod().toUpperCase())) {
                    throw new UnsupportedOperationException("No support for http method: " + mapping.getMethod());
                }
                Operation operation = null;
                
                if (resourceMapping.getName().equals("AuthToken")) {
                    // handle login separately. Login does not have a payload associated with it
                    operation = loginDoc.generate(resourceMapping.getSchema(), rc.getApi().getConsume());
                }
                else {
                    if (mapping.getMethod().equalsIgnoreCase("POST") && mapping.getUri().endsWith("/$batch")) {
                        // Special handling for batch api.
                        doc = docMapping.get(mapping.getMethod().toUpperCase() + "$batch");
                        operation = doc.generate(mapping.getSchema() != null
                                ? mapping.getSchema()
                                : resourceMapping.getSchema(), rc.getApi().getConsume());
                    }
                    else {
                        operation = doc.generate(mapping.getSchema() != null
                                ? mapping.getSchema()
                                : resourceMapping.getSchema(), rc.getApi().getConsume());
                    }
                }
                Path path = paths.get(pathUrl);
                invoke(path, operation, mapping.getMethod());
                
                // Add the api specific response
                Schema resSchema = null;
                if (resourceMapping.getName().equals("AuthToken")) {
                    resSchema = loginDoc.response(resourceMapping.getSchema(), rc.getApi().getBasePath() + pathUrl);
                }
                else {
                    resSchema = doc.response(mapping.getSchema() != null
                        ? mapping.getSchema()
                        : resourceMapping.getSchema(), rc.getApi().getBasePath() + pathUrl);
                }
                if (resSchema != null) {
                    schemas.put(resSchema.getKey(), resSchema);
                }
            }
        }
        model.setPaths(paths);
        
        // Add Tags
        tags(model, resources);
        
        // Add the default schema for error handlers
        schemas.put("bad_request", new BadRequestSpec().schema());      // 400
        schemas.put("un_authorized", new UnAuthorisedSpec().schema());  // 401
        schemas.put("not_found", new NotFoundSpec().schema());          // 404
        schemas.put("conflict", new ConflictSpec().schema());           // 409
        schemas.put("invalid_media", new InvalidMediaSpec().schema());  // 415
        schemas.put("many_request", new ManyRequestSpec().schema());    // 429
        schemas.put("server_error", new ServerErrorSpec().schema());    // 500
        
        // Add the components
        if (model.getComponents() == null) {
            Components components = new Components();
            model.setComponents(components);
        }
        model.getComponents().setSchemas(schemas);
        
        // model.setComponents(components);
        
        // Now dump ...
        write(model, docOpt);
    }
    
    private void invoke(Path path, Operation op, String name) {
        try {
            String nm = name.toLowerCase();
            Method method = path.getClass().getDeclaredMethod(
                    "set" + Character.toUpperCase(nm.charAt(0)) + nm.substring(1)
                    , new Class[] {Operation.class});
            
            method.invoke(path, new Object[] {op});
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void info(OpenApiModel model) {
        Info info = new Info();
        info.setVersion("2.0.0");
        info.setTitle("Example Rest Service");
        info.setDescription("A sample project to illustrate vertx rest api");
        info.setTermsOfService("Click here to read the terms and condition");
        
        model.setInfo(info);
    }
    
    private void externalDoc(OpenApiModel model) {
        ExternalDoc extDoc = new ExternalDoc();
        extDoc.setUrl("https://vertx.io/docs/vertx-web/java/");
        extDoc.setDescription("Find out additional documentation");
        
        model.setExternalDocs(extDoc);
    }
    
    private void tags(OpenApiModel model, List<String> resources) {
        List<Tag> tags = new ArrayList<>(resources.size());
        
        for (String resource : resources) {
            tags.add(new Tag(resource, "This module exposes the end points for handling " + resource, null));
        }
        model.setTags(tags);
    }
    
    private void servers(OpenApiModel model, Api api) {
        // Map<String, Variable> variables = new HashMap<>();
        
        // Variable var = new Variable();
        // var.setDefault("example");
        // var.setEnum(Arrays.asList("localhost", "example-dev", "example-qa", "example"));
        // variables.put("environment", var);
        
        // var = new Variable();
        // var.setDefault("443");
        // var.setEnum(Arrays.asList("8080", "443", "8443"));
        // variables.put("port", var);
        
        List<Server> servers = new ArrayList<>(3);
        Server server = new Server();
        server.setDescription("Dev");
        server.setUrl("https://example-dev.javalabs.org:443" + api.getBasePath());
        // server.setVariables(variables);
        servers.add(server);
        
        server = new Server();
        server.setDescription("QA");
        server.setUrl("https://example-qa.javalabs.org:443" + api.getBasePath());
        // server.setVariables(variables);
        servers.add(server);
        
        server = new Server();
        server.setDescription("Prod");
        server.setUrl("https://example.javalabs.org:443" + api.getBasePath());
        // server.setVariables(variables);
        servers.add(server);
        
        model.setServers(servers);
    }
    
    private void write(OpenApiModel model, DocOption docOpt) {
        try {
            // String s = MapperUtil.ymlMapper().writeValueAsString(model);
            // ClassWriter.write(new File("~/openapi.yaml"), s, docOpt.getVerbose());
            
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            // options.setIndicatorIndent(1);

            Representer representer = new YamlRepresenter(options);
            representer.addClassTag(OpenApiModel.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            representer.addClassTag(Reference.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            representer.addClassTag(Schema.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            
            Yaml yaml = new Yaml(representer, options);
            String result = yaml.dump(model);
            
            // Now, write to file.
            if (docOpt.getOutFile() != null) {
                File file = new File(docOpt.getOutFile());
                if (file.exists()) {
                    file.delete();
                }
                ClassWriter.write(file, result, docOpt.getVerbose());
                ConsoleWriter.timingPrintln("Wrote openapi file to " + file.getAbsolutePath());
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void security(OpenApiModel model) {
        Map<String, SecurityScheme> secs = new HashMap<>();
        
        SecurityScheme sec = new SecurityScheme();
        sec.setType("http");
        sec.setScheme("basic");
        secs.put("BasicAuth", sec);
        
        sec = new SecurityScheme();
        sec.setType("http");
        sec.setScheme("bearer");
        sec.setBearerFormat("JWT");
        secs.put("BearerAuth", sec);
        
        if (model.getComponents() == null) {
            Components components = new Components();
            model.setComponents(components);
        }
        model.getComponents().setSecuritySchemes(secs);
    }
}
