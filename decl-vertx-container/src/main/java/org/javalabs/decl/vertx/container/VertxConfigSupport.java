package org.javalabs.decl.vertx.container;

import io.vertx.core.VertxOptions;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.VertxTracerFactory;
import io.vertx.core.tracing.TracingOptions;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.javalabs.decl.util.ObjectCreator;
import org.javalabs.decl.vertx.jaxb.ContextParams;
import org.javalabs.decl.vertx.jaxb.VertxWeb;

/**
 * Abstract class for setting and managing the deployment configuration for vert.x.
 *
 * @author Sudiptasish Chanda
 */
public abstract class VertxConfigSupport {
    
    protected VertxConfigSupport() {}
    
    /**
     * Setup the server configuration.
     * 
     * @param vConfig    External vert.x web configuration.
     * @return VertxOptions
     */
    protected VertxOptions setupOptions(VertxWeb vConfig) {
        ContextParams params = vConfig.getContextParams();
        
        VertxOptions options = new VertxOptions();
        options.setEventLoopPoolSize(params.getEventLoopPoolSize())
                .setWorkerPoolSize(params.getWorkerPoolSize())
                .setBlockedThreadCheckInterval(params.getBlockedThreadCheckInterval())
                .setBlockedThreadCheckIntervalUnit(TimeUnit.MILLISECONDS)
                .setMaxEventLoopExecuteTime(params.getMaxEventLoopExecuteTime())
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
                .setMaxWorkerExecuteTime(params.getMaxWorkerExecuteTime())
                .setMaxWorkerExecuteTimeUnit(TimeUnit.MILLISECONDS)
                .setInternalBlockingPoolSize(params.getInternalBlockingPoolSize())
                .setHAEnabled(params.isHaEnabled())
                .setQuorumSize(params.getQuorumSize())
                .setHAGroup(params.getHaGroup())
                .setWarningExceptionTime(params.getWarningExceptionTime())
                .setWarningExceptionTimeUnit(TimeUnit.MILLISECONDS)
                .setPreferNativeTransport(params.isPreferNativeTransport())
                .setDisableTCCL(params.isDisableTccl())
                .setUseDaemonThread(params.isUseDaemonThread());
        
        // Set vertx file system options.
        if (params.getFileSystemOptions() != null) {
            FileSystemOptions fileOptions = new FileSystemOptions();
            fileOptions.setFileCachingEnabled(params.getFileSystemOptions().isFileCachingEnabled())
                    .setClassPathResolvingEnabled(params.getFileSystemOptions().isClassPathResolvingEnabled());
                    if (params.getFileSystemOptions().getFileCacheDir() != null) {
                        fileOptions.setFileCacheDir(params.getFileSystemOptions().getFileCacheDir());
                    }
            
            options.setFileSystemOptions(fileOptions);
        }
        
        // Set vertx metrics options.
        if (params.getMetricsOptions() != null && params.getMetricsOptions().isEnabled()) {
            MetricsOptions metricsOptions = new MetricsOptions();
            metricsOptions.setEnabled(params.getMetricsOptions().isEnabled());
            
            if (params.getMetricsOptions().getFactory() != null
                    && ! "".equals(params.getMetricsOptions().getFactory())) {
                
                VertxMetricsFactory factory = ObjectCreator.create(params.getMetricsOptions().getFactory());
                // metricsOptions.setFactory(factory);      Removed in vert.x 5
            }
            options.setMetricsOptions(metricsOptions);
        }
        
        // Set vertx tracing options.
        if (params.getTracingOptions() != null) {
            TracingOptions tracingOptions = new TracingOptions();
            
            if (params.getTracingOptions().getFactory() != null
                    && ! "".equals(params.getTracingOptions().getFactory())) {
                
                VertxTracerFactory factory = ObjectCreator.create(params.getMetricsOptions().getFactory());
                // tracingOptions.setFactory(factory);      Removed in vert.x 5
            }
            options.setTracingOptions(tracingOptions);
        }
        
        // Set vertx event bus options.
        if (params.getEventBusOptions() != null) {
            EventBusOptions busOptions = new EventBusOptions();
            busOptions.setClusterPublicHost(params.getEventBusOptions().getClusterPublicHost());
            busOptions.setClusterPublicPort(params.getEventBusOptions().getClusterPublicPort());
            busOptions.setClusterPingInterval(params.getEventBusOptions().getClusterPingInterval());
            busOptions.setClusterPingReplyInterval(params.getEventBusOptions().getClusterPingReplyInterval());
            busOptions.setAcceptBacklog(params.getEventBusOptions().getAcceptBacklog());
            busOptions.setReconnectInterval(params.getEventBusOptions().getReconnectInterval());
            busOptions.setReconnectAttempts(params.getEventBusOptions().getReconnectAttempts());
            busOptions.setConnectTimeout(params.getEventBusOptions().getConnectTimeout());
            busOptions.setTrustAll(params.getEventBusOptions().isTrustAll());
            busOptions.setLogActivity(params.getEventBusOptions().isLogActivity());
            
            options.setEventBusOptions(busOptions);
        }
        
        // Set vertx address resolver options.
        if (params.getAddressResolverOptions() != null) {
            AddressResolverOptions addressOptions = new AddressResolverOptions();
            addressOptions.setHostsPath(params.getAddressResolverOptions().getHostsPath());
            addressOptions.setHostsRefreshPeriod(params.getAddressResolverOptions().getHostsRefreshPeriod());
            addressOptions.setOptResourceEnabled(params.getAddressResolverOptions().isOptResourceEnabled());
            addressOptions.setCacheMinTimeToLive(params.getAddressResolverOptions().getCacheMinTimeToLive());
            addressOptions.setCacheMaxTimeToLive(params.getAddressResolverOptions().getCacheMaxTimeToLive());
            addressOptions.setCacheNegativeTimeToLive(params.getAddressResolverOptions().getCacheNegativeTimeToLive());
            addressOptions.setQueryTimeout(params.getAddressResolverOptions().getQueryTimeout());
            addressOptions.setMaxQueries(params.getAddressResolverOptions().getMaxQueries());
            // addressOptions.setNdots(params.getAddressResolverOptions().getNdots());
            addressOptions.setRotateServers(params.getAddressResolverOptions().isRotateServers());
            addressOptions.setRoundRobinInetAddress(params.getAddressResolverOptions().isRoundRobinInetAddress());
            
            if (params.getAddressResolverOptions().getServers() != null) {
                addressOptions.setServers(Arrays.asList(params.getAddressResolverOptions().getServers().split(",")));
            }
            if (params.getAddressResolverOptions().getSearchDomains() != null) {
                addressOptions.setSearchDomains(Arrays.asList(params.getAddressResolverOptions().getSearchDomains().split(",")));
            }
            options.setAddressResolverOptions(addressOptions);
        }
        
        // Custom metrics options.
        if (params.getMetricClass() != null) {
            // E.g., DropwizardMetricsOptions
            MetricsOptions metricsOptions = ObjectCreator.create(params.getMetricClass());
            metricsOptions.setEnabled(Boolean.TRUE);
            
            options.setMetricsOptions(metricsOptions);
        }
        return options;
    }
}
