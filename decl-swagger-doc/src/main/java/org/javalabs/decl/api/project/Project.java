package org.javalabs.decl.api.project;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.javalabs.decl.gen.JavaClass;

/**
 * The project class that encapsulates the attributes/hints to setup a workspace.
 *
 * @author Sudiptasish Chanda
 */
public class Project {
    
    private String name;
    private String dir = System.getProperty("user.dir");
    private String jdkVersion = "17";
    
    private Platform platform = Platform.JAVA;
    private TechStack stack = TechStack.VERTX;
    private BuildTool buildTool = BuildTool.MAVEN;
    
    // 2: Level two logging (print only summary)
    // 1: Level one logging (print class file and config file along with summary)
    private Integer verbose = 2;        // Default: Level 2 logging 

    private String srcDir = "src/main/java";
    private String srcResourceDir = "src/main/resources";
    private String testDir = "src/test/java";
    private String testResourceDir = "src/test/resources";
    private String targetDir = "target";
    private String generated = "generated";
    
    private String basePkg = "io.opns.app";
    private String corePkg = basePkg + "." + "core";
    private String utilPkg = basePkg + "." + "util";
    private String handlerPkg = basePkg + "." + "handler";
    private String boPkg = basePkg + "." + "bo";
    private String daoPkg = basePkg + "." + "dao";
    private String modelPkg = basePkg + "." + "model";
    private String authPkg = basePkg + "." + "auth";
    private String configPkg = basePkg + "." + "config";
    private String mainPkg = basePkg + "." + "main";
    
    private String docDir = "docs";
    private String dbDir = srcResourceDir + File.separator + "db";
    
    private final Properties dbProps = new Properties();
    
    private String keyStore = "server.pkcs";
    private String storePass = "secret";
    private Integer validityDays = 360;
    
    private Boolean e2e = Boolean.FALSE; 
    private String ormPath = "./orm/orm.xml";
    
    private Boolean fromDb = Boolean.FALSE;
    private String dbHost = "localhost";
    private String dbPort = "5432";
    private String dbName = "testdb";
    private String dbSchema = "public";
    private String dbUser = "test";
    private String dbPassword;
    private String dbDialect = "postgres";
    
    private String unparsedResource;
    private InputResource inputResource;
    
    private JavaClass model;
    
