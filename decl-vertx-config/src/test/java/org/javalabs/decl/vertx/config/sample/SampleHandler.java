package org.javalabs.decl.vertx.config.sample;

import org.javalabs.decl.vertx.rest.HttpMethod;
import org.javalabs.decl.vertx.rest.Mapping;
import org.javalabs.decl.vertx.rest.ResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
@ResourceMapping(name = "sample", path = "/api/v1/sample")
public class SampleHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleHandler.class);
    
    @Mapping(uri = "/employees", method = HttpMethod.POST)
    public void create() {
        LOGGER.info("Created employee");
    }
    
    @Mapping(uri = "/employees", method = HttpMethod.GET)
    public void getAll() {
        LOGGER.info("Fetched all employees");
    }
    
    @Mapping(uri = "/employees/:id", method = HttpMethod.GET)
    public void get() {
        LOGGER.info("Fetched specific employee");
    }
    
    @Mapping(uri = "/employees/:id", method = HttpMethod.PUT)
    public void modify() {
        LOGGER.info("Modified specific employee");
    }
    
    @Mapping(uri = "/employees/:id", method = HttpMethod.DELETE)
    public void remove() {
        LOGGER.info("Removed specific employee");
    }
}
