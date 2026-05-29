package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.config.parser.JAXBRoutingConfigParser;
import org.javalabs.decl.vertx.jaxb.Api;
import org.javalabs.decl.vertx.jaxb.Mapping;
import org.javalabs.decl.vertx.jaxb.ResourceMapping;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class RoutingConfigParserTest {
    
    @Test
    public void testRead() {
        try {
            JAXBRoutingConfigParser parser = new JAXBRoutingConfigParser();
            RoutingConfig config = parser.read("routing-config-test.xml");

            Assertions.assertNotNull(config);
            
            Assertions.assertEquals("com.example.rest", config.getPackage().getName());
            
            Api api = config.getApi();
            Assertions.assertNotNull(api);
            Assertions.assertEquals("1.0", api.getVersion());
            Assertions.assertEquals("/api/v1", api.getBasePath());
            Assertions.assertEquals("application/vnd.ex+json.v1", api.getProduce());
            Assertions.assertEquals("application/json", api.getConsume());
            
            List<ResourceMapping> list = config.getResourceMapping();
            Assertions.assertEquals(1, list.size());
            
            ResourceMapping rm = list.get(0);
            Assertions.assertEquals("Employee", rm.getName());
            Assertions.assertEquals("/employees", rm.getPath());
            Assertions.assertEquals("com.example.rest.handler.EmployeeHandler", rm.getResource());
            Assertions.assertEquals("com.example.rest.model.Employee", rm.getSchema());
            
            List<Mapping> tmp = rm.getMapping();
            Assertions.assertEquals(5, tmp.size());
            
            Assertions.assertEquals("", tmp.get(0).getUri());
            Assertions.assertEquals("POST", tmp.get(0).getMethod());
            Assertions.assertEquals("create", tmp.get(0).getApi());
            
            Assertions.assertEquals("/:id", tmp.get(1).getUri());
            Assertions.assertEquals("PUT", tmp.get(1).getMethod());
            Assertions.assertEquals("modify", tmp.get(1).getApi());
            
            Assertions.assertEquals("/:id", tmp.get(2).getUri());
            Assertions.assertEquals("GET", tmp.get(2).getMethod());
            Assertions.assertEquals("view", tmp.get(2).getApi());
            
            Assertions.assertEquals("/:id", tmp.get(3).getUri());
            Assertions.assertEquals("DELETE", tmp.get(3).getMethod());
            Assertions.assertEquals("remove", tmp.get(3).getApi());
            
            Assertions.assertEquals("", tmp.get(4).getUri());
            Assertions.assertEquals("GET", tmp.get(4).getMethod());
            Assertions.assertEquals("viewAll", tmp.get(4).getApi());

        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }
}
