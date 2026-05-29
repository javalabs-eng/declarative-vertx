package org.javalabs.decl.api.project;

import org.javalabs.decl.gen.JavaClass;
import org.javalabs.decl.util.CharUtil;

/**
 *
 * @author Sudiptasish Chanda
 */
public class CommonHelperSupport {
    
    /**
     * Analyze the handler class content and invoke the appropriate customizer.
     * 
     * @param project
     * @param content   Raw content of the handler file.
     * @param pkgName   package name of the java class.
     * @param jClass    Model class definition
     * @return String   Modified content.
     */
    public String analyze(Project project, String pkgName, String content, JavaClass jClass) {
        try {
            String tmp = content;
            tmp = tmp.replace("{PU_NAME}", project.name().toLowerCase() + "-" + "pu");
            tmp = tmp.replace("{AUTH_PACKAGE}", project.authPkg());
            tmp = tmp.replace("{BO_PACKAGE}", project.boPkg());
            tmp = tmp.replace("{CONFIG_PACKAGE}", project.configPkg());
            tmp = tmp.replace("{DAO_PACKAGE}", project.daoPkg());
            tmp = tmp.replace("{MODEL_PACKAGE}", project.modelPkg());
            tmp = tmp.replace("{UTIL_PACKAGE}", project.utilPkg());
            tmp = tmp.replace("{PACKAGE}", pkgName);
            
            if (jClass != null) {
                tmp = tmp.replace("{TABLE}", jClass.table());
                tmp = tmp.replace("{CAMEL(RESOURCE)}", CharUtil.lowerFirst(jClass.name()));
                tmp = tmp.replace("{RESOURCE}", jClass.name());
                tmp = tmp.replace("{ID_DATA_TYPE}", jClass.idDataType().substring(jClass.idDataType().lastIndexOf(".") + 1));
            }
            return tmp;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
