package org.javalabs.decl.api.agent;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import org.javalabs.decl.api.doc.DocOption;
import org.javalabs.decl.api.doc.JaxRsSwaggerDoc;

/**
 *
 * @author Sudiptasish Chanda
 */
public class JaxRsSwaggerAgent extends SwaggerAgent {

    private static final String SWAGGER_DOC = "org.javalabs.decl.api.doc.JaxRsSwaggerDoc";

    @Override
    public void crawl(String packageName, String outputFile) {
        try {
            URLClassLoader loader = new URLClassLoader(new URL[] {new File(".").toURI().toURL()}, this.getClass().getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);
            
            DocOption docOpt = new DocOption();
            docOpt.setVerbose(2);
            docOpt.setPackages(new String[] {packageName});
            docOpt.setOutFile(outputFile);
            
            JaxRsSwaggerDoc swaggerDoc = new JaxRsSwaggerDoc();
            swaggerDoc.generate(docOpt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
