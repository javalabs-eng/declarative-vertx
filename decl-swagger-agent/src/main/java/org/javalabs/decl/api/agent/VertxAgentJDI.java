package org.javalabs.decl.api.agent;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class VertxAgentJDI extends AgentJDI {

    @Override
    public void debug(String host, String port) {
        try {
            // attach(host, port);
            String className = "";
            // entry(host, port, className);
            
            attach(host, port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void entry(String host, String port, String className) throws Exception {
        // Prepare connector, set class to debug & launch VM.
        int lineNumberToPutBreakpoint = 18;
		LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        
		Map<String, Connector.Argument> env = launchingConnector.defaultArguments();
        env.get("hostname").setValue(host);
        env.get("port").setValue(port);
        env.get("main").setValue(className);
        
		VirtualMachine vm = launchingConnector.launch(env);
        
        // Request VM to trigger event when HelloWorld class is prepared.
		ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
		classPrepareRequest.addClassFilter(className);
		classPrepareRequest.enable();
        
        EventSet eventSet = null;
        
        try {
            // Wait for 1 sec for the next event.
			while ((eventSet = vm.eventQueue().remove(1000)) != null) {
				for (Event event : eventSet) {
                    processEvent(vm, event, lineNumberToPutBreakpoint);
					vm.resume();
				}
			}
		}
        catch (VMDisconnectedException e) {
			System.out.println("VM is now disconnected.");
		}
        catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void processEvent(VirtualMachine vm, Event event, int lineNumberToPutBreakpoint) throws Exception {
        // If this is ClassPrepareEvent, then set breakpoint
        if (event instanceof ClassPrepareEvent) {
            ClassPrepareEvent evt = (ClassPrepareEvent) event;
            ClassType classType = (ClassType) evt.referenceType();

            Location location = classType.locationsOfLine(lineNumberToPutBreakpoint).get(0);
            BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
            bpReq.enable();
        }

        // If this is BreakpointEvent, then read & print variables.
        if (event instanceof BreakpointEvent) {
            // disable the breakpoint event
            event.request().disable();

            // Get values of all variables that are visible and print
            StackFrame stackFrame = ((BreakpointEvent) event).thread().frame(0);
            Map<LocalVariable, Value> visibleVariables = (Map<LocalVariable, Value>) stackFrame
                    .getValues(stackFrame.visibleVariables());

            System.out.println("Local Variables:");
            for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
                System.out.println("\n" + entry.getKey().name() + " = " + entry.getValue());
            }
        }
    }
    
    private VirtualMachine attach(String hostname, String port)
            throws IOException, IllegalConnectorArgumentsException
            , IncompatibleThreadStateException, InvalidTypeException
            , ClassNotLoadedException, InvocationException {
        
        //getSocketAttaching connector to connect to other JVM using Socket
        AttachingConnector connector = Bootstrap.virtualMachineManager().attachingConnectors()
                .stream().filter(p -> p.transport().name().contains("socket"))
                .findFirst().get();

        //set the arguments for the connector
        Map<String, Connector.Argument> arg = connector.defaultArguments();
        arg.get("hostname").setValue(hostname);
        arg.get("port").setValue(port);

        //connect to remote process by socket
        VirtualMachine vm = connector.attach(arg);
        
        // vm.eventQueue().re
        
        ThreadReference mainThread = null;
        ThreadReference eventLoopThread = null;
        ThreadReference acceptorThread = null;
        
        List<ThreadReference> threads = vm.allThreads();
        for (ThreadReference thread : threads) {
            if (thread.name() != null && thread.name().contains("main")) {
                mainThread = thread;
            }
            else if (thread.name() != null && thread.name().startsWith("vert.x-eventloop-thread")) {
                eventLoopThread = thread;
            }
            else if (thread.name() != null && thread.name().startsWith("vert.x-acceptor-thread")) {
                acceptorThread = thread;
            }
        }
        // List<ReferenceType> classes = vm.classesByName("io.vertx.core.http.HttpServer");
        List<ReferenceType> classes = vm.classesByName("io.vertx.core.http.impl.HttpServerImpl");
        ReferenceType clazz = classes.get(0);
        List<ObjectReference> list = clazz.instances(1);       // There will be a single instance of HttpServer
        ObjectReference httpServer = list.get(0);
        
        Field streamField = httpServer.referenceType().fieldByName("requestStream");
        Value val = httpServer.getValue(streamField);
        ObjectReference stream = (ObjectReference)val;

        Field handlerField = stream.referenceType().fieldByName("handler");
        val = stream.getValue(handlerField);
        ObjectReference router = (ObjectReference)val;
        
        Field stateField = router.referenceType().fieldByName("state");
        val = router.getValue(stateField);
        ObjectReference state = (ObjectReference)val;
        
        // TreeSet object.
        Field routesField = state.referenceType().fieldByName("routes");
        val = state.getValue(routesField);
        ObjectReference routes = (ObjectReference)val;
        
        // TreeMap object.
        Field mField = routes.referenceType().fieldByName("m");
        val = routes.getValue(mField);
        ObjectReference m = (ObjectReference)val;
        
        // TreeMap.Entry object.
        Field rootField = m.referenceType().fieldByName("root");
        val = m.getValue(rootField);
        ObjectReference root = (ObjectReference)val;
        
        Field kField = root.referenceType().fieldByName("key");
        val = root.getValue(kField);
        ObjectReference key = (ObjectReference)val;
        
        // RouteState object
        stateField = key.referenceType().fieldByName("state");
        val = key.getValue(stateField);
        state = (ObjectReference)val;
        
        Field pathField = state.referenceType().fieldByName("path");
        val = state.getValue(pathField);
        ObjectReference path = (ObjectReference)val;
        
        System.out.println(val);
                
        List<Method> methods = classes.get(0).methodsByName("requestHandler", "()Lio/vertx/core/Handler;");
        
        
        
        // if (! list.isEmpty()) {
        //     ObjectReference objRef = list.get(0);
        //     ThreadReference threadRef = objRef.owningThread();
        //     
        //     eventLoopThread.suspend();
        //     val = objRef.invokeMethod(eventLoopThread, methods.get(0), Collections.emptyList(), 0);
        //     System.out.println(val);
        // }
        return vm;
    }
}
