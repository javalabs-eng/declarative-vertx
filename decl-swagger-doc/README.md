## Table of Contents  
- [OpenAPI vs Swagger](#bg)
- [About this Wiki](#ab)  
  - [Project Generator](#pg)
  - [Swagger Doc Generator](#sg)
- [Sample Configuration](#sccm)
- [Appendix](#app)


<a name="bg"/>

## OpenAPI vs Swagger

OpenAPI is a specification for designing RESTful APIs, while Swagger is a set of tools for implementing the OpenAPI specificationing.

### OpenAPI

A globally recognized specification for defining RESTful APIs. It's a vendor-neutral and language-independent interface that supports the design-first approach.
OpenAPI documents allow developers to define the essentials of an API, including: 

1. The presence and operations of each endpoint 
1. The input and output parameters of each operation 
1. Techniques for authentication 

Visit [OpenAPI Page](https://www.openapis.org) for more details.

### Swagger

Swagger is the name associated with some of the most well-known, and widely used tools for implementing the OpenAPI specification.
The Swagger toolset includes a mix of open source, free, and commercial tools, which can be used at different stages of the API lifecycle.
Visit [Swagger Home Page](https://swagger.io) for more details.

If you are interested in contributing towards the sdk, visit our [CONTRIBUTING.md](CONTRIBUTING.md) file.

<a name="ab"/>

## About this Wiki?

Swagger doc project provides two utilities.

<a name="pg"/>

### Project Generator:
It is a tool that automatically creates source code for a project based on predefined templates and user-provided parameters, essentially automating repetitive coding tasks.
It takes input like project specifications, platform, tech-stack, language, build tool, etc, and generate the corresponding code in a chosen programming language.

**Benefits**:
* Increased development speed: Reduce time spent on boilerplate code. 
* Improved consistency: Ensure code adheres to defined standards. 
* Reduced errors: Minimize manual typing mistakes.

Note that, while the generator automates much of the code, you might still need to manually adjust generated code to fit specific requirements.

#### How to Run

Steps:
1. Checkout and compile the project. Use `mvn clean install` to build the project.
2. Run the command `./openapi`
The welcome page will appear. It provides a command line interface. Follow the instruction to generate a project.

```
###################################################################################
#                                                                                 #
#                            Api doc generation tool                              #
#                                                                                 #
# Commands:                                                                       #
#                                                                                 #
#  swagger         Generate swagger api documentation                             #
#  project         Generate sample project along with api doc                     #
#                                                                                 #
###################################################################################

Type "help" to see the available commands
Use "command help" for usage of "command". [Example: swagger help]

<openapi>
```

3. View options for `project`

```
<openapi> project help

Description                        : Generate sample vert.x project with api documentation
Usage                              : project [OPTIONS] ...
Example 1 (use default fields)     : project -c -d /tmp -n example-rest -r Employee
Example 2 (specify field names)    : project -c -d /tmp -n example-rest -r Employee(name, location, salary#long). Default data type: #str

Supported data types               [STR, INT, LONG, FLOAT, BOOL, DATE]

The options are:

-c [--create]                        Generate the vert.x project
-p [--platform] <platform_name>      Platform name, E.g., Java, Go, NodeJs [default: Java]
-t [--tech-stack] <techstack>        Techstack, applicable when platform is selected as Java. E.g., Vertx, SpringBoot, JaxRs [default: Vertx]
-d [--dir] <dir_name>                Root directory of the project to be created
-n [--name] <name>                   Name of the java project
-r [--resource] <resource_name>      Resource name. E.g. Employee, Student, Bank, etc
-v [--verbose]                       Verbose Output. 1 (Granular level logging) | 2 (Summary logging). Default: 2
-j [--java-version] <ver>            The target java version of the generated application [default: 17]
-k [--base-package] <com.test.app>   The base package of the generated java sources [default: io.opns.app]

```

Examples:

* Command to create a Vert.x REST project.
```
project -c -t vertx -d /path/to/project_dir -n vertx-rest -r Employee(name, location, salary#long)
```

* Command to create a Spring boot REST project.
```
project -c -t spring -d /path/to/project_dir -n spring-rest -r Employee(name, location, salary#long)
```

* Command to create a Jax-Rs REST project.
```
project -c -t jaxrs -d /path/to/project_dir -n jaxrs-rest -r Employee(name, location, salary#long)
```

4. Advanced options for `project` created from an `orm.xml`

```
Advanced options:
--e2e                       Generate the end-to-end code driven by the database table design as specified in the orm.xml file
--orm-path                  Path to the orm (Object Relational Mapping) xml file. Mandatory if the --e2e option is specified.

```

Example:

* Create a Vert.x REST project from a pre-existing orm.xml file.
```
project -c -t vertx -d /path/to/project_dir -n example-rest --e2e --orm-path /path/to/orm.xml
```

Refer to [Appendix](#app) to see a sample `orm.xml` file.


5. Advanced options for `project` created from a database.

```
Advanced options:
--e2e                       Generate the end-to-end code driven by the database table design as specified in the orm.xml file
--from-db                   Use this option, if you want to connect to a database to extract the table metadata and generate
                            the end-to-end code. You have to provide the following options if '--from-db' is specified.

--db-host                   Database host     [default: localhost]
--db-port                   Database port     [default: 5432, indicating a local PostgreSQL instance]
--db-name                   Database name     [default: testdb]
--db-schema                 Database schema   [default: public]
--db-user                   Database user     [default: test]
--db-password               Database password
--db-dialect                Dialect name      [default: postgres]

```

Example

* Create a Vert.x REST project from a remote database (from a pre-existing set of tables).
```
project -c -t vertx -d /path/to/project_dir -n example-rest --e2e --from-db --db-host localhost --db-port 5432 --db-name testdb --db-user test --db-password test@123 --db-dialect postgres
```

**Not:** The generated project will have a `README.md` file. Follow the file for further instruction.

<a name="sg"/>

### Swagger Doc Generator

A tool that will automatically generate the swagger documentation from a pre-defined config file `routing-config.xml`.
Sample routing xml file:

```
<?xml version="1.0" encoding="UTF-8"?>

<routing-config>
    <!-- Package name where to find all the handler classes -->
    <package name="com.example.rest"/>
    
    <api version="1.0" basePath="/api/v1" produce="application/vnd.ex+json.v1" consume="application/json"/>
    
    <resource-mapping name="Employee"
            path="/employees"
            resource="com.example.rest.handler.EmployeeHandler"
            schema="com.example.rest.model.Employee">

        <mapping uri=""     method="POST"   api="create"/>
        <mapping uri="/:id" method="PUT"    api="modify"/>
        <mapping uri="/:id" method="GET"    api="view"/>
        <mapping uri="/:id" method="DELETE" api="remove"/>
        <mapping uri=""     method="GET"    api="viewAll"/>
    </resource-mapping>
    
</routing-config>
```

1. View options for `swagger`

```
<openapi> swagger help

Description                        : Generate swagger api documentation of a project
Usage                              : swagger [OPTIONS] ...
Example                            : swagger -c -r routing-config.xml -m jar1:jar2 -o /tmp/openapi.yaml

The options are:

-c [--create]                            Generate swagger doc
-r [--routing-file] <file_name>          Path of the routing-config file [default: routing-config.xml]
-m [--model-lib] <jar_file>              Jar(s) or location of the model java classes used in your project
-o [--out-file] </path/openapi.yaml>     Complete path where the openapi.yaml file will be generated
-v [--verbose]                           Verbose Output (default: 2)

```

2. Example

* Generate swagger doc for an existing project. The project must have all the business object model files (POJOs) and the `routing-config.xml`.
Multiple jar files can be specified as separated by `colon(:)`.

`swagger -c -r routing-config.xml -m /Users/schan280/Projects/vertx-rest/target/vertx-rest-0.0.1-SNAPSHOT.jar -o /Users/schan280/openapi.yaml -v 1`

<a name="app"/>

## Appendix

### View the Swagger Doc

The {project_path}/docs directory has both the index.html and final merged file, openapi.yaml. Start the python http server by running the below command:
`python3 -m http.server -b 127.0.0.1`

You should see the output like: Serving HTTP on 0.0.0.0 port 8000 ...

After enabling Simple HTTPServer successfully, it will start serving files through port number 8000.

Step 5: Type the url: http://localhost:8000 on the browser and you should see the API spec.

### Sample ORM File

This orm.xml file has schema definition for the following two tables:
1. `products`
2. `books`

```
<?xml version="1.0" encoding="UTF-8" ?>
<entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_1_0.xsd"
    version="1.0">
    
    <package>org.javalabs.model</package>

    <entity class="org.javalabs.model.Product" name="Product">
        <table name="products"/>
        <named-native-queries>
            <named-native-query>
                <name>Product.selectAll</name>
                <query>SELECT * FROM products</query>
            </named-native-query>
            <named-native-query>
                <name>Product.selectByProduct</name>
                <query>SELECT * FROM products WHERE product_name = ?</query>
            </named-native-query>
        </named-native-queries>
        <attributes>
            <id name="id">
                <generated-value strategy="AUTO"/>
            </id>
            <basic name="productName" type="java.lang.String">
                <column name="product_name" length="100" nullable="false" insertable="true" updatable="false"/>
            </basic>
            <basic name="description" type="java.lang.String">
                <column name="description" length="255"/>
            </basic>
            <basic name="price" type="java.lang.Integer">
                <column name="price" nullable="false" precision="10" scale="4"/>
            </basic>
        </attributes>
    </entity>

    <entity class="org.javalabs.model.Book" name="Book">
        <table name="books"/>
        <attributes>
            <id name="bookId" type="java.lang.String">
                <column name="book_id" length="32"></column>
            </id>
            <id name="publishDate" type="java.sq.Timestamp">
                <column name="publish_date"></column>
            </id>
            <basic name="repoName" type="java.lang.String">
                <column name="repoName" length="256" nullable="false" insertable="true" updatable="false"/>
            </basic>
            <basic name="writeDate" type="java.sq.Timestamp">
                <column name="write_date" nullable="false" insertable="true" updatable="true"/>
            </basic>
        </attributes>
    </entity>
</entity-mappings>

```
