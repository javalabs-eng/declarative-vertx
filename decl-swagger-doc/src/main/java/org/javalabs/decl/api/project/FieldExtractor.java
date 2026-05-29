package org.javalabs.decl.api.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extractor to extract the fields from the resource provided in cli.
 *
 * @author Sudiptasish Chanda
 */
public class FieldExtractor {
    
    /**
     * Extract the field set and determine their data types.
     * 
     * Set the fields and their corresponding data types in the {@link Project} object.
     * 
     * <p>
     * The command line interface user can type in the attributes along with the resource name.
     * Example 1: project -c -d /Users/Sudiptasish Chanda/Projects -n vertx-rest -r Employee(name, location, salary)
     * Example 2: project -c -d /Users/Sudiptasish Chanda/Projects -n vertx-rest -r Employee(name::str, location::str, salary::float, active:bool)
     * 
     * This method will extract the attribute details along with their data types.
     * 
     * If the attributes are not properly set, or wrong attributes were defined or in case of syntax error, this
     * method will throw {@link IllegalArgumentException}
     * 
     * @param project  The project object
     */
    public void extract(Project project) {
        List<String> attrs = new ArrayList<>();
        List<Class<?>> datatypes = new ArrayList<>();
        
        Boolean idField = Boolean.FALSE;
        Boolean createdField = Boolean.FALSE;
        
        int startIdx = project.unparsedResource().indexOf("(");
        int endIdx = project.unparsedResource().indexOf(")");
        
        Project.InputResource input = new Project.InputResource();
        project.inputResource(input);
        
        if (startIdx > 0) {
            String parts = project.unparsedResource().substring(startIdx + 1, endIdx);
            String[] fields = parts.split(",");     // [ name, location#str, salary#long, active#bool ]

            for (int i = 0; i < fields.length; i ++) {
                fields[i] = fields[i].trim();
                String[] arr = fields[i].split("#");   // [ location, str ]
                DataType dtype = DataType.STR;

                if (arr.length == 1) {
                    if (arr[0].toLowerCase().endsWith("date")
                            || arr[0].toLowerCase().endsWith("time")
                            || arr[0].toLowerCase().endsWith("timestamp")) {
                        dtype = DataType.DATE;
                    }
                    else if (arr[0].toLowerCase().endsWith("flag")
                            || arr[0].toLowerCase().endsWith("indicator")) {
                        dtype = DataType.BOOL;
                    }
                }
                else if (arr.length == 2) {
                    dtype = Enum.valueOf(DataType.class, arr[1].toUpperCase());
                }

                if (arr[0].toLowerCase().endsWith("id") || arr[0].toLowerCase().contains("identifier")) {
                    idField = Boolean.TRUE;
                }
                else if (arr[0].toLowerCase().contains("create") && arr.length == 2 && arr[1].equals("date")) {
                    createdField = Boolean.TRUE;
                }
                attrs.add(arr[0]);
                datatypes.add(dtype.dtype());
            }
            if (! idField) {
                attrs.add(0, "id");
                datatypes.add(0, DataType.INT.dtype());
            }
            if (! createdField) {
                attrs.add("createdOn");
                datatypes.add(DataType.DATE.dtype());
            }
            // Add the updatedOn attribute
            attrs.add("updatedOn");
            datatypes.add(DataType.DATE.dtype());

            // Add the normalized resource name.
            String tmp = project.unparsedResource().substring(0, startIdx);
            tmp = Character.toUpperCase(tmp.charAt(0)) + tmp.substring(1);
            input.resource(tmp);
        }
        else {
            // No parenthesis
            // Use the default set of fields.
            attrs.addAll(Arrays.asList("id", "name", "location", "createdOn", "updatedOn"));
            datatypes.addAll(Arrays.asList(DataType.INT.dtype(), DataType.STR.dtype(), DataType.STR.dtype(), DataType.DATE.dtype(), DataType.DATE.dtype()));
            
            String tmp = project.unparsedResource();
            tmp = Character.toUpperCase(tmp.charAt(0)) + tmp.substring(1);
            input.resource(tmp);
        }
        
        // Now set them to the project.
        input.fields(attrs);
        input.dateTypes(datatypes);
    }
}
