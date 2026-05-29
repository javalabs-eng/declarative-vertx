package org.javalabs.decl.workflow;

/**
 * Class to store the configuration of a workflow chain.
 *
 * @author Sudiptasish Chanda
 */
public class ChainConfig {
    
    private final String name;
    private Boolean async;
    private final Integer maxRetry;

    public ChainConfig(String name, Boolean async, Integer maxRetry) {
        this.name = name;
        this.async = async;
        this.maxRetry = maxRetry;
    }

    public String getName() {
        return name;
    }

    public Boolean getAsync() {
        return async;
    }

    public Integer getMaxRetry() {
        return maxRetry;
    }
}