    public String name() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }
    
    public String jdkVersion() {
        return jdkVersion;
    }

    public Project jdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
        return this;
    }
    
    public Boolean e2e() {
        return e2e;
    }

    public Project e2e(Boolean e2e) {
        this.e2e = e2e;
        return this;
    }
    
    public Boolean fromDb() {
        return fromDb;
    }

    public Project fromDb(Boolean fromDb) {
        this.fromDb = fromDb;
        return this;
    }

    public String dir() {
        return dir;
    }

    public Project dir(String dir) {
        this.dir = dir;
        return this;
    }

    public Platform platform() {
        return platform;
    }

    public Project platform(Platform platform) {
        this.platform = platform;
        return this;
    }

    public TechStack stack() {
        return stack;
    }

    public Project stack(TechStack stack) {
        this.stack = stack;
        return this;
    }

    public BuildTool buildTool() {
        return buildTool;
    }

    public Project buildTool(BuildTool buildTool) {
        this.buildTool = buildTool;
        return this;
    }

    public Integer verbose() {
        return verbose;
    }

    public Project verbose(Integer verbose) {
        this.verbose = verbose;
        return this;
    }

    public String srcDir() {
        return srcDir;
    }

    public Project srcDir(String srcDir) {
        this.srcDir = srcDir;
        return this;
    }

    public String srcResourceDir() {
        return srcResourceDir;
    }

    public Project srcResourceDir(String srcResourceDir) {
        this.srcResourceDir = srcResourceDir;
        return this;
    }

    public String testDir() {
        return testDir;
    }

    public Project testDir(String testDir) {
        this.testDir = testDir;
        return this;
    }

    public String testResourceDir() {
        return testResourceDir;
    }

    public Project testResourceDir(String testResourceDir) {
        this.testResourceDir = testResourceDir;
        return this;
    }

    public String targetDir() {
        return targetDir;
    }

    public Project targetDir(String targetDir) {
        this.targetDir = targetDir;
        return this;
    }

    public String generated() {
        return generated;
    }

    public void generated(String generated) {
        this.generated = generated;
    }

    public String basePkg() {
        return basePkg;
    }

    public Project basePkg(String basePkg) {
        this.basePkg = basePkg;
        
        this.corePkg = basePkg + "." + "core";
        this.utilPkg = basePkg + "." + "util";
        this.handlerPkg = basePkg + "." + "handler";
        this.boPkg = basePkg + "." + "bo";
        this.daoPkg = basePkg + "." + "dao";
        this.modelPkg = basePkg + "." + "model";
        this.authPkg = basePkg + "." + "auth";
        this.configPkg = basePkg + "." + "config";
        this.mainPkg = basePkg + "." + "main";
        
        return this;
    }

    public String handlerPkg() {
        return handlerPkg;
    }

    public Project handlerPkg(String handlerPkg) {
        this.handlerPkg = handlerPkg;
        return this;
    }

    public String utilPkg() {
        return utilPkg;
    }

    public Project utilPkg(String utilPkg) {
        this.utilPkg = utilPkg;
        return this;
    }

    public String boPkg() {
        return boPkg;
    }

    public Project boPkg(String boPkg) {
        this.boPkg = boPkg;
        return this;
    }

    public String daoPkg() {
        return daoPkg;
    }

    public Project daoPkg(String daoPkg) {
        this.daoPkg = daoPkg;
        return this;
    }

    public String modelPkg() {
        return modelPkg;
    }

    public Project modelPkg(String modelPkg) {
        this.modelPkg = modelPkg;
        return this;
    }

    public String authPkg() {
        return authPkg;
    }

    public Project authPkg(String authPkg) {
        this.authPkg = authPkg;
        return this;
    }

    public String configPkg() {
        return configPkg;
    }

    public Project configPkg(String configPkg) {
        this.configPkg = configPkg;
        return this;
    }

    public String corePkg() {
        return corePkg;
    }

    public Project corePkg(String corePkg) {
        this.corePkg = corePkg;
        return this;
    }

    public String mainPkg() {
        return mainPkg;
    }

    public Project mainPkg(String mainPkg) {
        this.mainPkg = mainPkg;
        return this;
    }

    public String unparsedResource() {
        return unparsedResource;
    }

    public Project unparsedResource(String unparsedResource) {
        this.unparsedResource = unparsedResource;
        return this;
    }

    public JavaClass model() {
        return model;
    }

    public void model(JavaClass model) {
        this.model = model;
    }

    public String docDir() {
        return docDir;
    }

    public void docDir(String docDir) {
        this.docDir = docDir;
    }

    public String dbDir() {
        return dbDir;
    }

    public void dbDir(String dbDir) {
        this.dbDir = dbDir;
    }

    public String keyStore() {
        return keyStore;
    }

    public void keyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String storePass() {
        return storePass;
    }

    public void storePass(String storePass) {
        this.storePass = storePass;
    }

    public Integer validityDays() {
        return validityDays;
    }

    public void validityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }
    
    public void addDbProps(String key, String val) {
        dbProps.setProperty(key, val);
    }
    
    public String dbProps(String key, String defaultVal) {
        return dbProps.getProperty(key, defaultVal);
    }

    public InputResource inputResource() {
        return inputResource;
    }

    public void inputResource(InputResource inputResource) {
        this.inputResource = inputResource;
    }

    public String ormPath() {
        return ormPath;
    }

    public void ormPath(String ormPath) {
        this.ormPath = ormPath;
    }
    
    public String dbHost() {
        return dbHost;
    }

    public Project dbHost(String dbHost) {
        this.dbHost = dbHost;
        return this;
    }
    
    public String dbPort() {
        return dbPort;
    }

    public Project dbPort(String dbPort) {
        this.dbPort = dbPort;
        return this;
    }
    
    public String dbName() {
        return dbName;
    }

    public Project dbName(String dbName) {
        this.dbName = dbName;
        return this;
    }
    
    public String dbSchema() {
        return dbSchema;
    }

    public Project dbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
        return this;
    }
    
    public String dbUser() {
        return dbUser;
    }

    public Project dbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }
    
    public String dbPassword() {
        return dbPassword;
    }

    public Project dbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }
    
    public String dbDialect() {
        return dbDialect;
    }

    public Project dbDialect(String dbDialect) {
        this.dbDialect = dbDialect;
        return this;
    }
    
    public static class InputResource {
        
        private String table;
        private String resource;
        private List<String> fields;
        private List<Class<?>> dateTypes;
        
        public String table() {
            return table;
        }

        public InputResource table(String table) {
            this.table = table;
            return this;
        }

        public String resource() {
            return resource;
        }

        public InputResource resource(String resource) {
            this.resource = resource;
            return this;
        }

        public List<String> fields() {
            return fields;
        }

        public InputResource fields(List<String> fields) {
            this.fields = fields;
            return this;
        }

        public List<Class<?>> dateTypes() {
            return dateTypes;
        }

        public InputResource dateTypes(List<Class<?>> dateTypes) {
            this.dateTypes = dateTypes;
            return this;
        }
        
    }
}
