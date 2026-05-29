package org.javalabs.decl.api.cust;

import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.gen.JavaClass;

/**
 * Class to inject customized code in specific methods.
 * 
 * <p>
 * Customization layer is defined at method level. It helps engineer to inject additional code/business logic
 * into the code. However, it is not mandatory for a method to have a customization layer.
 *
 * @author Sudiptasish Chanda
 */
public interface Customization {
    
    /**
     * Called while starting a method.
     * 
     * @param project 
     * @param model 
     * @return  Result
     */
    Result handlerEntry(Project project, JavaClass model);
    
    /**
     * Called while starting a method.
     * 
     * @param project 
     * @param model 
     * @return  Result
     */
    Result boEntry(Project project, JavaClass model);
    
    /**
     * Called while starting a method.
     * 
     * @param project 
     * @param model 
     * @return  Result
     */
    Result entry(Project project, JavaClass model);
    
    /**
     * Called while exiting a method.
     * 
     * @param project 
     * @return  Result
     */
    Result exit(Project project);
}
