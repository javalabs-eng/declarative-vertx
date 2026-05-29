package org.javalabs.decl.vertx.container;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.lang.reflect.Field;

import java.util.Set;
import org.javalabs.decl.container.Container;
import org.javalabs.decl.container.ContainerConfig;
import org.javalabs.decl.container.ContainerException;
import org.javalabs.decl.vertx.config.parser.WebConfigParser;
import org.javalabs.decl.vertx.jaxb.Verticle;
import org.javalabs.decl.vertx.jaxb.Verticles;
import org.javalabs.decl.vertx.jaxb.VertxWeb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that creates the vertx instance and deploys various verticles.
 * 
 * <p>
 * Once a vertx instance is started, it will create an event loop, where every incoming
 * request will be placed. Event Loop is an implementation of Reactor design pattern.
 * The goal of the event loop thread is to continuously check for new events that 
 * has arrived, and dispatch it to someone who knows how to handle it.
 * Thus, by using only one thread to consume all the events, we’re basically making
 * the best use of our hardware and underlying resources.
 * 
 * <p>
 * However, there are scenarios, where vertx can spawn multiple threads (internally)
 * to handle certain type of request. But the process is transparent to user. Because
 * there is only one thread to handle the incoming events, one has to ensure the
 * event loop thread is never blocked. Which means, calling {@link Thread#sleep(long)} , 
 * {@link Object#wait() } or lock acquisition must be avoided.
 * 
 * <p>
 * Typically all the {@link Verticle}s are deployed with the standard option. If
 * your verticle is supposed to perform some expensive in memory computing, or some
 * long running task, then it is best to mark the specific verticle as Worker verticle.
 * You can pass true to the method {@link DeploymentOptions#setThreadingModel(io.vertx.core.ThreadingModel)  } to
 * mark a verticle as Worker.
 * The communication between a normal verticle and a worker verticle happens via
 * event bus, which is aan in-memory queue. So if your verticle is trying to communicate
 * with other (worker) verticle, you should make use of the event bus, which can be
 * obtained by calling {@link Vertx#eventBus() } API.
 * 
 * @author Sudiptasish Chanda
 */
public abstract class VertxContainer extends VertxConfigSupport implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxContainer.class);
    
    private final WebConfigParser webParser = WebConfigParser.parser();
    
    private Vertx vertx;
    
    protected VertxContainer() { }
    
    @Override
    public void setup(ContainerConfig config) {
        // This is the order in which the vert.x configuration files will be read.
        // 1. Read the vertx-web.xml. This file contains the core parameters required to start a Vert.x instance.
        // 2. vertx-web.xml has reference to server.xml. This file contains the properties to start an embedded http server (within a verticle).
        // 3. server.xml contains reference to routing-config.xml. This file contain the Vert.x API route and corresponding handler methods.
        
        VertxWeb vConfig = webParser.read();
        // ConfigStorage.get().store(vConfig);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Read default vertx web configuration file");
            LOGGER.info("Configurations:\n" + Json.encodePrettily(vConfig));
        }
        
        // This API is the entry point for initializing and starting the vertx container.
        // It follows the default deployment strategy, see {@link #deploy() }.
        // If you have a different hierarchy, then have your own deploy API.
        init(vConfig);
        
        // Pre-Requisite check for deployment.
        preDeploy();
        
        // Now deploy the verticles listed in the vertx-web.xml
        deploy(vConfig.getVerticles());
        
        // Post deployment check.
        postDeploy();
    }
    
    /**
     * Create a Vertx instance by calling Vertx.vertx().
     * 
     * <p>
     * The Vertx instance creates a number of threads internally to handle the 
     * exchange of messages between verticles.
     *
     * The Vertx instance by itself doesn't do much except all the thread management,
     * creating an event bus etc. which are all communication and infrastructure tasks.
     * In order to get the application to do something useful, we need to deploy
     * one or more verticles (component) inside the Vertx instance.
     * 
     * @param vConfig
     */
    protected void init(VertxWeb vConfig) {
        VertxOptions options = setupOptions(vConfig);

        vertx = Vertx.vertx(options);
        vertx.exceptionHandler(new ContainerErrorHandler(this));
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created Vert.x Instance. Classloader: {}. Event Loop size: {}. "
                + "Worker Pool size: {}. Internal Blocking Pool Size: {}. "
                + "Block thread check interval (ms): {}. "
                , getClass().getClassLoader()
                , options.getEventLoopPoolSize()
                , options.getWorkerPoolSize()
                , options.getInternalBlockingPoolSize()
                , options.getBlockedThreadCheckInterval());
        }
    }

    /**
     * Pre-deployment of verticle operation.
     * 
     * <p>
     * A pre-deployment method refers to a set of actions or checks performed before deploying
     * software or applications into a production environment. These activities aim to ensure the
     * deployment process is smooth, prerequisites are met, and the deployed application will
     * function as expected. 
     * 
     * <p>
     * Overriding class must provide their own implementation.
     * This method can be leveraged for reading application specific configuration file,
     * initializing key store, etc.
     * 
     */
    protected void preDeploy() {
        // Empty body.
    }
    
    /**
     * Start the deployment.
     * 
     * The standard deployment follows the following algorithm:
     * It is expected that the config file will have an attribute verticles, that
     * will list down all the verticles to be deployed.
     * 
     * E.g.,
     * {
     *   ....
     *   ....
     * 
     *   "verticles": [
     *     {
     *       "name" : "",
     *       "class": ""
     *     },
     *     {
     *       "name" : "",
     *       "class": ""
     *     }
     *   ],
     *   ....
     *   ....
     * }
     * 
     * The <code>name</code> attribute is the meaningful name of the verticle.
     * The <code>class</code> attribute denotes the fully qualified class name of the verticle.
     * 
     * If no <code>verticles</code> attribute is found, then auto deployment will
     * be disabled. And user has to explicitly call the APIs to deploy any verticle.
     * 
     * @param configs
     */
    protected void deploy(Verticles configs) {
        if (configs == null || configs.getVerticle() == null || configs.getVerticle().isEmpty()) {
            LOGGER.warn("No verticle configuration found in vertx-web.xml");
            return;
        }
        for (Verticle config : configs.getVerticle()) {
            String verticleName = config.getName();
            String verticleClass = config.getClazz();
            
            deploy(verticleName, verticleClass, config);
        }
    }

    /**
     * Deploy a Verticle to the Vertx instance.
     *
     * @param verticleName      A meaningful name of the verticle.
     * @param verticleClass     The verticle class.
     * @param config            Configuration of this verticle.
     */
    protected void deploy(String verticleName
            , String verticleClass
            , Verticle config) {

        DeploymentOptions deployOptions = new DeploymentOptions();
        
        if (config.getDeployOptions() != null) {
            ThreadingModel model = null;
            Boolean worker = config.getDeployOptions().isWorker();
            
            String modelStr = config.getDeployOptions().getThreadingModel();
            if (modelStr == null) {
                if (worker) {
                    model = ThreadingModel.WORKER;
                }
                else {
                    model = ThreadingModel.EVENT_LOOP;
                }
            }
            else {
                model = Enum.valueOf(ThreadingModel.class, modelStr);
                if (model == ThreadingModel.WORKER) {
                    worker = Boolean.TRUE;
                }
                else if (model == ThreadingModel.EVENT_LOOP) {
                    worker = Boolean.FALSE;
                }
            }
            deployOptions
                    // .setWorker(worker)       // Removed in vert.x 5
                    .setThreadingModel(model)
                    // .setIsolationGroup(config.getDeployOptions().getIsolationGroup())
                    .setHa(config.getDeployOptions().isHa())
                    .setInstances(config.getDeployOptions().getInstances())
                    .setWorkerPoolName(config.getDeployOptions().getWorkerPoolName())
                    .setWorkerPoolSize(config.getDeployOptions().getWorkerPoolSize())
                    .setMaxWorkerExecuteTime(config.getDeployOptions().getMaxWorkerExecuteTime())
                    .setConfig(new JsonObject().put("config", config.getConfig()));     // Pass reference to the config file required by this verticle.
        }
        
        Future<String> future = vertx.deployVerticle(verticleClass, deployOptions);
        while (! future.isComplete()) {
            // Wait for the future to be complete
        }
        if (future.failed()) {
            throw new ContainerException(ContainerException.DEPLOYMENT_FAILURE, future.cause());
        }
        else {
            String deploymentId = future.result();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Deployment of verticle {} is successful. Deployment Id: {}"
                        , verticleName, deploymentId);
            }
        }

        // vertx.deployVerticle(verticleClass, deployOptions, deployHandler -> {
        //     if (deployHandler.failed()) {
        //         throw new ContainerException(ContainerException.DEPLOYMENT_FAILURE, deployHandler.cause());
        //     }
        //     else {
        //         String deploymentId = deployHandler.result();
        //         if (LOGGER.isInfoEnabled()) {
        //             LOGGER.info("Deployment of verticle {} is successful. Deployment Id: {}"
        //                     , verticleName, deploymentId);
        //         }
        //     }
        // });
    }

    /**
     * Post-deployment of verticle operation.
     * 
     * <p>
     * A post-deploy method refers to actions or processes that are carried out after software has
     * been deployed to a production environment. These methods aim to ensure the deployed software
     * is functioning correctly and address any issues that may arise after deployment.
     * 
     * <p>
     * If any additional task is needed by specific container post deployment of application, this
     * method should be leveraged.
     */
    protected void postDeploy() {
        // Empty body.
    }

    /**
     * Return the Vertx instance.
     * @return Vertx
     */
    public Vertx getVertx() {
        return vertx;
    }

    /**
     * Return a set of deploymentIds for all deployed verticles.
     * @return Set
     */
    public Set<String> verticleIds() {
        return getVertx().deploymentIDs();
    }

    @Override
    public void destroy() {
        try {
            Field closed = getVertx().getClass().getDeclaredField("closed");
            closed.setAccessible(true);
            Boolean val = closed.getBoolean(getVertx());

            if (! val) {
                // Closing the vert.x will automatically undeploy all the running verticle(s).
                
                // for (String id : verticleIds()) {
                //     getVertx().undeploy(id);
                //     if (LOGGER.isInfoEnabled()) {
                //         LOGGER.info("Undeployed verticle: {}", id);
                //     }
                // }
                // Finally, close the vertx instance.
                getVertx().close();
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Vertx Container Stopped");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error destroying vertx container", e);
        }
    }
}
