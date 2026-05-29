package org.javalabs.decl.workflow;

import java.util.concurrent.Future;

/**
 * A command represents a unit of work to be performed, whose purpose is to examine
 * and/or modify the state of a {@link Context}.
 * 
 * <p>
 * Multiple commands are assembled into a {@link Chain}, which allows them to either
 * complete the required processing or delegate further processing to the next 
 * {@link Command} in the {@link Chain}.
 * 
 * Commands are loosely coupled. A command either succeeds or fails. A command
 * has the {@link #execute(org.javalabs.decl.workflow.Context) } method that will be
 * called by the workflow chain once it is activated. If the command execution is
 * successful, then it will return <code>true</code> indicating the next command
 * to initiate the next processing step. If any of the command execution fails, the
 * chain will invoke the {@link #backtrack(org.javalabs.workflow.Context) } API,
 * which will start rollbacking the work already done by the previous set of command(s).
 * The backtracking feature can be disabled via a flag. Because it may be possible
 * that some command has done persistence to some storage or db, which cannot be
 * rollbacked. Hence by default the backtracing will be disabled.
 * 
 * <p>
 * A {@link Chain} does not handler exceptions originating from a command.
 * It is therefore recommended that the individual command must do their exception
 * handling. Returning any {@link RuntimeException} may have an impact on the
 * current workflow execution.
 *
 * @author Sudiptasish Chanda
 */
public interface Command {
    
    boolean CONTINUE = true;
    
    boolean FAILURE = false;
    
    /**
     * Return the name of the command.
     * @return String
     */
    String name();
    
    /**
     * Entry point of executing a command.
     * 
     * <p>
     * A command execution will either succeed or fail. Any exception arising from
     * the command execution will be caught by the command and a false value will
     * be returned, indicating a failure.
     * 
     * <p>
     * This is a Future object based command. The return type of this method is a Future. If the
     * command is executed in a sync fashion then the result fo the execution will be available
     * immediately.
     * 
     * @param ctx   The context object holding the state from th eprevious
     *              command of the chain.
     * @return Future
     */
    Future<?> execute(Context ctx);
    
    /**
     * The API that will be called sequentially by the chain, whenever a command
     * in the execution chain will fail.
     * 
     * @param ctx    The workflow context.
     */
    void backtrack(Context ctx);
}
