package org.javalabs.decl.api.project;

import java.io.IOException;
import org.javalabs.decl.util.ConsoleWriter;
import org.javalabs.decl.workflow.EngineArgs;
import org.javalabs.decl.workflow.WorkflowEngine;
import org.javalabs.decl.workflow.WorkflowError;

/**
 * A java project template.
 * 
 * <p>
 * Use this template when you intend to create a java based project. All java projects will be maven based.
 * Maven is a powerful project management tool that is based on POM (project object model). It is used for
 * project build, dependency, and documentation.
 *
 * @author Sudiptasish Chanda
 */
public class JavaProjectTemplate implements ProjectTemplate {
    
    private final TechStack stack;
    private final BuildTool tool;
    
    public JavaProjectTemplate(TechStack stack, BuildTool tool) {
        this.stack = stack;
        this.tool = tool;
    }
    
    @Override
    public Boolean setup(Project project) throws IOException {
        WorkflowError error = new WorkflowError();
        
        EngineArgs args = new EngineArgs();
        args.setWorkflowChain(chain(project));
        args.addBaggage("project.work", project);
        args.addBaggage("notify.error", Boolean.TRUE);
        args.addBaggage("workflow.error", error);

        // Create the engine args and kickstart the workflow.
        WorkflowEngine.getDefault().start(args);
        
        if (error.getError() != null) {
            ConsoleWriter.println(error.getError().getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    private String chain(Project project) {
        if (project.e2e()) {
            if (project.fromDb()) {
                return "java_project_e2e_db";
            }
            return "java_project_e2e";
        }
        return "java_project";
        
        /*if (stack == TechStack.VERTX) {
            if (tool == BuildTool.MAVEN) {
                return "vertx_maven";
            }
            else {
                return "vertx_gradle";
            }
        }
        else if (stack == TechStack.SPRING) {
            if (tool == BuildTool.MAVEN) {
                return "spring.maven";
            }
            else {
                return "spring.gradle";
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported tech stack [" + stack + "] and build tool [" + tool + "]");
        }*/
    }
}
