package org.javalabs.decl.api.main;

import org.javalabs.decl.api.doc.DocOption;
import org.javalabs.decl.api.doc.SwaggerDoc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.javalabs.decl.util.ConsoleWriter;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OpenApiMain {
    
    private static final Helper helper = new Helper();
    
    public static void main(String[] args) throws Exception {
        BufferedReader br = null;
        String line = null;
            
        try {
            // Initialize the workflow.
            Class.forName("org.javalabs.decl.workflow.WorkflowInitializer");
            
            // Display the cli prompt
            ConsoleWriter.prompt();
            
            // Print the welcome message
            ConsoleWriter.println(helper.welcome());
            
            ConsoleWriter.prompt();
            
            br = new BufferedReader(new InputStreamReader(System.in));
            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.equals("quit") || line.equals("bye") || line.equals("exit")) {
                        break;
                    }
                    // Request for help.
                    // <openapi> help
                    if (line.equals("help")) {
                        ConsoleWriter.timingPrint(helper.welcome());
                    }
                    else {
                        // Execute specific command and supply the options
                        // <openapi> swagger -create -routing-file routing-config.xml -model-lib /path/to/ninja-rest.jar -out-file ~/openapi.yaml
                        String[] arr = line.split(" ");
                        if ((arr.length == 1 && helper.commands().contains(arr[0]))
                                || (arr.length == 2 && helper.commands().contains(arr[0]) && arr[1].equals("help"))) {

                            // Print the options for the chosen command
                            ConsoleWriter.println(helper.options(arr[0]));
                        }
                        else if (arr.length > 2 && helper.commands().contains(arr[0]) && !arr[1].equals("help")) {
                            // Captured the arguments and possibly execute the command.
                            String[] options = new String[arr.length - 1];
                            System.arraycopy(arr, 1, options, 0, options.length);

                            ExecutorBase cmd = helper.command(arr[0]);
                            cmd.start(options);
                        }
                        else {
                            ConsoleWriter.println("Invalid command. Type help to see the available commands");
                        }
                    }
                }
                catch (Exception e) {
                    ConsoleWriter.println(e.getMessage());
                }
                ConsoleWriter.prompt();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                br.close();
            }
        }
    }
    
    public static void help(String[] args) throws Exception {
        DocOption docOpt = new DocOption();
        docOpt.setConfigFile("routing-config.xml");
        // docOpt.setMethods(Arrays.asList("get", "post", "put", "delete"));
        docOpt.setOutFile("/Users/Sudiptasish Chanda/Projects/openapi.yaml");
        
        SwaggerDoc doc = new SwaggerDoc();
        doc.generate(docOpt);
    }

}
