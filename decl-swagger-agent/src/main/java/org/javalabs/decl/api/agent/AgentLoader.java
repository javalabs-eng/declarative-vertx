package org.javalabs.decl.api.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;

/**
 * Swagger agent loader.
 *
 * @author Sudiptasish Chanda
 */
public class AgentLoader {
    
    public static void main(String[] args) {
        String processId = null;
        String agentPath = null;
        String host = "localhost";
        String port = null;
        Boolean jdi = Boolean.FALSE;
        
        StringBuilder option = new StringBuilder(128);
        
        for (int i = 0; i < args.length; i ++) {
            if (args[i].equals("-jdi")) {
                jdi = Boolean.TRUE;
            }
            else if (args[i].equals("-a")) {
                agentPath = args[++ i];
            }
            else if (args[i].equals("-p")) {
                processId = args[++ i];
            }
            else if (args[i].equals("-h")) {
                host = args[++ i];
            }
            else if (args[i].equals("-r")) {
                port = args[++ i];
            }
            else {
                option.append(args[i]).append(" ");
            }
        }
        try {
            if (System.getProperties().containsKey("pause")) {
                Thread.sleep(5000);
            }
            if (jdi) {
                if (host == null || port == null) {
                    throw new RuntimeException("Must provide process both host and port");
                }
                new VertxAgentJDI().debug(host, port);
            }
            else {
                if (processId == null) {
                    throw new RuntimeException("Must provide process id. E.g., -p 7363");
                }
                if (agentPath == null) {
                    throw new RuntimeException("Must provide agent lib. E.g., -a /path/to/java_agent.jar");
                }
                attach(processId, agentPath, option.toString());
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Attach to the remote process as identified by this processId and load the agent.
     * If attach is not supported by the underlying OS implementation, then an
     * exception will be thrown and instrumentation will be disabled.
     * 
     * @param processId
     * @param agentLibrary
     * @param option
     * 
     * @throws AttachNotSupportedException
     * @throws IOException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    private static void attach(String processId, String agentLibrary, String option)
            throws AttachNotSupportedException
            , IOException
            , AgentLoadException
            , AgentInitializationException {
        
        System.out.println(String.format("Uploading agent [%s] to remote process [%s]. Options: %s"
                , agentLibrary, processId, option));
        
        VirtualMachine vm = VirtualMachine.attach(processId);   // process id        
        vm.loadAgent(agentLibrary, option);  // Path to agent library
        vm.detach();
        
        System.out.println(String.format("Detached from remote process [%s]", processId));
    }
}
