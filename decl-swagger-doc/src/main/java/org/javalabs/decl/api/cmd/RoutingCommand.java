package org.javalabs.decl.api.cmd;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.api.project.TechStack;
import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.CharUtil;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.Package;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import org.javalabs.decl.workflow.Command;
import static org.javalabs.decl.workflow.Command.CONTINUE;
import org.javalabs.decl.workflow.Context;
import org.javalabs.decl.writer.ClassWriter;

/**
 * Create the vert.x specific routing configuration file.
 *
 * @author schan280
 */
public class RoutingCommand implements Command {
    
    private static final String ROUTING_CONFIG  = "routing-config.xml";
    
    private static final String[][] STD_APIS = {
            {"", "POST", "create"},
            {"/$batch", "POST", "batchCreate"},
            {"/:id", "PUT", "modify"},
            {"/:id", "GET", "view"},
            {"/:id", "DELETE", "remove"},
            {"", "GET", "viewAll"}
    };

    private final String name;
    
    public RoutingCommand(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Future<?> execute(Context ctx) {
        Project project = (Project)ctx.get("project.work");
        
        try {
            Map<String, JavaClass> classes = (Map)ctx.get("resource.names");
            RoutingConfig rc = new RoutingConfig();
            
            Package pkg = new Package();
            pkg.setName(project.handlerPkg());
            rc.setPackage(pkg);
            
            Api api = new Api();
            api.setVersion("1.0");
            api.setBasePath("/api/v1");
            api.setConsume("application/json");
            api.setProduce("application/vnd.ex+json.v1");
            rc.setApi(api);
            
            List<ResourceMapping> resources = new ArrayList<>(classes.size() + 1);
            
            // Add error handler
            ResourceMapping rm = new ResourceMapping();
            rm.setName("error");
            rm.setPath("/*");
            rm.setResource("org.javalabs.decl.vertx.container.handler.HttpErrorHandler");
            rm.setFailure(Boolean.TRUE);
            resources.add(rm);
            
            // Following packages are needed only if it is an end-to-end project.
            Mapping m = null;
            
            if (ctx.currentChain().name().equals("java_project_e2e") || ctx.currentChain().name().equals("java_project_e2e_db")) {
                // Login handler
                rm = new ResourceMapping();
                rm.setName("AuthToken");
                rm.setPath("/mgmt/login");
                rm.setResource(project.handlerPkg() + "." + "AuthenticationHandler");
                rm.setSchema(project.authPkg() + "." + "AuthToken");

                m = new Mapping();
                m.setUri("");
                m.setMethod("POST");
                m.setApi("authenticate");
                rm.getMapping().add(m);

                resources.add(rm);
            }
            
            // Now, add the remaining handlers.
            for (Map.Entry<String, JavaClass> me : classes.entrySet()) {
                String name = me.getKey();
                String tmp = CharUtil.lowerFirst(name);
                
                rm = new ResourceMapping();
                rm.setName(name);
                rm.setPath("/" + CharUtil.plural(tmp));
                rm.setResource(project.handlerPkg() + "." + name + "Handler");
                rm.setSchema(project.modelPkg() + "." + name);
                
                for (int i = 0; i < STD_APIS.length; i ++) {
                    m = new Mapping();
                    m.setUri(STD_APIS[i][0]);
                    m.setMethod(STD_APIS[i][1]);
                    m.setApi(STD_APIS[i][2]);
                    rm.getMapping().add(m);
                }
                resources.add(rm);
            }
            rc.setResourceMapping(resources);
            
            JAXBContext context = JAXBContext.newInstance(RoutingConfig.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
            marshaller.marshal(rc, out);
            out.flush();
            
            String xml = new String(out.toByteArray());
            ctx.add("routing.config", xml);
            
            if (project.stack() == TechStack.VERTX) {
                File projectRoot = new File(project.dir(), project.name());
                File cfgFile = new File(projectRoot.getAbsolutePath() + File.separator + project.srcResourceDir(), ROUTING_CONFIG);
                ClassWriter.write(cfgFile, xml, project.verbose());

                if (project.verbose() <= 2) {
                    ConsoleWriter.timingPrintln("Created vert.x " + ConsoleWriter.ANSI_GREEN + ROUTING_CONFIG + ConsoleWriter.ANSI_RESET);
                }
            }
            
            return CompletableFuture.completedFuture(CONTINUE);
        }
        catch (JAXBException | IOException | RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public void backtrack(Context ctx) {
        // Do Nothing
    }
}
