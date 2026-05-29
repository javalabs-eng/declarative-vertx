package org.javalabs.decl.api.doc;

import org.javalabs.decl.api.doc.SwaggerDoc;
import org.javalabs.decl.api.doc.DocOption;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class SwaggerDocTest {
    
    // @Test
    public void testGenerate() throws Exception {
        // String file = "/Users/Sudiptasish Chanda/Projects/example-rest/src/main/resources/routing-config.xml";
        DocOption docOpt = new DocOption();
        docOpt.setConfigFile("routing-config.xml");
        // docOpt.setMethods(Arrays.asList("get", "post", "put", "delete"));
        docOpt.setOutFile("/Users/Sudiptasish Chanda/Projects/openapi.yaml");
        
        SwaggerDoc doc = new SwaggerDoc();
        doc.generate(docOpt);
    }
}
