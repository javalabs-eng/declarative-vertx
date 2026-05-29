package org.javalabs.decl.vertx.config.parser;

import org.javalabs.decl.vertx.config.parser.JAXBWebConfigParser;
import org.javalabs.decl.vertx.config.parser.JAXBServerConfigParser;
import org.javalabs.decl.vertx.jaxb.AccessLog;
import org.javalabs.decl.vertx.jaxb.AllowedHeaders;
import org.javalabs.decl.vertx.jaxb.AllowedMethods;
import org.javalabs.decl.vertx.jaxb.CrossOrigin;
import org.javalabs.decl.vertx.jaxb.HttpOpts;
import org.javalabs.decl.vertx.jaxb.NetworkOpts;
import org.javalabs.decl.vertx.jaxb.SecurityConstraint;
import org.javalabs.decl.vertx.jaxb.WebServerConfig;
import org.javalabs.decl.vertx.jaxb.ServerOpts;
import org.javalabs.decl.vertx.jaxb.SslOpts;
import org.javalabs.decl.vertx.jaxb.TcpOpts;
import org.javalabs.decl.vertx.jaxb.UrlPatterns;
import org.javalabs.decl.vertx.jaxb.VertxWeb;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ServerConfigParserTest {
    
    @Test
    public void testRead() {
        try {
            JAXBServerConfigParser parser = new JAXBServerConfigParser();
            WebServerConfig config = parser.read("server-test.xml");

            Assertions.assertNotNull(config);

            // Test the inidividual attributes.
            ServerOpts sOpts = config.getServerOpts();
            Assertions.assertNotNull(sOpts);

            Assertions.assertEquals(true, sOpts.isSsl());
            Assertions.assertEquals("0.0.0.0", sOpts.getHost());
            Assertions.assertEquals(8999, sOpts.getPort());
            Assertions.assertEquals(5, sOpts.getAcceptBacklog());
            Assertions.assertEquals("NONE", sOpts.getClientAuth());
            Assertions.assertEquals(false, sOpts.isSni());
            Assertions.assertEquals(false, sOpts.isUseProxyProtocol());
            Assertions.assertEquals(20000, sOpts.getProxyProtocolTimeout());
            Assertions.assertEquals(true, sOpts.isRegisterWriteHandler());
            
            TcpOpts tOpts = config.getTcpOpts();
            Assertions.assertNotNull(tOpts);
            
            Assertions.assertEquals(true, tOpts.isTcpNoDelay());
            Assertions.assertEquals(true, tOpts.isTcpKeepAlive());
            Assertions.assertEquals(90, tOpts.getSoLinger());
            Assertions.assertEquals(80000, tOpts.getIdleTimeout());
            Assertions.assertEquals(70000, tOpts.getReadIdleTimeout());
            Assertions.assertEquals(60000, tOpts.getWriteIdleTimeout());
            Assertions.assertEquals(false, tOpts.isTcpFastOpen());
            Assertions.assertEquals(false, tOpts.isTcpCork());
            Assertions.assertEquals(true, tOpts.isTcpQuickAck());
            Assertions.assertEquals(60000, tOpts.getTcpUserTimeout());
            
            SslOpts sslOpts = tOpts.getSslOpts();
            Assertions.assertEquals("CSG256B", sslOpts.getEnabledCipherSuites());
            Assertions.assertEquals(1, sslOpts.getSslHandshakeTimeout());
            Assertions.assertEquals(true, sslOpts.isUseAlpn());
            Assertions.assertEquals("Tlsv1.1,Tlsv1.4", sslOpts.getEnabledSecureTransportProtocols());
            
            NetworkOpts nOpts = config.getNetworkOpts();
            Assertions.assertNotNull(nOpts);
            
            Assertions.assertEquals(16777216, nOpts.getSendBufferSize());
            Assertions.assertEquals(16777216, nOpts.getReceiveBufferSize());
            Assertions.assertEquals(true, nOpts.isReuseAddress());
            Assertions.assertEquals(-1, nOpts.getTrafficClass());
            Assertions.assertEquals(false, nOpts.isLogActivity());
            Assertions.assertEquals(false, nOpts.isReusePort());
            
            HttpOpts hOpts = config.getHttpOpts();
            Assertions.assertNotNull(hOpts);

            Assertions.assertEquals(false, hOpts.isCompressionSupported());
            Assertions.assertEquals(32, hOpts.getCompressionLevel());
            Assertions.assertEquals(655361, hOpts.getMaxWebSocketFrameSize());
            Assertions.assertEquals(2621441, hOpts.getMaxWebSocketMessageSize());
            Assertions.assertEquals(true, hOpts.isHandle100ContinueAutomatically());
            Assertions.assertEquals(81925, hOpts.getMaxChunkSize());
            Assertions.assertEquals(40965, hOpts.getMaxInitialLineLength());
            Assertions.assertEquals(81925, hOpts.getMaxHeaderSize());
            Assertions.assertEquals(81925, hOpts.getMaxFormAttributeSize());
            Assertions.assertEquals(2565, hOpts.getMaxFormFields());
            Assertions.assertEquals(10245, hOpts.getMaxFormBufferedBytes());
            Assertions.assertEquals(true, hOpts.isHttp2ClearTextEnabled());
            Assertions.assertEquals(57, hOpts.getHttp2ConnectionWindowSize());
            Assertions.assertEquals(true, hOpts.isDecompressionSupported());
            Assertions.assertEquals(false, hOpts.isAcceptUnmaskedFrames());
            Assertions.assertEquals(128, hOpts.getDecoderInitialBufferSize());
            Assertions.assertEquals(true, hOpts.isPerFrameWebSocketCompressionSupported());
            Assertions.assertEquals(true, hOpts.isPerMessageWebSocketCompressionSupported());
            Assertions.assertEquals(16, hOpts.getWebSocketCompressionLevel());
            Assertions.assertEquals(false, hOpts.isWebSocketPreferredClientNoContext());
            Assertions.assertEquals(false, hOpts.isWebSocketAllowServerNoContext());
            Assertions.assertEquals(10006, hOpts.getWebSocketClosingTimeout());
            Assertions.assertEquals("NONE", hOpts.getTracingPolicy());
            Assertions.assertEquals(false, hOpts.isRegisterWebSocketWriteHandlers());
            Assertions.assertEquals(300, hOpts.getHttp2RstFloodMaxRstFramePerWindow());
            Assertions.assertEquals(50001, hOpts.getHttp2RstFloodWindowDuration());
            
            CrossOrigin co = config.getCrossOrigin();
            Assertions.assertNotNull(co);
            
            AllowedHeaders headers = co.getAllowedHeaders();
            AllowedMethods methods = co.getAllowedMethods();
            
            Assertions.assertNotNull(headers);
            Assertions.assertNotNull(methods);
            
            Assertions.assertEquals(14, headers.getAllowedHeader().size());
            Assertions.assertEquals(7, methods.getAllowedMethod().size());
            
            AccessLog acLog = config.getAccessLog();
            Assertions.assertNotNull(acLog);
            Assertions.assertEquals("com.test.TestLoggerFormatter", acLog.getLogFormat());
            Assertions.assertEquals("{CTX_ROOT}, /v1/kv", acLog.getRoute());
            
            SecurityConstraint sc = config.getSecurityConstraint();
            Assertions.assertNotNull(sc);
            Assertions.assertNotNull(sc.getAuthConstraint());
            
            Assertions.assertEquals("", sc.getAuthHandler());
            Assertions.assertEquals("NO_AUTH", sc.getAuthConstraint().getAuthType());
            
            UrlPatterns up = sc.getAuthConstraint().getUrlPatterns();
            Assertions.assertNotNull(up);
            List<String> list = up.getUrlPattern();
            
            Assertions.assertEquals(3, list.size());
            Assertions.assertEquals("{CTX_ROOT}/mgmt/", list.get(0));
            Assertions.assertEquals("{CTX_ROOT}/store/config/", list.get(1));
            Assertions.assertEquals("/v1/kv/", list.get(2));
            Assertions.assertEquals("conf/routing-config.xml", config.getRoutingConfig());

        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }
    
    @Test
    public void testReadDefault() {
        try {
            JAXBServerConfigParser parser = new JAXBServerConfigParser();
            WebServerConfig config = parser.read("server-default.xml");

            Assertions.assertNotNull(config);

            // Test the inidividual attributes.
            ServerOpts sOpts = config.getServerOpts();
            Assertions.assertNotNull(sOpts);

            Assertions.assertEquals(false, sOpts.isSsl());
            Assertions.assertEquals("0.0.0.0", sOpts.getHost());
            Assertions.assertEquals(8080, sOpts.getPort());
            Assertions.assertEquals(-1, sOpts.getAcceptBacklog());
            Assertions.assertEquals("NONE", sOpts.getClientAuth());
            Assertions.assertEquals(false, sOpts.isSni());
            Assertions.assertEquals(false, sOpts.isUseProxyProtocol());
            Assertions.assertEquals(10000, sOpts.getProxyProtocolTimeout());
            Assertions.assertEquals(false, sOpts.isRegisterWriteHandler());
            
            TcpOpts tOpts = config.getTcpOpts();
            Assertions.assertNotNull(tOpts);
            
            Assertions.assertEquals(true, tOpts.isTcpNoDelay());
            Assertions.assertEquals(false, tOpts.isTcpKeepAlive());
            Assertions.assertEquals(-1, tOpts.getSoLinger());
            Assertions.assertEquals(0, tOpts.getIdleTimeout());
            Assertions.assertEquals(0, tOpts.getReadIdleTimeout());
            Assertions.assertEquals(0, tOpts.getWriteIdleTimeout());
            Assertions.assertEquals(false, tOpts.isTcpFastOpen());
            Assertions.assertEquals(false, tOpts.isTcpCork());
            Assertions.assertEquals(false, tOpts.isTcpQuickAck());
            Assertions.assertEquals(0, tOpts.getTcpUserTimeout());
            
            SslOpts sslOpts = tOpts.getSslOpts();
            Assertions.assertNull(sslOpts.getEnabledCipherSuites());
            Assertions.assertEquals(10000, sslOpts.getSslHandshakeTimeout());
            Assertions.assertEquals(false, sslOpts.isUseAlpn());
            Assertions.assertEquals("TLSv1,TLSv1.1,TLSv1.2,TLSv1.3", sslOpts.getEnabledSecureTransportProtocols());
            
            NetworkOpts nOpts = config.getNetworkOpts();
            Assertions.assertNotNull(nOpts);
            
            Assertions.assertEquals(-1, nOpts.getSendBufferSize());
            Assertions.assertEquals(-1, nOpts.getReceiveBufferSize());
            Assertions.assertEquals(true, nOpts.isReuseAddress());
            Assertions.assertEquals(-1, nOpts.getTrafficClass());
            Assertions.assertEquals(false, nOpts.isLogActivity());
            Assertions.assertEquals(false, nOpts.isReusePort());
            
            HttpOpts hOpts = config.getHttpOpts();
            Assertions.assertNotNull(hOpts);

            Assertions.assertEquals(false, hOpts.isCompressionSupported());
            Assertions.assertEquals(6, hOpts.getCompressionLevel());
            Assertions.assertEquals(65536, hOpts.getMaxWebSocketFrameSize());
            Assertions.assertEquals(262144, hOpts.getMaxWebSocketMessageSize());
            Assertions.assertEquals(false, hOpts.isHandle100ContinueAutomatically());
            Assertions.assertEquals(8192, hOpts.getMaxChunkSize());
            Assertions.assertEquals(4096, hOpts.getMaxInitialLineLength());
            Assertions.assertEquals(8192, hOpts.getMaxHeaderSize());
            Assertions.assertEquals(8192, hOpts.getMaxFormAttributeSize());
            Assertions.assertEquals(256, hOpts.getMaxFormFields());
            Assertions.assertEquals(1024, hOpts.getMaxFormBufferedBytes());
            Assertions.assertEquals(true, hOpts.isHttp2ClearTextEnabled());
            Assertions.assertEquals(-1, hOpts.getHttp2ConnectionWindowSize());
            Assertions.assertEquals(false, hOpts.isDecompressionSupported());
            Assertions.assertEquals(false, hOpts.isAcceptUnmaskedFrames());
            Assertions.assertEquals(128, hOpts.getDecoderInitialBufferSize());
            Assertions.assertEquals(true, hOpts.isPerFrameWebSocketCompressionSupported());
            Assertions.assertEquals(true, hOpts.isPerMessageWebSocketCompressionSupported());
            Assertions.assertEquals(6, hOpts.getWebSocketCompressionLevel());
            Assertions.assertEquals(false, hOpts.isWebSocketPreferredClientNoContext());
            Assertions.assertEquals(false, hOpts.isWebSocketAllowServerNoContext());
            Assertions.assertEquals(10000, hOpts.getWebSocketClosingTimeout());
            Assertions.assertEquals("ALWAYS", hOpts.getTracingPolicy());
            Assertions.assertEquals(false, hOpts.isRegisterWebSocketWriteHandlers());
            Assertions.assertEquals(200, hOpts.getHttp2RstFloodMaxRstFramePerWindow());
            Assertions.assertEquals(30000, hOpts.getHttp2RstFloodWindowDuration());
            
            CrossOrigin co = config.getCrossOrigin();
            Assertions.assertNotNull(co);
            
            AllowedHeaders headers = co.getAllowedHeaders();
            AllowedMethods methods = co.getAllowedMethods();
            
            Assertions.assertNull(headers);
            Assertions.assertNull(methods);
            
            AccessLog acLog = config.getAccessLog();
            Assertions.assertNull(acLog);
            
            SecurityConstraint sc = config.getSecurityConstraint();
            Assertions.assertNull(sc);
            
            Assertions.assertEquals("routing-config.xml", config.getRoutingConfig());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }
    
    // @Test
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
}
