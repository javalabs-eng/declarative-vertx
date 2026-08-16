## Table of Contents  
- [Background](#bg)
- [Declarative Approach in Building REST API](#sccm)
  - [Declarative Vert.x - Complete Example](#sex)
    - [Add Dependency](#sexd)
    - [Add vertx-web.xml](#sexv)
    - [Add server.xml](#sexs)
    - [Add routing-config.xml](#sexr)
    - [Finish Setup](#sexf)
    - [Run Your Test Application](#sexr)
    - [Optional Container](#sexo)
  - [Declarative Vert.x - Routing Example](#ser)
    - [Add routing-config.xml](#serx)
    - [Write The Main Class](#serw)
    - [Run Your Test Application](#serr)
- [Handler Vs Lambda](#app)
  - [Key Differences](#app)
  - [When To Use A Handler Class](#app)
  - [When To Use A Lambda Expression](#app)
- [Appendix A: Vertx-Web.xml](#appa)
- [Appendix B: Server.xml](#appa)
- [Appendix C: Routing-Config.xml](#appc)
- [Appendix D: Javadoc](#appd)

  
<a name="bg"/>

## Background

<div align="justify">
Traditionally application like <b>Servlet</b>, <b>Taglib</b>, <b>Spring Boot</b>, <b>Jax-Rs</b>, etc heavily leverage external <b>xml</b> configuration or annotations to define REST API endpoints. It allows developers to define the mapping between HTTP requests and Java methods within a class by decorating them with annotations like <b>@Path</b>, <b>@GET</b>, <b>@POST</b>, <b>@PUT</b>, and <b>@DELETE</b> to specify the resource URI and supported HTTP methods respectively. Annotations or external configuration provide a more declarative way to define RESTful web service behavior without needing extensive configuration or boiler-plate code.
</div>
<br/>
Vert.x takes a more code-centric approach and does not provide extensive annotation or external configuration support for building REST API.
<br/>
<br/>
<div align="justify">
Key points about Vert.x and REST API development:
<ul>
<li><b>Route-based:</b>
Vert.x uses a routing mechanism where you define routes based on HTTP methods (GET, POST, PUT, DELETE) and paths, and then associate them with custom handler functions to process requests.</li>
<li><b>Flexibility:</b>
This approach gives developers more control over how requests are handled but requires writing more explicit code compared to Spring's annotation-based approach.</li>
<li><b>Vert.x Web module:</b>
The core functionality for building REST APIs in Vert.x is provided by the "vertx-web" module, which provides classes like Router to define routes and handle requests.</li>
</ul>
</div>
Refer to [Vert.x REST](https://vertx.io/blog/some-rest-with-vert-x/) to see how you can use vert.x to develop your RESTful resources.

<a name="sccm"/>

## Declarative Approach in Building REST API

<div align="justify">
A "declarative approach" in creating a REST API means focusing on defining the desired state of your data and the operations you want to perform on it, without specifying the exact steps to achieve that state.
<br/><br/>
Some key points:
<ul>
<li><b>Focus on resource representation:</b>
Primarily define the data structures (resources) that your API exposes, outlining their attributes and relationships, rather than intricate logic for manipulating them.</li>
<li><b>Standard HTTP verbs:</b>
Utilize standard HTTP methods like GET, POST, PUT, DELETE to clearly indicate the intended actions on those resources, allowing the client to understand the operations without needing additional instructions.</li> 
<li><b>Stateless design:</b>
Each request should contain all the necessary information to be processed independently, without relying on previous interactions with the server.</li>
<li><b>Minimal configuration:</b>
Use declarative frameworks or tools to specify the desired API behavior through configuration files, allowing for easier management and updates</li>
</ul>
Today, Vert.x does not provide any framework to achieve this. And <b>declarative-vertx</b> aims to bridge this gap.
</div>

<a name="sex"/>

### Declarative Vert.x - Complete Example

<div align="justify">
To use <b>declarative-vertx</b>, you need three configuration files.
<ul>
<li><b>vertx-web.xml</b> - This file is the core configuration file that provides configuration and deployment information for Vert.x. It's the standard name used by decl-vertx-container module as a deployment descriptor in Vert.x applications. Apart from standard vert.x configuration, this file also defines the Verticles that will be deployed.</li>
<li><b>server.xml</b> - If any of your verticles is starting an http server, then you need to create the second file server.xml, which is a configuration file for the embedded http server. It dictates how the server behaves during startup and operation. It also defines various elements like the server, services, connectors, and containers, which handle requests and manage web applications.</li>
<li><b>routing-config.xml</b> - This file defines how HTTP requests are handled based on their paths and methods. Configuration typically involves setting up routes with corresponding handlers, potentially including path parameters and request body processing</li>
</ul>
</div>

In the below section we will see how to setup your project to use <b>declarative-vertx</b>

You can find the step by step tutorial in the [decl-swagger-doc](decl-swagger-doc) project.

<a name="sexd"/>

#### Add Dependency

##### Maven Project

Modify your `pom.xml` file and add the following dependency in your pom file.

```
<dependency>
    <groupId>org.javalabs.decl</groupId>
    <artifactId>decl-vertx-container</artifactId>
    <version>2.0.5</version>
</dependency>
```

##### Gradle Project

```
dependencies {
    compile 'org.javalabs.decl:decl-vertx-container:0.0.1-SNAPSHOT'
}
```

<a name="sexv"/>

#### Add vertx-web.xml. 

Create the file `vertx-web.xml` and place it under `src/main/resources` directory.
Here is a sample file. You can specify as many verticles as you want to deploy by adding a new `verticle` section.

```
<vertx-web>
    <verticles>
        <verticle>
            <name>http.server</name>
            <class>com.example.rest.TestHttpServerVerticle</class>
	    <!-- Specify http configuration file (server.xml) only if the verticle is supposed to start an embedded http server -->
            <config>server.xml</config>
        </verticle>
	<verticle>
            <name>test.processor</name>
            <class>com.example.rest.TestWorkerVerticle</class>
            <deploy-options>
                <worker>true</worker>
            </deploy-options>
        </verticle>
    </verticles>
    
</vertx-web>
```

Refer to [vertx-web.xsd](decl-vertx-config/src/main/resources/schema/vertx-web.xsd) to understand the schema.
Click [vertx-web.dtd](decl-vertx-config/src/main/resources/dtd/vertx-web.dtd) to view the dtd.

We have defined two verticles.
1. `http.server` - This will start the embedded http server. Thus require the file `server.xml`.
2. `test.processor` - This is a background worker verticle.

We have specified the minimum attributes needed to configure a verticle. Refer to [Appendix A: Vertx-Web.xml](#appa) for advanced configuration.

<a name="sexs"/>

#### Add server.xml

Create a file `server.xml` which will be used by the `http.server` to start the http server. Place it under `src/main/resources` directory.

```
<server-config>
    <server-opts>
    	<ssl>false</ssl>
	<port>8080</port>
    </server-opts>
    
    <context-root>/api/v1</context-root>
    
    <!-- The vert.x routing configuration file for the http server -->
    <routing-config>routing-config.xml</routing-config>
</server-config>
```

Refer to [server.xsd](decl-vertx-config/src/main/resources/schema/server.xsd) to understand the schema.
Click [server.dtd](decl-vertx-config/src/main/resources/dtd/server.dtd) to view the dtd.

Again, we have specified the minimum attributes needed to configure an http server. Refer to [Appendix B: Server.xml](#appb) for advanced configuration.

The `http.server` verticle will read this configuration file to initialize and bring up the http server. An http server needs all the end points to be registered. And the file `routing-config.xml` defines all the routing.

<a name="sexr"/>

#### Add routing-config.xml

To define API Specification and Contract, create the `routing-config.xml`. This file will have all the routing details.

```
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

Refer to [routing-config.xsd](decl-vertx-config/src/main/resources/schema/routing-config.xsd) to understand the schema.

**At this stage your project setup is complete. Let's now create the required classes and deploy.**

<a name="sexf"/>

#### Finish Setup

Now we will write the java classes required for this project.

**1. TestHttpServerVerticle.java** is the http server verticle. Refer to `server.xml` above.

```
import org.javalabs.decl.container.spi.EmbeddedHttpServer;
import org.javalabs.decl.container.spi.HttpServerProvider;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHttpServerVerticle extends AbstractVerticle {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestHttpServerVerticle.class);
    
    private EmbeddedHttpServer server;
    
    @Override
    public void start() throws Exception {
        // Since this verticle starts an embedded http server, therefore get the vert.x "server.xml" file as below.
        String configFile = config().getString("config");
        
        server = HttpServerProvider.get().create(configFile);
        server.start(getVertx());
    }

    @Override
    public void stop() throws Exception {
        server.stop(0);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Stopped Http server verticle");
        }
    }
}

```

**2. TestWorkerVerticle.java** is the worker verticle. Refer to `server.xml` above.

```
package com.example.rest;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWorkerVerticle extends AbstractVerticle {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestWorkerVerticle.class);
    
    @Override
    public void start() throws Exception {
        String configFile = config().getString("config");
        
        // Read the config file and start this worker verticle.
    }

    @Override
    public void stop() throws Exception {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Stopped Test worker verticle");
        }
    }
}
```

**3. EmployeeHandler.java** is the handler class as defined in the `routing-config.xml` above.

```
package com.example.rest.handler;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import java.util.List;

public class EmployeeHandler {

    // All handler class must have a reference to the Vert.x instance.
    private final Vertx vertx;
    
    public EmployeeHandler(Vertx vertx) {
        this.vertx = vertx;
    }
    
    public void create(RoutingContext ctx) {
        Employee element = Json.decodeValue(ctx.body().buffer(), Employee.class);

	// Validate, enrich and store the employee object.
        // ....
        // ....

        ctx.response().setStatusCode(201).end();
    }
    
    public void modify(RoutingContext ctx) {
        final String id = ctx.pathParam("id");
        Employee element = Json.decodeValue(ctx.body().buffer(), Employee.class);

	// Validate and update the employee object.
        // ....
        // ....

        ctx.response().setStatusCode(200).end();
    }
    
    public void view(RoutingContext ctx) {
        final String id = ctx.pathParam("id");
        
        // Fetch the employee object by it's id.
        Employee emp = fetch(id);

        Buffer buffer = Json.encodeToBuffer(emp);
        ctx.response().setStatusCode(200).end(buffer);
    }
    
    public void viewAll(RoutingContext ctx) {
        List<Employee> emps = fetchAll();

        Buffer buffer = Json.encodeToBuffer(emps);
        ctx.response().setStatusCode(200).end(buffer);
    }
    
    public void remove(RoutingContext ctx) {
        final String id = ctx.pathParam("id");

        // Delete the employee
	delete(id);
	ctx.response().setStatusCode(204).end();
    }
}
```

**4. Employee.java** - The POJO

```
package com.example.rest.model;

import java.util.Date;

public class Employee {

    private Integer id;
    private String name;
    private Long salary;
    private Date createdOn;

    // Regular Getters and Setters

}
```

**5. Finally The Main Class**

```
package com.example.rest;

import org.javalabs.decl.vertx.container.DeclarativeVertx;

public class ExampleMain {
    
    public static void main(String[] args) throws Exception {
        DeclarativeVertx.start();
    }
}
```

<a name="sexr"/>

#### Run Your Test Application 

Compile the project using your favourite build tool and run. Once the server is started you will see the below log.

```
[main] INFO org.javalabs.decl.vertx.config.Routematic - Route:
(1) /api/v1/employees
	[POST]     true
	[PUT]  /:id  true
	[GET]  /:id  true
	[DELETE]  /:id  true
	[GET]     true

[vert.x-eventloop-thread-1] INFO org.javalabs.decl.vertx.container.VertxHttpServer - Started Http Server. Listening to port: 8080
[main] INFO org.javalabs.decl.vertx.container.VertxContainer - Deployment of verticle http.server is successful. Deployment Id: fc8af56e-c87b-4fe7-b307-ad50564ef2c1
[main] INFO org.javalabs.decl.vertx.container.VertxContainer - Deployment of verticle test.worker is successful. Deployment Id: 1bea6f29-ffcf-462e-bf7a-577c0b6db639

```

<a name="sexo"/>

#### Optional Container

If you want to have your initialization code, you can create your optional container class as well.

```
package com.example.rest;

import org.javalabs.decl.vertx.container.VertxContainer;

public class TestContainer extends VertxContainer {
    
    public TestContainer() {}

    @Override
    protected void preDeploy() {
        // Add pre initialization code for vertx.
    }

    @Override
    protected void postDeploy() {
        // Add post deployment code for vertx.
    }    
}

```

In which case, change the **main class** to below:

```
package com.example.rest;

import org.javalabs.decl.container.ContainerConfig;

public class ExampleMain {
    
    public static void main(String[] args) throws Exception {
        TestContainer container = new TestContainer();
        container.setup(new ContainerConfig());
    }
}
```

<a name="ser"/>

### Declarative Vert.x - Routing Example

<div align="justify">
While Vert.x is designed to be used with Verticles, it's possible to start an HTTP server without explicitly defining a Verticle by using the 
<b>vertx.createHttpServer()</b> method and then deploying it with a route handler within the main application execution. You do not need the
vertx-web.xml or server.xml in this case. However, you do need the <b>routing-config.xml</b> to define the mapping, and declarative vert.x will ensure
the mappings are added to the <b>Router</b> on the fly.
</div>
<br/>
Below are the steps to configure.

<a name="serx"/>

#### Add routing-config.xml

As usual, to define API Specification and Contract, create the `routing-config.xml`. This file will have all the routing details.

```
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

<a name="serw"/>

#### Write The Main Class

```
package com.example.rest;

import org.javalabs.decl.vertx.config.Routematic;
import org.javalabs.decl.vertx.config.parser.RoutingConfigParser;
import org.javalabs.decl.vertx.jaxb.RoutingConfig;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMain {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleMain.class);
    
    private HttpServer server = null;
    private final Routematic rmatic = new Routematic();
    
    public static void main(String[] args) throws Exception {
        ExampleMain ex = new ExampleMain();
        ex.run();
    }
    
    public void run() {
        Vertx vertx = Vertx.vertx();
        
        // Initialize the vert.x router.
        // The router is a core component that receives incoming HTTP requests and directs them to the appropriate handler.
        Router router = Router.router(vertx);
        router.route("/*").handler(BodyHandler.create(false).setBodyLimit(1024 * 1024));
        
        // Read the routing-config.xml and configure the handler(s).
        // Dynamic programming, no boiler-plate code.
        RoutingConfig rc = RoutingConfigParser.parser().read();
        
        // Add the routing path to the Vert.x router.
        rmatic.addPath(vertx, router, rc);
        
        server = vertx.createHttpServer();

        // In order to handle incoming HTTP requests a request handler must be set
        // on the HTTP server. This is normally done before starting the server.
        server.requestHandler(router);
        server.listen(8080);
        
        LOGGER.info("Started Http Server. Listening to port: {}", server.actualPort());
    }
}
```

<a name="serr"/>

#### Run Your Test Application

Compile the project using your favourite build tool and run. Once the server is started you will see the below log.

```
[main] INFO org.javalabs.decl.vertx.config.Routematic - Route:
(1) /api/v1/employees
	[POST]     true
	[PUT]  /:id  true
	[GET]  /:id  true
	[DELETE]  /:id  true
	[GET]     true

[vert.x-eventloop-thread-1] INFO com.example.rest.ExampleMain - Started Http Server. Listening to port: 8080
```

<a name="app"/>

## Handler Vs Lambda

<div align="justify">
In Vert.x REST, a `handler class` is a dedicated class that implements the Handler interface, providing a structured way to handle requests, while a `lambda expression` is a concise, inline function used to directly define the request handling logic within the routing code, often preferred for simpler operations due to its brevity and readability; both are used to process incoming requests but differ in terms of code organization and complexity.
</div>

### Key Differences

1. Code Structure:
A handler class is a separate class with a defined handle method, allowing for more complex logic with potential helper methods, while a lambda expression is a short, anonymous function embedded directly in the routing code. 
1. Readability:
For simple operations, lambda expressions are often considered more readable due to their concise syntax. 
1. Reusability:
A handler class can be reused across different routes if the logic is generic, while a lambda might be specific to a particular route. 

### When To Use A Handler Class

1. Complex logic:
When the request handling involves multiple steps, conditional checks, database interactions, or other intricate operations, creating a dedicated handler class provides better organization and maintainability.
1. Code separation:
If you want to separate the routing logic from the actual request processing, using a handler class can help maintain a clean separation of concerns. 

### When To Use A Lambda Expression

1. Simple operations:
For basic tasks like returning a static response, performing simple calculations, or basic data manipulation, a lambda expression can be a more concise and readable choice.
1. Inline logic:
When you want to define the request handling behavior directly within the route definition, using a lambda can improve code flow.

<a name="appa"/>

## Appendix A: Vertx-Web.xml

Configuration parameters of **vertx-web.xml**.
It has 3 sections:

- **context-listener**
  - Custom listener
- **context-params**
  - Standard attributes
  - file-system-options
  - metrics-options
  - tracing-options
  - event-bus-options
  - address-resolver-options
- **verticles**
  - Standard attributes
  - deploy-options
 
### 1. context-listener
  
Custom Context Listener. This will be invoked during the bootstrap of your application to perform any global initialization task.

### 2. context-params

#### 2.1 Default options
  
Tuning parameters for the Vert.x instance.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `event-loop-pool-size` | Default event loop pool size is twice the number of cores on the machine. You can override the value by specifying a number here. E.g., 2, 4, etc. | N | Number of cores |
| `worker-pool-size` | Define the worker pool size. An alternative way to run blocking code is to use a worker verticle. A worker verticle is always executed with a thread from the worker pool. | N | `10` |
| `blocked-thread-check-interval` | Configure the maximum time in millis an event loop can be blocked, which is useful for preventing long-running blocking operations from impacting the system's performance | N | `1000` |
| `max-event-loop-execute-time` | Configure the maximum time an event loop can be blocked, which is useful for preventing long-running blocking operations from impacting the system's performance. | N | `2000` |
| `max-worker-execute-time` | Setting dictates the maximum amount of time a worker thread can be blocked before being interrupted. | N | `60000` |
| `internal-blocking-pool-size` | Provides a thread pool for handling these blocking operations without blocking the event loop | N | `20` |
| `cluster-manager` | Custom cluster manager to be used for various functions | N |  |
| `ha-enabled`      | High Availability (HA) is enabled through this configuration. This ensures that your Verticles and other core components are deployed across a cluster of Vert.x nodes, providing resilience against node failures | N | `false` |
| `quorum-size`      | It refers to the minimum number of nodes required in a cluster to maintain a quorum for operations like HA (High Availability) and distributed data consistency | N | `1` |
| `ha-group`       | Set the high availability group name. | N | `__DEFAULT__` |
| `warning-exception-time` | It determines the threshold for triggering a stack trace when a thread is blocked | N | `5000` |
| `prefer-native-transport`| Set whether to prefer the native transport to the NIO transport. | N | `false` |
| `disable-tccl`      | The TCCL is a thread-local variable used to determine which classloader to use when loading classes for a given thread. | N | `false` |
| `use-daemon-thread`         | This setting will enable Vert.x to use daemon threads for Verticles, meaning they don't prevent the JVM from exiting when they are no longer running.  | N | `false` |
| `metric-class`         | Custom metric class | N | |

#### 2.2 file-system-options
  
Provides configuration options for how Vert.x interacts with the file system.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `class-path-resolving-enabled` | When set, Vert.x will attempt to load files from the classpath if they are not found on the filesystem. | N | `true` |
| `file-caching-enabled` | If true, files accessed through the classpath will be cached on the filesystem, even if class-path-resolving-enabled is false. | N | `true` |
| `file-cache-dir` | Specifies the directory where files from the classpath will be extracted and cached for faster access. This can significantly improve IO performance when accessing files packaged within the application | N | `java.io.tmpdir` |

#### 2.3 metrics-options
  
Vert.x uses a metrics SPI (Service Provider Interface) to provide metrics for various components like HTTP clients, servers, event bus, and pools. Micrometer and Dropwizard are examples of metrics implementations that Vert.x supports.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `enabled` | Indicates whether metric collection is enabled. | N | `false` |
| `factory` | Factory class for creating metric instance. | N |  |

#### 2.4 tracing-options
  
Used to configure distributed tracing behavior. They allow you to control how tracing is handled across different components of your application, such as HTTP servers and clients. Key aspects include the tracing policy (`PROPAGATE`, `ALWAYS`, or `IGNORE`), and the ability to create custom tracing options through a `VertxTracerFactory`.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `factory` | Factory class for creating metric instance. | N |  |

#### 2.5 event-bus-options
  
Allows customization of the Vert.x Event Bus. It's used to configure the default behavior of the event bus, which is a distributed messaging system within Vert.x.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `cluster-public-host` | The public host to advertise for clustering. If null, the same host as the cluster host will be used. | N |  |
| `cluster-public-port` | The public port to advertise for clustering. If -1, the same port as the cluster port will be used. | N | `0` |
| `cluster-ping-interval` | Refers to the frequency at which a node in a cluster checks for other cluster members. | N | `20000` |
| `cluster-ping-reply-interval` | It controls the timeout for receiving a reply after sending a ping message to other nodes in a Vert.x cluster. | N | `20000` |
| `accept-backlog` | Setting for the Event Bus determines how many pending client connections can be queued when the Event Bus is busy. | N | `1024` |
| `reconnect-interval` | It configures how long the Event Bus waits before attempting to reconnect after a connection failure. | N | `1000` |
| `reconnect-attempts` | In Vert.x, the EventBus offers automatic reconnection attempts after a disconnection. You can configure the reconnection behavior using this attribute to control how often to try reconnecting. | N | `1` |
| `connect-timeout`      | This setting on the Event Bus determines how long the Event Bus will wait for a connection to be established before timing out. | N | `60000` |
| `trust-all`      | It allows all consumers to receive messages published to any address. This means any Verticle can send a message to any other Verticle without explicit trust or authorization. | N | `true` |
| `log-activity`       | Whether logging will be enabled. | N | `false` |

#### 2.6 address-resolver-options
  
This setting is used to configure the behavior of the Vert.x DNS resolver. They allow you to specify DNS servers, configure cache TTL, and adjust other resolver settings.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `hosts-path` | Provides a way to configure the address resolution behavior, including specifying an alternative hosts file. | N | `/etc/hosts` |
| `hosts-refresh-period` | Controls how frequently the resolver checks for updates to the /etc/hosts file (or the configured hosts file). | N | `0` |
| `servers` | Allows you to specify a list of DNS server addresses that Vert.x will use when resolving hostnames to IP addresses. | N |  |
| `opt-resource-enabled` | Controls whether a DNS query includes an optional resource record (OPT RR) that hints to the DNS server about the resolver's capacity to handle large responses. | N | `false` |
| `cache-min-time-to-live` | Specifies the minimum time in seconds that a successfully resolved DNS address will be cached. | N | `0` |
| `cache-max-time-to-live` | Controls the maximum time (in millis) that a successfully resolved DNS address will be cached. | N | `2147483647` |
| `cache-negative-time-to-live` | Specifies the duration (in seconds) for which failed hostname resolutions are cached. During this time, subsequent attempts to resolve the same hostname will not trigger a DNS query, but instead, return the cached negative result, improving performance by reducing DNS traffic and speeding up responses for failed lookups. | N | `0` |
| `query-timeout`      | This setting determines the maximum amount of time (in milliseconds) that the address resolver will wait for a response to a DNS query before considering it a timeout. | N | `5000` |
| `max-queries`      | Allows you to configure the maximum number of queries sent during a hostname resolution. | N | `4` |
| `rd-flag`       | It controls the "Recursion Desired" (RD) flag in DNS queries. Setting it to true (the default) means the resolver will request recursive resolution from the DNS server, while false means it will not. | N | `true` |
| `search-domains`       | Search domains are used when resolving hostnames that are not fully qualified (i.e., they don't have a domain suffix). The resolver will append each search domain to the hostname until a valid IP address is found.. | N |  |
| `ndots`       | Controls how the resolver handles hostnames with dots when resolving DNS addresses using search domains. | N | `false` |
| `rotate-servers`       | This option, when set to true, enables round-robin selection of DNS servers from the configured list, distributing the load across them. | N | `false` |
| `round-robin-inet-address`       | It enables a round-robin selection of IP addresses when resolving a hostname to multiple addresses. This means that instead of always using the first IP address returned by the DNS server, Vert.x will cycle through the available IP addresses in a round-robin fashion, distributing the connection load across them.. | N | `false` |

### 3. verticles

#### 3.1 Standard attributes

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `name` | Unique name of the verticle. | Y |  |
| `class` | The verticle class. Must be a subclass of io.vertx.core.Verticle. | Y |  |
| `config` | Specify an optional configuration file that will be read by the specific Verticle instance. If the Verticle starts an embedded Http server, then provide the server configuration in a special file called `server.xml`. | Y |  |

#### 3.2 deploy-options
  
Deployment configuration for a Vertcle.

| Property  | Description   |   Mandarory   | Default    |
|-----------|---------------|---------------|----------- |
| `worker` | Determine whether a verticle is a worker verticle or not. A worker verticle in Vert.x is a type of verticle that executes its code on a thread from a worker thread pool, rather than on the event loop. This allows worker verticles to handle blocking operations without blocking the event loop, which is crucial for maintaining the responsiveness of the application. | N | `false` |
| `threading-model` | A vertical threading model is a method where a process (like an application) is divided into multiple threads, and each thread handles a distinct task within the application's execution. | N | `EVENT_LOOP` |
| `isolation-group` | It relate to how verticles are deployed and potentially share resources, especially classloaders. | N |  |
| `ha` | Indicates whether it's a high availability verticle. | N | `false` |
| `extra-classpath` | When a Verticle needs to access resources beyond the libraries that are part of the main Vert.x application, you use extra-classpath to provide these external dependencies during deployment. | N |  |
| `instances` | The number of instances of the verticle to instantiate in the Vert.x server. Each verticle instance is strictly single threaded so to scale your application across available cores you might want to deploy more than one instance | N | `1` |
| `worker-pool-name` | It allows you to specify which worker pool a verticle will use when running blocking or long-running code. When a worker pool name is set, the verticle will use that named worker pool, otherwise, it will use the default Vert.x worker pool. | N |  |
| `worker-pool-size`      | An alternative way to run blocking code is to use a worker verticle. A worker verticle is always executed with a thread from the worker pool. By default blocking code is executed on the Vert.x worker pool, configured with worker-pool-size. | N | `20` |
| `max-worker-execute-time`      | Setting dictates the maximum amount of time a worker thread can be blocked before being interrupted. By default, this is set to 60 seconds. This setting helps prevent blocking worker threads from impacting other verticles or event loops.  | N | `60000` |

<a name="appb"/>

## Appendix B: Server.xml

Configuration parameters of **server.xml**.
It has the following sections:

- **server-opts**
- **tcp-opts**
  - ssl-opts
- **network-opts**
- **http-opts**
  - initial-settings
- **cross-origin**
  - allowed-headers
  - allowed-methods
- **keystore-config**
- **truststore-config**
- **context-root**
- **access-log**
- **security-constraint**
  - auth-handler
  - auth-constraint
- **routing-config**
 
### 1. server-opts
  
Web server configuration parameters. All parameters are optional.

| Property  | Description   | Default    |
|-----------|---------------|----------- |
| `ssl` | Set the ssl parameter if you want your server to be started in ssl mode. Along with this you have to use a keystore containing the SSL certificate and private key. You also need to ensure the server is listening on the HTTPS port. | `false` |
| `host` | The host setting that determines which network interface the server will listen on. By default, the server listens on all available network interfaces (typically 0.0.0.0). | `0.0.0.0` |
| `port` | The port attribute, of type int, specifies the TCP port number that the server will listen for incoming connections. | `8080` |
| `accept-backlog` | It specifies the maximum number of pending connection requests that the server can queue before refusing new connections. It's essentially a limit on the number of incomplete TCP connections that the server will allow to accumulate. A higher acceptBacklog allows the server to handle more concurrent connection attempts, but setting it too high can lead to resource exhaustion if the server isn't processing connections quickly enough. | `-1` |
| `client-auth` | Configures the engine to require/request client authentication. The accepted values are: `NONE`, `REQUEST`, `REQUIRED`. | `NONE` |
| `sni` | The setting for Server Name Indication (SNI). SNI is a TLS extension that allows a client to specify which hostname it is trying to connect to when using HTTPS. This is crucial for hosting multiple HTTPS sites on the same IP address and port. | `false` |
| `use-proxy-protocol` | To use the proxy protocol with a Vert.x HttpServer, you need to enable it by setting this property. This allows the server to correctly interpret the PROXY protocol header, which is often used in proxy setups like HAProxy to pass client connection information to the backend server. | `false` |
| `proxy-protocol-timeout`      | It specifies the maximum time, in millis, that the server will wait for a PROXY protocol header from a client before considering the connection timed out. | `10000` |
| `register-write-handler`      | This is used to configure whether write handlers for server WebSockets should be registered by default. It doesn't directly relate to HTTP server options for general requests, but rather specifically to `WebSocket` connections | `false` |

### 2. tcp-opts
  
Configuration parameters for TCP protocol. All parameters are optional.

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `tcp-no-delay` | It controls whether the `TCP_NODELAY` socket option is enabled for the HTTP server. When enabled, it disables the Nagle's algorithm, potentially reducing latency for small packets at the cost of increased network overhead. When disabled (set to false), the `Nagle's Algorithm` is enabled, potentially improving network efficiency for many small packets. | `true` |
| `tcp-keep-alive` | This refers to the underlying TCP connection's keep-alive mechanism. It periodically sends probes to ensure the connection is still active and prevent it from being closed by intermediate network devices due to inactivity. | `false` |
| `so-linger` | It controls the socket's linger behavior when closing the connection. Specifically, it determines how long the socket waits to send any remaining unsent data before actually closing the connection. | `-1` |
| `idle-timeout` | This setting determines how long an HTTP connection can be idle before it's automatically closed. This setting helps prevent resource exhaustion by actively disconnecting connections that are not actively sending or receiving data. | `0` |
| `read-idle-timeout` | It determines the maximum time a server socket will remain idle (without receiving any data) before the connection is automatically closed. It's a mechanism to prevent idle connections from consuming resources indefinitely. | `0` |
| `write-idle-timeout` | It sets the maximum amount of time a connection can remain idle before being closed due to inactivity on the write side. It's a crucial setting for preventing idle connections from consuming resources indefinitely. | `0` |
| `tcp-fast-open` | Enable the `TCP_FASTOPEN` option - only with linux native transport. | `false` |
| `tcp-cork` | This option, when enabled, leverages the TCP_CORK option available in Linux native transports. This option, when set to true, instructs the kernel to delay sending TCP packets until a larger buffer is filled, or a timeout is reached. This can improve network performance in certain scenarios by reducing the number of small packets sent. | `false` |
| `tcp-quick-ack` | It allows the server to send TCP acknowledgments (ACKs) immediately, rather than waiting for the kernel's delayed ACK timer. This can improve network performance, particularly in scenarios with frequent small packets. | `false` |
| `tcp-user-timeout` | It controls the maximum time a TCP connection can be idle before being closed by the server. This is particularly useful for preventing resource exhaustion from inactive connections. | `0` |

#### 2.1 ssl-opts

Set the SSL Options for tcp.
  
| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `ssl-handshake-timeout` | It allows you to set a timeout for the SSL/TLS handshake process. This timeout defines how long the system will wait for a client to complete the handshake before considering it a failure. | `10000` |
| `key-cert-options` | This options are used within SSLOptions to configure the key and certificate information for SSL/TLS connections. Specifically, KeyCertOptions define how the server or client presents its identity during the SSL handshake. You'll typically use KeyStoreOptions or PemKeyCertOptions as implementations of KeyCertOptions to provide the necessary certificate and key data, usually in a keystore file or PEM format. |  |
| `trust-options` | This options are used to configure how the SSL/TLS layer trusts certificates during a connection. They specify the source of trusted certificates, which can be a keystore, a set of certificates, or a trust manager. |  |
| `enabled-cipher-suites` | This property, when not empty, determines which cipher suites are used for encryption during the SSL/TLS handshake. It overrides the default cipher suites provided by the SSL engine. |  |
| `use-alpn` | It controls whether or not the Application-Layer Protocol Negotiation (ALPN) extension for TLS is enabled. ALPN allows the client and server to negotiate which application-level protocol (like HTTP/2) to use over the secure connection. | `false` |
| `crl-paths` | It is used to specify the paths to Certificate Revocation List (CRL) files. These files are used to verify if a certificate presented by a server or client has been revoked, enhancing the security of SSL/TLS connections. | `0` |
| `crl-values` | It allows you to add a CRL value (represented as a Buffer) to the SSL options, enabling CRL checking during SSL/TLS handshakes. |  |
| `enabled-secure-transport-protocols` | It sets up the ssl transport protocols that are to be enabled for the current vertx server. | `TLSv1 TLSv1.1 TLSv1.2 TLSv1.3` |

### 3. network-opts
  
Network options for the http server. All parameters are optional.

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `send-buffer-size` | It controls the size of the TCP send buffer used by the HTTP server. This buffer is used to temporarily store data before it's sent over the network. Setting it appropriately can impact performance, especially under high load or when dealing with large responses. | `-1` |
| `receive-buffer-size` | It controls the size of the TCP receive buffer for the server. This buffer is used to store incoming data before it's processed by the Vert.x HTTP server. Setting a larger buffer size can potentially improve performance under high load, while a smaller buffer might be more appropriate for lower traffic scenarios. | `-1` |
| `reuse-address` | It controls whether a socket can be bound to an address that is already in use, allowing multiple sockets to listen on the same port. When enabled, it allows a new server instance to start even if a previous server instance on the same address is still in a TIME_WAIT state, which can occur after a server shuts down. | `true` |
| `traffic-class` | It allows you to set the traffic class (also known as "service class" or "DiffServ octet") for the underlying TCP sockets of an HTTP server. This option is related to the IP header's DSCP or ECN fields, which can be used to prioritize network traffic. | `-1` |
| `log-activity` | It enables logging of network activity for an HTTP server. When set to true, Netty's pipeline is configured to log network events at the DEBUG level on Netty's logger. To see these logs, you must also configure your logging framework (e.g., Logback, SLF4J) to enable DEBUG level logging for `io.netty.handler.logging.LoggingHandler`. | `false` |
| `reuse-port` | It allows you to enable or disable the reuse of the port for the HTTP server. When reusePort is set to true, the server will attempt to reuse the port even if it's in a TIME_WAIT state, which can be useful in certain scenarios like when restarting the server quickly after it was previously closed. | `false` |

### 4. http-opts
  
Http options for the web server. All parameters are optional.

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `compression-supported` | This allows the server to compress responses using gzip or deflate when a client indicates support for these methods in the Accept-Encoding header. | `false` |
| `compression-level` | This option  controls the level of compression used when the server compresses response bodies. This setting, ranging from 1 to 9, affects the trade-off between compression ratio and CPU usage. A higher level (closer to 9) provides better compression but consumes more CPU resources. The default value is 6, which is a common balance point. | `6` |
| `max-web-socket-frame-size` | It configures the maximum allowable size of a WebSocket frame in bytes. It defaults to 65536 (64KB). You can increase this limit. This setting prevents denial-of-service attacks by limiting the size of individual WebSocket messages. | `65536` |
| `max-web-socket-message-size` | It configures the maximum allowable size for a WebSocket message, including fragmented messages. The default value is 4 times the `max-web-socket-frame-size`. | `262144` |
| `handle-100-continue-automatically` |To enable automatic handling of the "Expect: 100-Continue" header in Vert.x, you need to set this option to true. This tells the Vert.x HTTP server to automatically respond with a "100 Continue" (HTTP 1.1) or "102 Processing" (HTTP/2) status code when a client sends an "Expect: 100-Continue" header, indicating that it is safe to send the request body. | `false` |
| `max-chunk-size` | It limits the maximum size of HTTP chunks received by the server. It's a safety mechanism to prevent excessively large chunks from overwhelming the server. | `8192` |
| `max-initial-line-length` | It sets the maximum allowed length for the initial line of an HTTP request (e.g., "GET /index.html HTTP/1.1"). This setting helps prevent denial-of-service (DoS) attacks by limiting the size of incoming request lines. | `4096` |
| `max-header-size` | It defines the maximum allowed size in bytes for HTTP headers (including the initial line) when a server receives an HTTP request. Setting this option prevents large or malicious header sizes from overwhelming the server. | `8192` |
| `max-form-attribute-size` | It configures the maximum allowed size for a single attribute in a form submission. It's a mechanism to prevent excessive memory consumption from overly large form field values. The default value for maxFormAttributeSize is 8192 bytes. If a form attribute exceeds this limit, the server will typically respond with an HTTP status code 413 (Request Entity Too Large). | `8192` |
| `max-form-fields` | It allows you to set the maximum number of fields allowed in a form when handling HTTP requests. This setting is crucial for preventing denial-of-service (DoS) attacks where malicious clients might send excessively large forms to overwhelm the server. By setting a reasonable limit, you can mitigate such risks. | `256` |
| `max-form-buffered-bytes` | It limits the amount of buffered bytes when decoding a form. This setting prevents excessive memory consumption by restricting the size of form data the server will buffer during request processing. | `1024` |
| `alpn-versions` | It allows you to configure the ALPN (Application-Layer Protocol Negotiation) versions for an HTTP/2 server. ALPN is a TLS extension that allows the server and client to negotiate which protocol (e.g., HTTP/1.1, HTTP/2) to use during the initial connection handshake. The alpnVersions option in HttpServerOptions enables you to specify the supported protocol versions that the server will advertise to clients. | `HTTP_2` `HTTP_1_1` |
| `http2-clear-text-enabled` | To enable HTTP/2 clear-text (h2c) support in Vert.x HttpServerOptions, you need to set thisoption to true. This allows the server to negotiate HTTP/2 without requiring TLS. | `true` |
| `http2-connection-window-size` | It allows you to configure various settings for an HTTP/2 server, including the connection window size. The http2ConnectionWindowSize setting controls the initial flow control window size for an HTTP/2 connection. This setting determines how much data the server can send to a client before waiting for the client to acknowledge receipt of the data. | `-1` |
| `decompression-supported` | It allows you to enable decompression for incoming requests. The attribute controls whether the server will automatically decompress request bodies that are compressed using gzip, deflate, or br (Brotli) encodings. By default, decompression is enabled. | `false` |
| `accept-unmasked-frames` | It allows you to configure whether the server accepts unmasked WebSocket frames. Setting this attribute to true enables the server to accept WebSocket frames that are not masked (i.e., have a mask key of all zeros). By default, Vert.x WebSocket servers will reject unmasked frames, as per RFC 6455, Section 5.1, which mandates masking for client-to-server frames. | `false` |
| `decoder-initial-buffer-size` | It configures the initial size of the buffer used by the HTTP decoder when processing incoming requests. The default value is 128 bytes, but you can adjust it to potentially improve performance when dealing with large headers or requests by setting it to a more appropriate size. | `128` |
| `per-frame-web-socket-compression-supported` | This allows you to specify whether the server should support per-frame WebSocket compression. | `true` |
| `per-message-web-socket-compression-supported` | It controls whether the server supports per-message WebSocket compression, which is a technique to reduce the size of messages sent over a WebSocket connection. The default value is true. | `true` |
| `web-socket-compression-level` | It allows you to configure the compression level for WebSocket connections. It's a value between 1 and 9, where higher values mean more compression but potentially slower performance, while lower values mean less compression but potentially faster performance.  | `6` |
| `web-socket-preferred-client-no-context` | It is used to configure various aspects of an HTTP server, including WebSocket behavior. This option, when set within HttpServerOptions, determines whether the server will accept the server_no_context_takeover parameter in the per-message deflate extension offered by the client during WebSocket connections. | `false` |
| `web-socket-allow-server-no-context` | It allows you to configure WebSocket behavior, including the handling of `server_no_context_takeover` parameter in per-message deflate compression. Setting this attribute to true enables the server to accept this parameter, indicating it won't maintain a context for deflate compression across messages. | `false` |
| `web-socket-closing-timeout` | It is used to configure how long the server waits for a WebSocket to close after receiving a close frame from the client. | `10000` |
| `tracing-policy` | It includes a tracingPolicy setting to control how tracing is handled for HTTP requests. The TracingPolicy enum has three options: `ALWAYS`, `IGNORE`, `PROPAGATE` | `ALWAYS` |
| `register-web-socket-write-handlers` | It controls whether write handlers for server WebSockets are registered by default. When set to true, the server will register handlers that allow writing data to the WebSocket connection. | `false` |
| `http2-rst-flood-max-rst-frame-per-window` | Set the max number of RST frame allowed per time window, this is used to prevent HTTP/2 RST frame flood DDOS attacks. | `200` |
| `http2-rst-flood-window-duration` | It defines the duration of the time window used for detecting and mitigating HTTP/2 RST frame floods. This setting, along with http2MaxRstFramePerWindow, helps prevent denial-of-service attacks that exploit the sending of excessive RST_STREAM frames.  | `30000` |

#### 4.1 initial-settings
  
| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `header-table-size` | Size of the header table. | `4096` |
| `push-enabled` | It controls whether the server supports HTTP/2 server push. If enabled, the server can proactively send resources to the client in anticipation of future requests, potentially improving performance, especially for static assets. | `true` |
| `max-concurrent-streams` | It sets the limit on the number of concurrent streams an HTTP/2 server allows for a single connection. This setting is crucial for managing resources and preventing denial-of-service (DoS) attacks, especially in HTTP/2 where multiplexing is common. | `4294967295` |
| `initial-window-size` | This Setting controls the initial flow control window size for HTTP/2 connections. This setting, which is an integer value, determines how much data a server can send before needing to wait for an acknowledgement from the client. Setting it to a larger value can improve throughput, especially for large responses, while a smaller value can help prevent a slow client from overwhelming the server. | `65535` |
| `max-frame-size` | It refers to the maximum allowed size for a single WebSocket frame, not the overall message size which can be composed of multiple frames. The default max-frame-size for WebSocket frames is 65536 bytes (64KB). If a larger frame is received, a CorruptedFrameException is thrown. | `16384` |
| `max-header-list-size` | It configures the maximum allowed size for the combined size of all HTTP headers in a request. This setting helps prevent denial-of-service attacks by limiting the amount of memory that can be consumed by a single request. | `8192` |

### 5. cross-origin

It describes the cross origin related headers and methods.

#### 5.1 allowed-headers

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `allowed-header` | Name of the headers that are allowed. E.g., `Authorization`, `X-Custom-Header`, `eTag`, etc. | |

#### 5.2 allowed-methods

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `allowed-method` | Name of the methods that are allowed. E.g., `GET`, `POST`, `DELETE`, etc. | |

### 6. keystore-config

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `store-name` | Name of the key store. It must be in `PKCS` format | |
| `store-password` | Password of the key store | |

### 7. truststore-config

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `store-name` | Name of the trust store. It must be in `PKCS` format | |
| `store-password` | Password of the trust store | |

### 8. context-root

A path prefix in a URL that identifies a specific web application deployed on a server. It's essentially the `folder` or `namespace` within the server where your application's resources reside.

### 9. access-log

It records all requests processed by an HTTP server. These logs contain valuable information such as timestamps, IP addresses, requested URLs, and response codes, which can be used for troubleshooting, security monitoring, and analyzing website usage patterns.

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `log-format` | The access log format. Must be a sub-class of `io.vertx.ext.web.handler.LoggerFormatter`. | |
| `route` | Define the comma separated path(s) for which access log will be enabled. | |

### 10. security-constraint

A security constraint is used to define the access privileges to a collection of resources using their URL mapping.

#### 10.1 auth-constraint

Specifies whether authentication is to be used and names the roles authorized to perform the constrained requests.

| Property  | Description   |   Default    |
|-----------|---------------|------------- |
| `auth-type` | Custom auth type. `NO_AUTH` is a special type of auth that indicates that authorization will be disabled for certain set of urls as specified in the `url-patterns` section. | |
| `url-patterns` | Define the url patterns. | |

### 11. routing-config

In Vert.x Web, a route defines a path and associated handler(s) for processing HTTP requests. Routers manage these routes, directing requests to the appropriate handler based on criteria like the HTTP method (GET, POST, etc.), path, or regular expression. Failure handlers are used to deal with errors during request processing This section contains the file that holds all the routing path and their corresponding handlers. The presence of this file is mandatory.

Refer to [Appendix C: Routing-Config.xml](#appc) for more details.

<a name="appc"/>

## Appendix C: Routing-Config.xml

Configuration parameters of **routing-config.xml**.
It has the following sections:

- **package**
- **api**
- **resource-mapping**
  - mapping

### 1. package

| Attribute  | Description   |   Mandatory | Default    |
|-----------|---------------|------------- |------|
| `name` | Package name to look for the `handler` classes. | Y | |

### 2. api

| Attribute  | Description   |   Mandatory | Default    |
|-----------|---------------|------------- |--------|
| `version` | The current version of the REST. | Y | |
| `basePath` | The base path, a.k.a the context root/namespace. | Y | |
| `produce` | Specifies the media types (also known as MIME types) that the client is able to understand and is willing to accept in the response from the server. This value  will be injected into the `Accept` header | |
| `consume` | It specifies the media type of the resource being transmitted in the body of an HTTP request or response. This is the `Content-Type` header. | Y | |

### 3. resource-mapping

| Attribute  | Description   |   Mandatory | Default    |
|-----------|---------------|------------- |--------|
| `name` | Name of the resource represented by this API end point. E.g., `Employee` | Y | |
| `path` | The base path to access the resource. E.g., `/employees` | Y | |
| `resource` | Name of the handler class that is mapped with this resource name. E.g., `com.example.rest.handler.EmployeeHandler` | Y | |
| `schema` | The fully qualified name of the employee POJO. | Y | |

#### 3.1 mapping

| Attribute  | Description   |   Mandatory | Default    |
|-----------|---------------|------------- |--------|
| `uri` | URI fragment | Y | |
| `method` | The Http method. Supported methods are `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS`, `CONNECT`  | Y | |
| `api` | Name of the method of the handler class that will be invoked whenever user hits the url as identified by `path` and `uri` fragment | Y | |


<a name="appd"/>

## Appendix D: Javadoc

Refer to [Declarative Vertx Javadoc](https://github.com/pages/sudiptasish/declarative-vertx/)
