package org.javalabs.decl.api.project;

import java.io.IOException;

/**
 * Abstract class for rest project template.
 * 
 * <p>
 * A project can be created in different language and in different tech stack. To give example, engineer can
 * create a java project, a go-lang project, node.js project, etc. And in java project one can choose different
 * tech stack, i.e., vert.x, spring boot, jax-rs for creating rest based project.
 * 
 * <p>
 * If you want to create a project, use the appropriate template to automatically generate the source code with
 * appropriate directory structure for the project. It will also create some run time (dynamic) variable in that
 * source code. 
 *
 * @author Sudiptasish Chanda
 */
public interface ProjectTemplate {
    
    /**
     * Return the appropriate project template.
     * 
     * @param project   The project object.
     * 
     * @return ProjectTemplate
     */
    static ProjectTemplate get(Project project) {
        if (project.platform() == Platform.JAVA) {
            return new JavaProjectTemplate(project.stack(), project.buildTool());
        }
        throw new IllegalArgumentException("Only Java platform is supported");
    }
    
    /**
     * Setup the project.
     * 
     * <p>
     * The appropriate project template will be invoked on-the-fly to create project workspace and
     * directory hierarchies.
     * 
     * @param project       Encapsulates the project details.
     * @return Boolean      Indicates whether the setup is a success or not.
     * @throws IOException  If any error occurs (disk permission issue, etc) while setting up the project.
     */
    Boolean setup(Project project) throws IOException;
}
