package org.javalabs.decl.vertx.container;

import io.vertx.core.Handler;
import org.javalabs.decl.container.ContainerException;
import org.javalabs.decl.container.diag.VMDumpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic exception handler for container specific error.
 * 
 * <p>
 * This error handler will be used whenever there is some uncaught exception is
 * thrown by the container. The {@link #handle(java.lang.Throwable) } API will be
 * invoked, which will decide the next course of action.
 *
 * @author Sudiptasish Chanda
 */
public class ContainerErrorHandler implements Handler<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerErrorHandler.class);
    
    private final VertxContainer container;

    public ContainerErrorHandler(VertxContainer container) {
        this.container = container;
    }

    @Override
    public void handle(Throwable e) {
        LOGGER.error("Invoked ContainerErrorHandler. Error Message:", e);
        
        // For OutOfMemoryError, try to get the used memory size and dump it in the log.
        // It has been observed, that once heap dump request is raised via ecp tickr tool,
        // the generated dump does not contain the memory snapshot at the time, hence
        // we are trying to generate it immediately after the OOM error.
        if (e instanceof OutOfMemoryError
                || (e.getCause() != null && e.getCause() instanceof OutOfMemoryError)) {
            
            VMDumpUtil.dumpHeap("./heap_dump_" + System.currentTimeMillis() + ".hprof", true);
            
            // Do not invoke the shutdown API, as that might cause the termination of
            // the current instance/POD, in which case we will miss the heap dump.
        }
        else if (e instanceof ContainerException) {
            ContainerException error = (ContainerException)e;
            int code = error.getErrorCode();
            String msg = error.getErrorMessage();
            Throwable cause = error.getCause();
            
            if (code == ContainerException.DEPLOYMENT_FAILURE) {
                // For deployment failure, we are going to shutdown the container.
                LOGGER.error("Container startup error", cause);
                handleDeploymentFailure();
            }
        }
        else {
            // Unexpected error
            // TODO (Sudiptasish Chanda): Send email to support group.
            LOGGER.error("Unexpected Error : " + e.getMessage());
            
            // handleDeploymentFailure();
        }
    }
    
    /**
     * Invoke this method when there is a deployment failure.
     */
    private void handleDeploymentFailure() {
        container.destroy();
    }
}
