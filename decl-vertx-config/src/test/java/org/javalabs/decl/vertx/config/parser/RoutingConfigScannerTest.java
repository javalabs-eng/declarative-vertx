package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.config.sample.SampleHandler;
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
public class RoutingConfigScannerTest {
 
    @Test
    public void testScan() {
        RoutingConfigScanner scanner = new RoutingConfigScanner();
        // RoutingConfig rc = scanner.scan(new String[] {"org.javalabs.decl.vertx.config.sample"});
        RoutingConfig rc = scanner.scan();
        
        Assertions.assertTrue(rc.getResourceMapping().size() == 1);
        List<ResourceMapping> rms = rc.getResourceMapping();
        for (ResourceMapping rm : rms) {
            Assertions.assertEquals(SampleHandler.class.getName(), rm.getResource());
            Assertions.assertEquals("sample", rm.getName());
            Assertions.assertEquals("/api/v1/sample", rm.getPath());
            
            for (Mapping m : rm.getMapping()) {
                if ("create".equals(m.getApi())) {
                    Assertions.assertEquals("/employees", m.getUri());
                    Assertions.assertTrue("post".equalsIgnoreCase(m.getMethod()));
                    Assertions.assertEquals("application/json", m.getConsume());
                    Assertions.assertEquals("application/json", m.getProduce());
                }
                else if ("getAll".equals(m.getApi())) {
                    Assertions.assertEquals("/employees", m.getUri());
                    Assertions.assertTrue("get".equalsIgnoreCase(m.getMethod()));
                    Assertions.assertEquals("application/json", m.getConsume());
                    Assertions.assertEquals("application/json", m.getProduce());
                }
                else if ("get".equals(m.getApi())) {
                    Assertions.assertEquals("/employees/:id", m.getUri());
                    Assertions.assertTrue("get".equalsIgnoreCase(m.getMethod()));
                    Assertions.assertEquals("application/json", m.getConsume());
                    Assertions.assertEquals("application/json", m.getProduce());
                }
                else if ("modify".equals(m.getApi())) {
                    Assertions.assertEquals("/employees/:id", m.getUri());
                    Assertions.assertTrue("put".equalsIgnoreCase(m.getMethod()));
                    Assertions.assertEquals("application/json", m.getConsume());
                    Assertions.assertEquals("application/json", m.getProduce());
                }
                else if ("remove".equals(m.getApi())) {
                    Assertions.assertEquals("/employees/:id", m.getUri());
                    Assertions.assertTrue("delete".equalsIgnoreCase(m.getMethod()));
                    Assertions.assertEquals("application/json", m.getConsume());
                    Assertions.assertEquals("application/json", m.getProduce());
                }
            }
        }
    }
}
