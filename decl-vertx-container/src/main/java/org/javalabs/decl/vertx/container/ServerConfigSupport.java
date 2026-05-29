package org.javalabs.decl.vertx.container;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.TrustOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.vertx.jaxb.WebServerConfig;

/**
 * A server that supports configuration through external xml file.
 *
 * @author Sudiptasish Chanda
 */
public abstract class ServerConfigSupport {
    
    protected ServerConfigSupport() {}
    
    /**
     * Setup the server configuration.
     * 
     * @param config    External server configuration.
     * @return HttpServerOptions
     */
    protected HttpServerOptions setupOptions(WebServerConfig config) {
        HttpServerOptions options = new HttpServerOptions();
        JksOptions jks = null;
        JksOptions trust = null;
        
        if (config.getKeystoreConfig() != null) {
            if (config.getKeystoreConfig().getStoreName() != null) {
                jks = new JksOptions()
                    .setPath(config.getKeystoreConfig().getStoreName())
                    .setPassword(config.getKeystoreConfig().getStorePassword());
            }
        }
        if (config.getTruststoreConfig() != null) {
            if (config.getTruststoreConfig().getStoreName() != null) {
                trust = new JksOptions()
                    .setPath(config.getTruststoreConfig().getStoreName())
                    .setPassword(config.getTruststoreConfig().getStorePassword());
            }
        }
        options.setKeyCertOptions(jks).setTrustOptions(trust);
        
        // Set the network options.
        if (config.getNetworkOpts() != null) {
            options.setSendBufferSize(config.getNetworkOpts().getSendBufferSize())
                    .setReceiveBufferSize(config.getNetworkOpts().getReceiveBufferSize())
                    .setReuseAddress(config.getNetworkOpts().isReuseAddress())
                    .setReusePort(config.getNetworkOpts().isReusePort())
                    .setTrafficClass(config.getNetworkOpts().getTrafficClass())
                    .setLogActivity(config.getNetworkOpts().isLogActivity());
        }
        
        // Set the tcp options.
        if (config.getTcpOpts() != null) {
            options.setTcpNoDelay(config.getTcpOpts().isTcpNoDelay())
                    .setTcpKeepAlive(config.getTcpOpts().isTcpKeepAlive())
                    .setSoLinger(config.getTcpOpts().getSoLinger())
                    .setIdleTimeout(config.getTcpOpts().getIdleTimeout())
                    .setReadIdleTimeout(config.getTcpOpts().getReadIdleTimeout())
                    .setWriteIdleTimeout(config.getTcpOpts().getWriteIdleTimeout())
                    .setSsl(config.getTcpOpts().isSsl())
                    .setTcpFastOpen(config.getTcpOpts().isTcpFastOpen())
                    .setTcpCork(config.getTcpOpts().isTcpCork())
                    .setTcpQuickAck(config.getTcpOpts().isTcpQuickAck())
                    .setTcpUserTimeout(config.getTcpOpts().getTcpUserTimeout());
            
            if (config.getTcpOpts().getSslOpts() != null) {
                options.getSslOptions().setSslHandshakeTimeout(config.getTcpOpts().getSslOpts().getSslHandshakeTimeout());
                
                if (config.getTcpOpts().getSslOpts().getKeyCertOptions() != null) {
                    KeyCertOptions keyCert = ObjectCreator.create(config.getTcpOpts().getSslOpts().getKeyCertOptions());
                    options.getSslOptions().setKeyCertOptions(keyCert);
                }
                if (config.getTcpOpts().getSslOpts().getTrustOptions()!= null) {
                    TrustOptions trustCert = ObjectCreator.create(config.getTcpOpts().getSslOpts().getTrustOptions());
                    options.getSslOptions().setTrustOptions(trustCert);
                }
                
                options.getSslOptions().setUseAlpn(config.getTcpOpts().getSslOpts().isUseAlpn());
                
                if (config.getTcpOpts().getSslOpts().getEnabledSecureTransportProtocols() != null) {
                    Set<String> set = Set.of(config.getTcpOpts().getSslOpts().getEnabledSecureTransportProtocols().split(","));
                    options.getSslOptions().setEnabledSecureTransportProtocols(set);
                }
            }
        }
        
        // Set the net server options.
        if (config.getServerOpts() != null) {
            options.setPort(config.getServerOpts().getPort());
            options.setHost(config.getServerOpts().getHost());
            options.setAcceptBacklog(config.getServerOpts().getAcceptBacklog());
            options.setSni(config.getServerOpts().isSni());
            options.setUseProxyProtocol(config.getServerOpts().isUseProxyProtocol());
            options.setProxyProtocolTimeout(config.getServerOpts().getProxyProtocolTimeout());
            options.setRegisterWriteHandler(config.getServerOpts().isRegisterWriteHandler());
            
            if (config.getServerOpts().getClientAuth() != null) {
                options.setClientAuth(Enum.valueOf(ClientAuth.class, config.getServerOpts().getClientAuth()));
            }
            
        }
        
        // Set the http options.
        if (config.getHttpOpts() != null) {
            options.setCompressionSupported(config.getHttpOpts().isCompressionSupported());
            options.setCompressionLevel(config.getHttpOpts().getCompressionLevel());
            options.setMaxWebSocketFrameSize(config.getHttpOpts().getMaxWebSocketFrameSize());
            options.setMaxWebSocketMessageSize(config.getHttpOpts().getMaxWebSocketMessageSize());
            options.setHandle100ContinueAutomatically(config.getHttpOpts().isHandle100ContinueAutomatically());
            options.setMaxChunkSize(config.getHttpOpts().getMaxChunkSize());
            options.setMaxInitialLineLength(config.getHttpOpts().getMaxInitialLineLength());
            options.setMaxHeaderSize(config.getHttpOpts().getMaxHeaderSize());
            options.setMaxFormAttributeSize(config.getHttpOpts().getMaxFormAttributeSize());
            options.setHttp2ClearTextEnabled(config.getHttpOpts().isHttp2ClearTextEnabled());
            options.setHttp2ConnectionWindowSize(config.getHttpOpts().getHttp2ConnectionWindowSize());
            options.setDecompressionSupported(config.getHttpOpts().isDecompressionSupported());
            options.setAcceptUnmaskedFrames(config.getHttpOpts().isAcceptUnmaskedFrames());
            options.setDecoderInitialBufferSize(config.getHttpOpts().getDecoderInitialBufferSize());
            options.setPerFrameWebSocketCompressionSupported(config.getHttpOpts().isPerFrameWebSocketCompressionSupported());
            options.setPerMessageWebSocketCompressionSupported(config.getHttpOpts().isPerMessageWebSocketCompressionSupported());
            options.setWebSocketCompressionLevel(config.getHttpOpts().getWebSocketCompressionLevel());
            options.setWebSocketPreferredClientNoContext(config.getHttpOpts().isWebSocketPreferredClientNoContext());
            options.setWebSocketAllowServerNoContext(config.getHttpOpts().isWebSocketAllowServerNoContext());
            options.setWebSocketClosingTimeout(config.getHttpOpts().getWebSocketClosingTimeout());
            options.setRegisterWebSocketWriteHandlers(config.getHttpOpts().isRegisterWebSocketWriteHandlers());
            options.setHttp2RstFloodMaxRstFramePerWindow(config.getHttpOpts().getHttp2RstFloodMaxRstFramePerWindow());
            options.setHttp2RstFloodWindowDuration(config.getHttpOpts().getHttp2RstFloodWindowDuration());
            
            if (config.getHttpOpts().getInitialSettings() != null) {
                Http2Settings http2 = new Http2Settings();
                http2.setHeaderTableSize(config.getHttpOpts().getInitialSettings().getHeaderTableSize());
                http2.setPushEnabled(config.getHttpOpts().getInitialSettings().isPushEnabled());
                http2.setMaxConcurrentStreams(config.getHttpOpts().getInitialSettings().getMaxConcurrentStreams());
                http2.setInitialWindowSize(config.getHttpOpts().getInitialSettings().getInitialWindowSize());
                http2.setMaxFrameSize(config.getHttpOpts().getInitialSettings().getMaxFrameSize());
                http2.setMaxHeaderListSize(config.getHttpOpts().getInitialSettings().getMaxHeaderListSize());
                
                options.setInitialSettings(http2);
            }
            if (config.getHttpOpts().getAlpnVersions() != null) {
                String[] arr = config.getHttpOpts().getAlpnVersions().split(",");
                if (arr.length > 0) {
                    List<HttpVersion> versions = new ArrayList<>();
                    for (String s : arr) {
                        versions.add(Enum.valueOf(HttpVersion.class, s));
                    }
                    options.setAlpnVersions(versions);
                }
            }
        }
        return options;
    }
}
