package org.javalabs.decl.api.project;

import org.javalabs.decl.api.cust.Customization;
import org.javalabs.decl.api.cust.DeleteMethodCustomization;
import org.javalabs.decl.api.cust.GetAllMethodCustomization;
import org.javalabs.decl.api.cust.GetMethodCustomization;
import org.javalabs.decl.api.cust.PostMethodCustomization;
import org.javalabs.decl.api.cust.PutMethodCustomization;
import org.javalabs.decl.api.cust.Result;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.javalabs.decl.api.cust.BatchMethodCustomization;
import org.javalabs.decl.gen.JavaClass;

/**
 * Vert.x api helper class.
 *
 * @author Sudiptasish Chanda
 */
public class ApiHelper {
    
    private final List<Customization> customizers = new ArrayList<>();
    
    public ApiHelper() {
        customizers.add(new PostMethodCustomization());
        customizers.add(new BatchMethodCustomization());
        customizers.add(new PutMethodCustomization());
        customizers.add(new GetAllMethodCustomization());
        customizers.add(new GetMethodCustomization());
        customizers.add(new DeleteMethodCustomization());
    }
    
    /**
     * Analyze the handler class content and invoke the appropriate customizer.
     * 
     * @param project   Active project object.
     * @param content   Raw content of the handler file.
     * @return String   Modified content.
     */
    public String analyze(Project project, String content, JavaClass model) {
        try {
            String tmp = content;
            tmp = tmp.replace("{HANDLER_PACKAGE}", project.handlerPkg());
            tmp = tmp.replace("{MODEL_PACKAGE}", project.modelPkg());
            tmp = tmp.replace("{LOWER(RESOURCE)}", project.inputResource().resource().toLowerCase());
            tmp = tmp.replace("{RESOURCE}", project.inputResource().resource());
            
            for (Customization cust : customizers) {
                Method[] methods = methods(cust);

                for (Method method : methods) {
                    String exp = "{" + cust.getClass().getName() + "." + method.getName() + "(${Project}, ${JavaClass})" + "}";
                    if (content.indexOf(exp) > 0) {
                        Result res = (Result)method.invoke(cust, new Object[] {project, model});
                        tmp = tmp.replace(exp, res.toString());
                    }
                }
            }
            return tmp;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Method[] methods(Customization cust) {
        return cust.getClass().getDeclaredMethods();
    }
}
