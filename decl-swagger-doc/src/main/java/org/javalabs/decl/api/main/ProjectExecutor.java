package org.javalabs.decl.api.main;

import org.javalabs.decl.api.project.BuildTool;
import org.javalabs.decl.api.project.Platform;
import org.javalabs.decl.api.project.Project;
import org.javalabs.decl.api.project.ProjectTemplate;
import org.javalabs.decl.api.project.TechStack;
import java.util.Arrays;
import java.util.List;

/**
 * Project workspace executor.
 *
 * @author Sudiptasish Chanda
 */
public class ProjectExecutor implements ExecutorBase {
    
    private final String name = "project";
    private final String description = "Generate sample vert.x project with api documentation";
    private final List<String> longOptions = Arrays.asList("--create", "--platform", "--tech-stack", "--build-tool", "--dir", "--name", "--resource", "--verbose", "--e2e", "--orm-path", "--from-db", "--java-version", "--base-package");
    private final List<String> shortOptions = Arrays.asList("-c", "-p", "-t", "-b", "-d", "-n", "-r", "-v", "-a", "-o", "-f", "-j", "-k");
    
    public ProjectExecutor() {}

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }
    
    @Override
    public void start(String[] options) throws Exception {
        Project project = prepare(options);
        
        ProjectTemplate template = ProjectTemplate.get(project);
        Boolean status = template.setup(project);
        
        if (status) {
            // Do Something later
        }
    }
    
    private Project prepare(String[] options) {
        Boolean create = Boolean.FALSE;
        Project project = new Project();
        
        for (int i = 0; i < options.length; i ++) {
            if (options[i].equals("-c") || options[i].equals("--create")) {
                create = Boolean.TRUE;
            }
            else if (options[i].equals("-p") || options[i].equals("--platform")) {
                try {
                    verifyArg(options[i], options[i + 1]);
                    project.platform(Enum.valueOf(Platform.class, options[i + 1].toUpperCase()));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid platform " + options[i + 1]
                            + ". Valid values are: " + Arrays.toString(Platform.values()));
                }
            }
            else if (options[i].equals("-t") || options[i].equals("--tech-stack")) {
                try {
                    verifyArg(options[i], options[i + 1]);
                    project.stack(Enum.valueOf(TechStack.class, options[i + 1].toUpperCase()));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid tech-stack " + options[i + 1]
                            + ". Valid values are: " + Arrays.toString(TechStack.values()));
                }
            }
            else if (options[i].equals("-b") || options[i].equals("--build-tool")) {
                try {
                    verifyArg(options[i], options[i + 1]);
                    project.buildTool(Enum.valueOf(BuildTool.class, options[i + 1].toUpperCase()));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid build-tool " + options[i + 1]
                            + ". Valid values are: " + Arrays.toString(BuildTool.values()));
                }
            }
            else if (options[i].equals("-d") || options[i].equals("--dir")) {
                verifyArg(options[i], options[i + 1]);
                project.dir(options[i + 1]);
            }
            else if (options[i].equals("-n") || options[i].equals("--name")) {
                verifyArg(options[i], options[i + 1]);
                project.name(options[i + 1]);
            }
            else if (options[i].equals("-j") || options[i].equals("--java-version")) {
                project.jdkVersion(options[i + 1]);
            }
            else if (options[i].equals("-k") || options[i].equals("--base-package")) {
                project.basePkg(options[i + 1]);
            }
            else if (options[i].equals("-r") || options[i].equals("--resource")) {
                String rTmp = options[i + 1];
                if (rTmp.contains("(") && ! rTmp.endsWith(")")) {
                    // Take all string entries until we encounter )
                    for (int k = i + 2; ; k ++) {
                        rTmp += options[k];
                        if (options[k].endsWith(")")) {
                            // Found the end
                            i = k;
                            break;
                        }
                    }
                }
                else {
                    verifyArg(options[i], rTmp);
                }
                if (Character.isLowerCase(rTmp.charAt(0))) {
                    rTmp = Character.toUpperCase(rTmp.charAt(0)) + rTmp.substring(1);
                }
                project.unparsedResource(rTmp);
            }
            else if (options[i].equals("-v") || options[i].equals("--verbose")) {
                verifyArg(options[i], options[i + 1]);
                project.verbose(Integer.valueOf(options[i + 1]));
            }
            else if (options[i].equals("-a") || options[i].equals("--e2e")) {
                project.e2e(Boolean.TRUE);
            }
            else if (options[i].equals("-o") || options[i].equals("--orm-path")) {
                project.ormPath(options[i + 1]);
            }
            else if (options[i].equals("-f") || options[i].equals("--from-db")) {
                project.fromDb(Boolean.TRUE);
            }
            else if (options[i].equals("--db-host") && project.fromDb()) {
                project.dbHost(options[i + 1]);
            }
            else if (options[i].equals("--db-port") && project.fromDb()) {
                project.dbPort(options[i + 1]);
            }
            else if (options[i].equals("--db-name") && project.fromDb()) {
                project.dbName(options[i + 1]);
            }
            else if (options[i].equals("--db-schema") && project.fromDb()) {
                project.dbSchema(options[i + 1]);
            }
            else if (options[i].equals("--db-user") && project.fromDb()) {
                project.dbUser(options[i + 1]);
            }
            else if (options[i].equals("--db-password") && project.fromDb()) {
                project.dbPassword(options[i + 1]);
            }
            else if (options[i].equals("--db-dialect") && project.fromDb()) {
                project.dbDialect(options[i + 1]);
            }
        }
        if (! create) {
            throw new IllegalArgumentException("Did you miss the -c [--create] flag?");
        }
        if (! project.e2e() && project.unparsedResource() == null) {
            throw new IllegalArgumentException("Must provide the resource name [E.g., Employee, Bank, Student, etc]");
        }
        if (project.e2e() && ! project.fromDb() && project.ormPath() == null) {
            throw new IllegalArgumentException("Must provide path to orm.xml for end-to-end code generation");
        }
        if (project.fromDb() && ! project.e2e()) {
            throw new IllegalArgumentException("Must provide 'e2e' option as well when --from-db is set");
        }
        if (project.fromDb() && project.dbPassword() == null) {
            throw new IllegalArgumentException("Must provide the database password");
        }
        return project;
    }
    
    private void verifyArg(String param, String val) {
        if (longOptions.contains(val) || shortOptions.contains(val)) {
            throw new IllegalArgumentException("Missing value for option [" + param + "]");
        }
    }
}
