package org.javalabs.decl.workflow;

/**
 * A class to capture any error arising from the workflow execution.
 * 
 * <p>
 * This object will accessible only if caller set the baggage property <code>notify.error=true</code>.
 * The object can be fetched from the {@link Context} by the key <code>workflow.error</code>.
 *
 * @author Sudiptasish Chanda
 */
public class WorkflowError {
    
    private Throwable error;
    
    public WorkflowError() {}
    
    public WorkflowError(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
