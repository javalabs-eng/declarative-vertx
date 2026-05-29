package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.config.parser.JAXBWebConfigParser;
import org.javalabs.decl.vertx.jaxb.AddressResolverOptions;
import org.javalabs.decl.vertx.jaxb.ContextParams;
import org.javalabs.decl.vertx.jaxb.EventBusOptions;
import org.javalabs.decl.vertx.jaxb.FileSystemOptions;
import org.javalabs.decl.vertx.jaxb.MetricsOptions;
import org.javalabs.decl.vertx.jaxb.Verticle;
import org.javalabs.decl.vertx.jaxb.Verticles;
import org.javalabs.decl.vertx.jaxb.VertxWeb;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Sudiptasish Chanda
 */
public class WebConfigParserTest {
    
    @Test
    public void testRead() {
        try {
            JAXBWebConfigParser parser = new JAXBWebConfigParser();
            VertxWeb vertxWeb = parser.read("vertx-web-test.xml");

            Assertions.assertNotNull(vertxWeb);

            // Test the inidividual attributes.
            ContextParams params = vertxWeb.getContextParams();
            Assertions.assertNotNull(params);

            Assertions.assertEquals(3, params.getEventLoopPoolSize());
            Assertions.assertEquals(14, params.getWorkerPoolSize());
            Assertions.assertEquals(1000, params.getBlockedThreadCheckInterval());
            Assertions.assertEquals(2000, params.getMaxEventLoopExecuteTime());
            Assertions.assertEquals(60000, params.getMaxWorkerExecuteTime());
            Assertions.assertEquals(24, params.getInternalBlockingPoolSize());
            Assertions.assertEquals("", params.getClusterManager());
            Assertions.assertEquals(true, params.isHaEnabled());
            Assertions.assertEquals(3, params.getQuorumSize());
            Assertions.assertEquals("TestHAGroup", params.getHaGroup());
            Assertions.assertEquals("com.test.TestErrorHandler", params.getErrorHandler());
            
            EventBusOptions ebOpt = params.getEventBusOptions();
            Assertions.assertNotNull(ebOpt);
            
            Assertions.assertEquals("", ebOpt.getClusterPublicHost());
            Assertions.assertEquals(20000, ebOpt.getClusterPingInterval());
            Assertions.assertEquals(20000, ebOpt.getClusterPingReplyInterval());
            Assertions.assertEquals(-1, ebOpt.getAcceptBacklog());
            Assertions.assertEquals(1000, ebOpt.getReconnectInterval());
            Assertions.assertEquals(1, ebOpt.getReconnectAttempts());
            Assertions.assertEquals(60000, ebOpt.getConnectTimeout());
            Assertions.assertEquals(true, ebOpt.isTrustAll());
            
            FileSystemOptions fsOpt = params.getFileSystemOptions();
            Assertions.assertNotNull(fsOpt);
            
            Assertions.assertEquals(true, fsOpt.isClassPathResolvingEnabled());
            Assertions.assertEquals(true, fsOpt.isFileCachingEnabled());
            
            MetricsOptions mOpt = params.getMetricsOptions();
            Assertions.assertNotNull(mOpt);
            
            Assertions.assertEquals(false, mOpt.isEnabled());
            
            AddressResolverOptions asOpt = params.getAddressResolverOptions();
            Assertions.assertNotNull(asOpt);
            
            Verticles verticles = vertxWeb.getVerticles();
            Assertions.assertNotNull(verticles);
            
            List<Verticle> list = verticles.getVerticle();
            Assertions.assertEquals(2, list.size());
            
            Verticle verticle = list.get(0);
            Assertions.assertEquals("app.http.server", verticle.getName());
            Assertions.assertEquals("org.javalabs.decl.vertx.HttpServer", verticle.getClazz());
            Assertions.assertEquals(false, verticle.getDeployOptions().isWorker());
            Assertions.assertEquals(true, verticle.getDeployOptions().isHa());
            Assertions.assertEquals(3, verticle.getDeployOptions().getInstances());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }
    
    @Test
    public void testReadDefault() {
        try {
            JAXBWebConfigParser parser = new JAXBWebConfigParser();
            VertxWeb vertxWeb = parser.read("vertx-web-default.xml");

            Assertions.assertNotNull(vertxWeb);

            // Test the inidividual attributes.
            ContextParams params = vertxWeb.getContextParams();
            Assertions.assertNotNull(params);

            Assertions.assertEquals(2, params.getEventLoopPoolSize());
            Assertions.assertEquals(20, params.getWorkerPoolSize());
            Assertions.assertEquals(1000, params.getBlockedThreadCheckInterval());
            Assertions.assertEquals(2000, params.getMaxEventLoopExecuteTime());
            Assertions.assertEquals(60000, params.getMaxWorkerExecuteTime());
            Assertions.assertEquals(20, params.getInternalBlockingPoolSize());
            Assertions.assertEquals("", params.getClusterManager());
            Assertions.assertEquals(false, params.isHaEnabled());
            Assertions.assertEquals(1, params.getQuorumSize());
            Assertions.assertEquals("__DEFAULT__", params.getHaGroup());
            Assertions.assertEquals("org.javalabs.decl.vertx.container.ContainerErrorHandler", params.getErrorHandler());
            
            EventBusOptions ebOpt = params.getEventBusOptions();
            Assertions.assertNotNull(ebOpt);
            
            Assertions.assertEquals("", ebOpt.getClusterPublicHost());
            Assertions.assertEquals(20000, ebOpt.getClusterPingInterval());
            Assertions.assertEquals(20000, ebOpt.getClusterPingReplyInterval());
            Assertions.assertEquals(-1, ebOpt.getAcceptBacklog());
            Assertions.assertEquals(1000, ebOpt.getReconnectInterval());
            Assertions.assertEquals(1, ebOpt.getReconnectAttempts());
            Assertions.assertEquals(60000, ebOpt.getConnectTimeout());
            Assertions.assertEquals(true, ebOpt.isTrustAll());
            
            FileSystemOptions fsOpt = params.getFileSystemOptions();
            Assertions.assertNotNull(fsOpt);
            
            Assertions.assertEquals(true, fsOpt.isClassPathResolvingEnabled());
            Assertions.assertEquals(true, fsOpt.isFileCachingEnabled());
            
            MetricsOptions mOpt = params.getMetricsOptions();
            Assertions.assertNotNull(mOpt);
            
            Assertions.assertEquals(false, mOpt.isEnabled());
            
            AddressResolverOptions asOpt = params.getAddressResolverOptions();
            Assertions.assertNotNull(asOpt);
            
            Verticles verticles = vertxWeb.getVerticles();
            Assertions.assertNotNull(verticles);
            
            List<Verticle> list = verticles.getVerticle();
            Assertions.assertEquals(1, list.size());
            
            Verticle verticle = list.get(0);
            Assertions.assertEquals("app.http.server", verticle.getName());
            Assertions.assertEquals("org.javalabs.decl.vertx.HttpServer", verticle.getClazz());
            Assertions.assertEquals(false, verticle.getDeployOptions().isWorker());
            Assertions.assertEquals(false, verticle.getDeployOptions().isHa());
            Assertions.assertEquals(1, verticle.getDeployOptions().getInstances());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }
    
    @Test
    public void testMissingVerticle() {
        try {
            JAXBWebConfigParser parser = new JAXBWebConfigParser();
            VertxWeb vertxWeb = parser.read("vertx-web-missing.xml");

            Assertions.assertNotNull(vertxWeb);
        }
        catch (Exception e) {
            Assertions.assertTrue(e.getCause().getCause() instanceof SAXParseException);
            Assertions.assertTrue(e.getCause().getCause().getMessage().contains("The content of element 'vertx-web' is not complete. One of '{verticles}' is expected."));
        }
    }
    
    @Test
    public void testReadCompact() {
        try {
            JAXBWebConfigParser parser = new JAXBWebConfigParser();
            VertxWeb vertxWeb = parser.read("vertx-web-compact.xml");

            Assertions.assertNotNull(vertxWeb);
            
            // Test the inidividual attributes.
            ContextParams params = vertxWeb.getContextParams();
            Assertions.assertNotNull(params);

            Assertions.assertEquals(2, params.getEventLoopPoolSize());
            Assertions.assertEquals(20, params.getWorkerPoolSize());
            Assertions.assertEquals(1000, params.getBlockedThreadCheckInterval());
            Assertions.assertEquals(2000, params.getMaxEventLoopExecuteTime());
            Assertions.assertEquals(60000, params.getMaxWorkerExecuteTime());
            Assertions.assertEquals(20, params.getInternalBlockingPoolSize());
            //Assertions.assertEquals("", params.getClusterManager());
            Assertions.assertEquals(false, params.isHaEnabled());
            Assertions.assertEquals(1, params.getQuorumSize());
            Assertions.assertEquals("__DEFAULT__", params.getHaGroup());
            Assertions.assertEquals("org.javalabs.decl.vertx.container.ContainerErrorHandler", params.getErrorHandler());
            
            EventBusOptions ebOpt = params.getEventBusOptions();
            Assertions.assertNotNull(ebOpt);
            
            //Assertions.assertEquals("", ebOpt.getClusterPublicHost());
            Assertions.assertEquals(20000, ebOpt.getClusterPingInterval());
            Assertions.assertEquals(20000, ebOpt.getClusterPingReplyInterval());
            Assertions.assertEquals(-1, ebOpt.getAcceptBacklog());
            Assertions.assertEquals(1000, ebOpt.getReconnectInterval());
            Assertions.assertEquals(1, ebOpt.getReconnectAttempts());
            Assertions.assertEquals(60000, ebOpt.getConnectTimeout());
            Assertions.assertEquals(true, ebOpt.isTrustAll());
            
            FileSystemOptions fsOpt = params.getFileSystemOptions();
            Assertions.assertNotNull(fsOpt);
            
            Assertions.assertEquals(true, fsOpt.isClassPathResolvingEnabled());
            Assertions.assertEquals(true, fsOpt.isFileCachingEnabled());
            
            MetricsOptions mOpt = params.getMetricsOptions();
            Assertions.assertNotNull(mOpt);
            
            Assertions.assertEquals(false, mOpt.isEnabled());
            
            AddressResolverOptions asOpt = params.getAddressResolverOptions();
            Assertions.assertNotNull(asOpt);
            
            Verticles verticles = vertxWeb.getVerticles();
            Assertions.assertNotNull(verticles);
            
            List<Verticle> list = verticles.getVerticle();
            Assertions.assertEquals(1, list.size());
            
            Verticle verticle = list.get(0);
            Assertions.assertEquals("MyVerticle", verticle.getName());
            Assertions.assertEquals("org.javalabs.test.MyVerticle", verticle.getClazz());
            Assertions.assertEquals(false, verticle.getDeployOptions().isWorker());
            Assertions.assertEquals(false, verticle.getDeployOptions().isHa());
            Assertions.assertEquals(1, verticle.getDeployOptions().getInstances());
            Assertions.assertEquals("/path/to/server.xml", verticle.getConfig());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }
}
