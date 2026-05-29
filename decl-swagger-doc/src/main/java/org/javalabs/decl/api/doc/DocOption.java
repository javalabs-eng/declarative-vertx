package org.javalabs.decl.api.doc;

import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public class DocOption {
    
    // /path/to/routing-config.xml
    private String configFile;
    
    private List<String> methods;
    private String modelLib;
    private String resource;
    private String outFile;
    private Integer verbose = 2;
    
    // Package name where to find the rest handler/controller class.
    private String[] packages;
    
    // Raw content of the routing-config.xml file.
    // If it's value is present then the configFile won't be read again.
    private String cfgContent;
    
    public DocOption() {}

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public String getModelLib() {
        return modelLib;
    }

    public void setModelLib(String modelLib) {
        this.modelLib = modelLib;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public Integer getVerbose() {
        return verbose;
    }

    public void setVerbose(Integer verbose) {
        this.verbose = verbose;
    }

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }
    
    public String getCfgContent() {
        return cfgContent;
    }

    public void setCfgContent(String cfgContent) {
        this.cfgContent = cfgContent;
    }
}
