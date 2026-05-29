package org.javalabs.decl.workflow;

/**
 * A Chain represents a pre-configured list of {@link Command}s that will be 
 * executed in order to perform processing on a specified {@link Context}.
 * 
 * <p>
 * Each included {@link Command} will be executed in turn, until either one of 
 * them returns <code>false</code>, or the end of the chain has been reached. 
 * The {@link Chain} itself will return the return value of the last {@link Command}
 * that was executed.
 *
 * @author Sudiptasish Chanda
 */

public interface Chain {
    
    /**
     * Return the name of the workflow chain.
     * @return String
     */
    String name();
    
    /**
     * Initialize the workflow chain.
     * This method may initialize the chain with the default set of commands.
     */
    void init();
    
    /**
     * Check if the current chain is running.
     * @return boolean
     */
    boolean active();
    
    /**
     * Add a command to the current workflow chain.
     * @param command   Command to be added.
     */
    void add(Command command);
    
    /**
     * Return the set of commands that are part of this chain.
     * @return String[]
     */
    Command[] commands();
    
    /**
     * Fetch the specific command by it's name.
     * 
     * @param name  Name of the command to be queried
     * @return Command.
     */
    Command command(String name);
    
    /**
     * Start a workflow chain.
     * @param ctx   Context object that individual command will use to pass
     *              on the contextual info that they want to pass on to the
     *              next command in the chain.
     */
    void start(Context ctx);
    
    /**
     * Terminate the workflow chain.
     * 
     * Terminating a chain implies disrupts the command execution flow and stop all
     * the current running commands. This API will make a best effort to stop the
     * chain, however, if any of the commands is making some network call, or waiting
     * for some network response, it may not be terminated immediately.
     */
    void terminate();
}
