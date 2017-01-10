# NodeAgent 2

## Introduction 

NodeAgent2 is a piece of middleware invoked by Ant handler tasks from ORCA on one side, and speaks the substrate native APIs on the other.  The invocation happens over a remotable interface, making it possible for the ORCA AM invoking the task to be on one host and the target of the tasks, where NA2 executes, on another.

NodeAgent2 was developed for two purposes:

 * Simplify working with self-scheduling substrates (like OSCARS and NSI), i.e. those that have their own scheduler that may require 'meter-feeding' so long as ORCA reservation is open. 
 * Simplify the development of new handlers based on large number of dependencies that don't work inside ORCA code.

NA2 exposes a RESTful interface and allows to perform standard handler operations (join/leave/modify) using property dictionaries passed in and out of each call. In addition it has the ability to self-schedule renew operations on reservations it has created, thus allowing it to operate on e.g. OSCARS and NSI and without requiring ORCA to perform an explicit *renew* call via a handler. 

NA2 has persistence, thus allowing it to be shutdown and restarted without losing state. 

For securing its remote API it implements HTTP Digest authentication with a single user 'admin' and the password set via the XML configuration file. SSL/TLS server-side support is optional. The provided example Spring configuration file shows how to enable it, however it is not mandatory, especially if NA2 is run on the same host as the ORCA AM and listens only on the local interface.

NA2 is designed to operate with any number of plugins implementing the interface to the particular substrate (join/leave/modify/renew operations). Plugins are mapped to API endpoints, such that same plugin can be mapped to multiple endpoints, if desired. Plugins are provided as self-contained jars and are configured via NA2 configuration file. 

## Implementation 

NodeAgent2 is implemented as a Spring Boot application with JPA/Hibernate persistence support provided through the included HSQLDB that by default is configured as a set of files, not a remote server. This is the recommended way of running NodeAgent2 unless database scalability somehow becomes an issue. 

NA2 plugin implementations are required to fulfill a minimal set of dependencies provided as orca.node-agent2.agentlib Maven artifact. 

## Principle of operation 

Each plugin must implement this [Plugin interface](agentlib/src/main/java/org/renci/nodeagent2/agentlib/Plugin.java) supporting the basic join/leave/modify/renew commands. A plugin is associated with a REST endpoint corresponding to the particular substrate instance. A plugin is configured with scheduling information: the reservation duration used when the reservation is created on the first join call and all consecutive renew calls. The NA2 runs a periodic thread to check for expired reservations, executed every 'tick'. For each plugin it is possible to configure how many ticks ahead of its renew deadline the reservation should be renewed (defaulting to one tick). 

After each 'join' operation, the NA2 inserts into its schedule the next deadline, a pre-configured number of ticks ahead of the actually provisioned deadline, when this reservation must be renewed (for plugins whose period is configured to be 0 this step is skipped). While the corresponding ORCA reservation remains Active, NA2 continues to renew the substrate reservation at the preconfigured period. Calling a leave on the reservation removes it from the renew schedule. The schedule is persistent, i.e. NodeAgent can be restarted without loss of state.

If join operation detects an error, it is returned to ORCA, however no schedule update is performed under the assumption that a reservation that failed to join cannot be renewed. Similarly, if an error is returned by the renew call, the reservation is taken out of the schedule (no future renews are performed) and the error is logged. There is an assumption that the status command implemented by the plugin can return from the substrate the more detailed error message about the reservation if desired.

Each plugin call is associated with a set of properties and also returns a set of properties, which are represented as String-to-String maps. Each schedule entry saves two separate sets of properties associated with each reservation:
 * Original properties passed with the join call (inProperties)
 * Properties *returned* by the join call (joinProperties). 

Note that after each *renew* call the properties returned by this call are merged with the existing joinProperties and saved into the schedule. They are then passed on to the next renew call. This allows break/make type renew operations in substrates lacking  a native renew capability, where a new reservation must be created on each renew call to preserve the illusion of a continuous reservation. 

Join operation is assumed to generate a unique *reservation id* string which from then on is used to refer to the reservation. This reservation id is explicitly returned by the join call and is used as a parameter to modify, leave and renew calls. Reservation id cannot change during the lifetime of the reservation. Break/make renew implementations must find a way to pass a new reservation identifier as part of the properties. 

## Configuration

