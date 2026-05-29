package org.javalabs.decl.api.cust;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.gen.JavaImport;
import org.javalabs.decl.gen.JavaMethod;
import org.javalabs.decl.gen.JavaPackage;
import org.javalabs.decl.vertx.config.parser.RoutingConfigParser;
import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;

/**
 * Class for generating the vert.x handler resources.
 *
 * @author Sudiptasish Chanda
 */
public class ResourceGenerator {
    
    private static final String DEFAULT_CONFIG = "routing-config.xml";
    
    /**
     * Read the default <code>routing-config.xml</code> file and generate the skeletons.
     * @return List
     */
    public List<JavaClass> generate() {
        Class<?>[] imports = {Vertx.class, RoutingContext.class, Json.class};
        String rtFile = System.getProperty("routing.config", DEFAULT_CONFIG);
        
        RoutingConfigParser parser = RoutingConfigParser.parser();
        RoutingConfig rc = parser.read(rtFile);
        
        Api api = rc.getApi();
        List<ResourceMapping> resources = rc.getResourceMapping();
        
        StringBuilder body = new StringBuilder(4096);
        List<JavaClass> classes = new ArrayList<>(resources.size());
        
        try {
            for (ResourceMapping resource : resources) {
                String[] path = resource.getResource().split("\\.");
                String className = path[path.length - 1];
                
                JavaClass jClass = new JavaClass(className);
                
                String dir = null;
                String packageName = null;
                
                if (path.length > 1) {
                    String[] tmp = new String[path.length - 1];
                    System.arraycopy(path, 0, tmp, 0, path.length - 1);
                    
                    dir = String.join("/", tmp);
                    packageName = String.join(".", tmp);
                }
                jClass.dir(dir);
                
                JavaPackage jPkg = new JavaPackage();
                jPkg.name(packageName);
                jClass.pkg(jPkg);
                
                for (Class<?> cl : imports) {
                    jClass.addImport(new JavaImport(cl));
                }
                
                if (resource.getMapping().isEmpty()) {
                    JavaMethod jm = new JavaMethod(jClass);
                    noOpBody(body);
                    
                    jm.name("handle")
                        .argTypes(RoutingContext.class)
                        .body(body.toString());
                    
                    jClass.addMethod(jm);
                    body.delete(0, body.length());
                }
                else {
                    String schema = resource.getSchema();
                    
                    for (Mapping mapping : resource.getMapping()) {
                        JavaMethod jm = new JavaMethod(jClass);
                        if (mapping.getSchema() != null && mapping.getSchema().trim().length() > 0) {
                            schema = mapping.getSchema();
                        }
                        if (mapping.getMethod().equalsIgnoreCase("POST") || mapping.getMethod().equalsIgnoreCase("PUT")) {
                            if (schema != null && schema.length() > 0) {
                                Class<?> cl = Class.forName(mapping.getSchema());
                                
                                JavaImport jImp = new JavaImport();
                                jImp.set(cl);
                                jClass.addImport(jImp);
                                
                                deser(body, cl);
                            }
                        }
                        noOpBody(body);
                        
                        jm.name(mapping.getApi())
                            .argTypes(RoutingContext.class)
                            .body(body.toString());

                        jClass.addMethod(jm);
                        body.delete(0, body.length());
                    }
                }
                classes.add(jClass);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
    
    private void noOpBody(StringBuilder body) {
        body.append("\t\t").append("// Add your logic")
            .append("\n\n\t\t").append("// Send response")
            .append("\n\t\t").append("routingContext.response().end(\"Success\");");
    }
    
    private void deser(StringBuilder body, Class<?> cl) {
        String sn = cl.getSimpleName();
        
        body.append("\t\t")
            .append(sn)
            .append(" ")
            .append(sn.toLowerCase())
            .append(" = ")
            .append("Json.decodeValue(routingContext.body().buffer()")
            .append(", ")
            .append(sn)
            .append(".class")
            .append(");");

        body.append("\n\n");
    }
}
