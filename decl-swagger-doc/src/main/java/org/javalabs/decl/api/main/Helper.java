package org.javalabs.decl.api.main;

import org.javalabs.decl.api.project.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Helper {
    
    private final Map<String, ExecutorBase> commands = new HashMap<>();
    
    public Helper() {
        commands.put("swagger", new SwaggerExecutor());
        commands.put("project", new ProjectExecutor());
    }
    
    public List<String> commands() {
        return new ArrayList<>(commands.keySet());
    }
    
    public ExecutorBase command(String name) {
        return commands.get(name);
    }
    
    public String welcome() {
        StringBuilder buff = new StringBuilder(128);
        
        buff.append("\n").append("###################################################################################");
        buff.append("\n").append("#                                                                                 #");
        buff.append("\n").append("#                            Api doc generation tool                              #");
        buff.append("\n").append("#                                                                                 #");
        buff.append("\n").append("# Commands:                                                                       #");
        buff.append("\n").append("#                                                                                 #");
        buff.append("\n").append(String.format("#  swagger         Generate swagger api documentation                             #"));
        buff.append("\n").append(String.format("#  project         Generate sample vert.x project with api doc                    #"));
        buff.append("\n").append("#                                                                                 #");
        buff.append("\n").append("###################################################################################");
        buff.append("\n").append("\n").append("Type \"help\" to see the available commands");
        buff.append("\n").append("Use \"command help\" for usage of \"command\". [Example: swagger help]").append("\n");
        
        //else {
        //    buff.append("Invalid command").append("\n");
        //    buff.append("If you are unsure of the command, type <command> help to see the list of options").append("\n");
        //}
        return buff.toString();
    }
    
    public String options(String command) {
        StringBuilder buff = new StringBuilder(512);
        
        if (command.equals("swagger")) {
            buff.append("\n").append(String.format("%-35s: %s", "Description", "Generate swagger api documentation of a project"));
            buff.append("\n").append(String.format("%-35s: %s", "Usage", "swagger [OPTIONS] ..."));
            buff.append("\n").append(String.format("%-35s: %s", "Example", "swagger -c -r routing-config.xml -m jar1:jar2 -o /tmp/openapi.yaml"));
            buff.append("\n\n").append("The options are:");
            buff.append("\n\n");
            buff.append(String.format("%-40s %s\n", "-c [--create]", "Generate swagger doc"));
            buff.append(String.format("%-40s %s\n", "-r [--routing-file] <file_name>", "Path of the routing-config file [default: routing-config.xml]"));
            buff.append(String.format("%-40s %s\n", "-m [--model-lib] <jar_file>", "Jar(s) or location of the model java classes used in your project"));
            buff.append(String.format("%-40s %s\n", "-o [--out-file] </path/openapi.yaml>", "Complete path where the openapi.yaml file will be generated"));
            buff.append(String.format("%-40s %s\n", "-v [--verbose]", "Verbose Output"));
        }
        else if (command.equals("project")) {
            buff.append("\n").append(String.format("%-35s: %s", "Description", "Generate sample vert.x project with api documentation"));
            buff.append("\n").append(String.format("%-35s: %s", "Usage", "project [OPTIONS] ..."));
            buff.append("\n").append(String.format("%-35s: %s", "Example 1 (use default fields)", "project -c -d /tmp -n example-rest -r Employee"));
            buff.append("\n").append(String.format("%-35s: %s", "Example 2 (specify field names)", "project -c -d /tmp -n example-rest -r Employee(name, location, salary#long)"));
            buff.append("\n\n").append("The options are:");
            buff.append("\n\n");
            buff.append(String.format("%-40s %s\n", "-c [--create]", "Generate the vert.x project"));
            buff.append(String.format("%-40s %s\n", "-p [--platform] <platform_name>", "Platform name, E.g., Java, Go, NodeJs [default: Java]"));
            buff.append(String.format("%-40s %s\n", "-t [--tech-stack] <techstack>", "Techstack, applicable when platform is selected as Java. E.g., Vertx, SpringBoot, Jax-Rs [default: Vertx]"));
            buff.append(String.format("%-40s %s\n", "-d [--dir] <dir_name>", "Root directory of the project to be created"));
            buff.append(String.format("%-40s %s\n", "-n [--name] <name>", "Name of the java project"));
            buff.append(String.format("%-40s %s\n", "-r [--resource] <resource_name>", "Resource name. E.g. Employee, Student, Bank, etc"));
            buff.append(String.format("%-40s %s\n", "-v [--verbose]", "Verbose Output. 1 (Granular level logging) | 2 (Summary logging). Default: 2"));
            buff.append(String.format("%-40s %s\n", "Supported data types", Arrays.toString(DataType.values())));
        }
        else {
            buff.append("Invalid command ").append(command).append("\n");
            buff.append("If you are unsure about the command, type help to see the list of commands").append("\n");
        }
        return buff.toString();
    }
}