Several configuration files are required for NA2:
 * Spring configuration file in YAML or as properties (like [this example](agent/server/na2-spring.yml) this example)
   * NA2 is pointed at this file using environment variable SPRING_CONFIG_LOCATION like so:
```
export SPRING_CONFIG_LOCATION=/path/to/config/file/na2-spring.yml
```
  * It is possible to use a properties file instead of a YAML file. Simply change the value of the environment variable above to point to the properties file. As per Spring documentation the conversion between YAML and properties file is straightforward: a property some.property.name=value becomes YAML property
```
some:
  property:
    name: value
```

 * LOG4J configuration file (it is pointed to from the Spring configuration file using property logging.config). This is a properties file, something like [this example](agent/server/log4j-na2.properties).
 * NA2 configuration file - this is an XML file that describes the plugins that NA2 is aware of and their individual properties. Like [this example](agent/server/na-test-config.xml).
  * The *name* of the plugin in the configuration also corresponds to the REST endpoint used for this substrate instance. Notice it is legal to configure the same jar for multiple endpoints. The name must conform to this simple pattern "[a-zA-Z0-9-]+" and be at least 5 characters long.
  * You can look at the full [schema](agent/config/src/main/resources/orca/nodeagent2/agent/config/xsd/AgentConfig.xsd) of the configuration file. Any changes to the schema are automatically turned into beans by the build process using JAXB. 

## REST interface details 

NA2 exposes a RESTful interface that combines mapping to plugin operations and status queries. All parameters are passed and results are returned as JSON objects: 

 * http(s)://hostname:port/join/[plugin name] (POST) - maps to the *join* operation implemented by the jar associated with this endpoint
 * http(s)://hostname:port/leave/[plugin name]/[reservation id] (POST) - maps to the *leave* operation implemented by the jar associated with this endpoint
 * http(s)://hostname:port/modify/[plugin name]/[reservation id] (POST) - maps to the *modify* operation implemented by the jar associated with this endpoint
 * http(s)://hostname:port/status/[plugin name]/[reservation id] (GET) - maps to the *status* operation implemented by the jar associated with this endpoint
 * http(s)://hostname:port/description/[plugin name] (GET) - returns the optional description provided in the configuration file
 * http(s)://hostname:port/plugins (GET) - returns configuration information about all known plugins
 * http(s)://hostname:port/plugins/[plugin name] (GET) - returns configuration information about the specific plugin
 * http(s)://hostname:port/schedule (GET) - returns the complete schedule information stored in the database
 * http(s)://hostname:port/schedule/[plugin name] (GET) - returns the schedule information for the particular plugin
 * http(s)://hostname:port/schedule/[plugin name]/[reservation id] (GET) - returns the schedule information for the particular reservation of the specified plugin

There are examples of using [pure Java](client/src/main/java/orca/nodeagent2/client/RestClient.java) to communicate with this API (using minimal Java dependencies) as well as examples of using [curl](https://github.com/RENCI-NRIG/na2-oscars-plugin/tree/master/scripts) to do the same. 

## ORCA Interface

ORCA now includes additional Ant tasks that implement the join/leave/modify calls on NA2 with property dictionaries passed in and out of each call. Their implementations can be found [here](https://github.com/RENCI-NRIG/orca5/tree/master/handlers/nodeagent2/src/main/java/orca/handlers/nodeagent2). The principle of operation is simple: the handler can invoke NA2's join, leave or modify calls like this:

```
<nodeagent2.join baseUrl="${na2.url}" prefix="oscars" returnPrefix="oscars.return" password="${na2.password}" plugin="${na2.plugin}" statusProperty="code" errorMsgProperty="message" reservationIdProperty="oscars.join.reservation" />
```

Each task requires the same parameters:
 * URL of the NA2
 * plugin name (the final portion of the name of the REST endpoint above)
 * prefix of properties that should be passed in this call (all properties starting with this prefix will be sent to the NA2 plugin). E.g. a property name 'oscars.blah' will be sent to the task. 
 * prefix or properties that will be returned. Any property 'some.property.name' will be returned as ${returnPrefix}.some.property.name 
 * name of the property which will contain the status code upon return
 * name of the property which will contain the error message upon return
 * name of the property which will contain the reservation id for the resource upon return

Note that renew cannot be called externally - it is intended to be used internally by NA2. 

The full example of how to use them can be found in the [OSCARS Handler] (https://github.com/RENCI-NRIG/orca5/tree/master/handlers/oscars/resources/handlers/oscars/handler.xml). 

Note that each target in the handler (join, leave or modify) must have this as the first statement of the target code:
```
<taskdef resource="orca/handlers/nodeagent2/nodeagent2.xml" classpathref="run.classpath" loaderref="run.classpath.loader" />
```

## Developing new plugins 

### Dependencies 
Developing new plugins is straightforward. Each new plugin must implement a class following the [Plugin interface](agentlib/src/main/java/orca/nodeagent2/agentlib/Plugin.java)  in the  orca.node-agent2.agentlib Maven artifact:
```
<repositories>
  <repository>
    <id>geni-orca-snapshot</id>
    <url>http://ci-dev.renci.org/nexus/content/repositories/geni-orca-snapshot/</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
  <repository>
    <id>geni-orca-libs</id>
    <url>http://ci-dev.renci.org/nexus/content/repositories/geni-orca-libs</url>
  </repository>
</repositories>
...
<dependency>
  <groupId>orca.node-agent2</groupId>
  <artifactId>agentlib</artifactId>
  <version>${na2.version}</version>
  <type>pom</type>
</dependency>
```

while can be built from source under node-agent2/agentlib/ or, alternatively, fetched from [NRIG Nexus] (http://ci-dev.renci.org/nexus/content/repositories/geni-orca-snapshot repository). 
Each plugin jar must contain all of its dependencies, thus it is recommended to use Maven's 'jar-with-dependencies' plugin to generate it. For more complex scenarios you may need to use the Maven shade plugin. For an example of its use look at the [OSCARS NA2 plugin](https://github.com/RENCI-NRIG/na2-oscars-plugin/blob/master/pom.xml).

Example plugin implementations can be found in:
 * [Null-Agent](null-agent/src/main/java/orca/nodeagent2/null_agent/Main.java) - a trivial implementation that does nothing and has minimal internal dependencies. Uses jar-with-dependencies Maven plugin.
 * [OSCARS Plugin](https://github.com/RENCI-NRIG/na2-oscars-plugin) - a full implementation of OSCARS API v06 plugin that relies on CXF and for this reason needs Maven shade plugin for assembling the uber jar.
 * [Exec Plugin](https://github.com/RENCI-NRIG/na2-exec-plugin) - a plugin that executes external programs for each of the join/leave/modify actions

### Classloading

NA2 implements a special [ParentLast classloader] (nodeagent2/agent/core/src/main/java/orca/nodeagent2/agent/core/ChildFirstURLClassLoader.java) , which forces code running in the plugin to consult its own classpath before defaulting to the NA2 classloader path, thus any plugin class dependencies specified will be linked from the jars in the plugin instead of classes on the main NA2 classpath. 

### Locking

NA2 automatically serializes renew/leave/modify/status requests *for a given reservation* i.e. if a reservation is being renewed and a leave call comes, the renew will complete before leave is invoked). Other types of locking are left to the implementors of the plugins. For example, the OSCARS plugin calls are internally fully synchronized due to the limitations of OSCARS service.

## Building and Running NA2  from source 

 * make sure you have Java 7 and Maven 3.x
 * check out NodeAgent2 from from GitHub
 * run in top-level directory
```
$ mvn clean install
```
 * then descend into agent/server
 * run
``` 
$ mvn clean package
```
 * make sure the configuration files are present and SPRING_CONFIG_LOCATION environment variable points at the Spring configuration file
 * run the NA2
```
$ sh target/appassembler/bin/nodeagent2
```
 * any plugin is typically built using
```
$ mvn clean package
```

## Packaging notes

The current implementation makes no assumptions about the preferred locations of the configuration files and the database. Everything starts with pointing the executable at the Spring configuration file using the SPRING_CONFIG_LOCATION environment variable. The NA2 server executable is packaged as a Maven appassembler package under /agent/server/target/appassembler. The daemon is under agent/server/target/generated-resources/appassembler/jsw/na2d/.

For clean restart, remove the database configured in the configuration file (all files and directories starting with the common name given in the spring.datasource.url property). This wipes out the schedule database and the agent will recreate it fresh when next restarted. 

There are no assumptions made about the locations of the plugin jars. Their fully-qualified locations are expected to be specified in the NA2 configuration file. Since there can be multiple independently developed plugins running under a single NA2, it is preferred to package plugins as individual RPMs consisting of a jar file in a pre-defined location (e.g. /etc/na2/plugins). 
