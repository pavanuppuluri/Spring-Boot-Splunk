## Integrating Spring Boot with Splunk

Integrating **Spring Boot** with **Splunk** allows you to monitor, analyze, and visualize logs from your Spring Boot application in a Splunk instance. This integration helps in centralized logging and monitoring, providing valuable insights into the behavior and performance of your application.

### Steps to Integrate Spring Boot with Splunk

#### 1. Set up a Splunk Account and Splunk HTTP Event Collector (HEC) Token

1. **Sign up for a Splunk account**: If you don't have a Splunk account, you can sign up at [Splunk.com](https://www.splunk.com).
2. **Install Splunk Enterprise** or use **Splunk Cloud**.
3. **Enable the HTTP Event Collector (HEC)** in Splunk:
   - HEC allows you to send events (logs) directly to Splunk using HTTP POST requests.
   - To enable HEC:
     - Go to **Settings** → **Data Inputs** → **HTTP Event Collector** in the Splunk UI.
     - Click **New Token**, give it a name, then click **Next**.
     - Set the **Source type** as `json` and configure other settings as needed.
     - Save the token value. This token will be used for authenticating your HTTP requests to Splunk.

#### 2. Add Dependencies to Spring Boot Project

You can integrate Splunk logging into your Spring Boot application using the **Logback appender** for Splunk. One popular library for this purpose is the **logback-splunk appender**.

##### For Maven Projects:
Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.splunk</groupId>
    <artifactId>splunk-hec-logger</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 3.Configure Logback to Send Logs to Splunk

Once the dependency is added, you need to configure **Logback** (the default logging system in Spring Boot) to send logs to **Splunk** using the **HTTP Event Collector (HEC)**.

**Create or update** the `src/main/resources/logback-spring.xml` file with the following configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- Define the appender for Splunk HEC -->
    <appender name="SPLUNK" class="com.splunk.logging.HecAppender">
        <url>https://your-splunk-instance:8088</url> <!-- URL of your Splunk HEC endpoint -->
        <token>YOUR_SPLUNK_HEC_TOKEN</token> <!-- Your Splunk HEC Token -->
        <index>your_index</index> <!-- Optional: specify your Splunk index -->
        <source>springboot</source> <!-- Optional: specify your source -->
        <sourcetype>json</sourcetype> <!-- Set the sourcetype to JSON -->
        <event>json</event> <!-- Send events as JSON -->
        <timeStampPattern>yyyy-MM-dd'T'HH:mm:ss.SSSZ</timeStampPattern> <!-- Optional: Customize timestamp format -->
        <maxQueueSize>512</maxQueueSize> <!-- Optional: Configure max queue size -->
        <queueFlushInterval>1000</queueFlushInterval> <!-- Optional: Configure flush interval -->
    </appender>

    <!-- Configure root logger to log to console and Splunk -->
    <root level="INFO">
        <appender-ref ref="SPLUNK"/>
        <appender-ref ref="CONSOLE"/>
    </root>

    <!-- Optional: Log to console as well -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

## Explanation

- **`<url>`**: The URL of your Splunk instance’s HTTP Event Collector (HEC) endpoint.
- **`<token>`**: The HEC token you generated from the Splunk UI. This is used to authenticate requests sent to Splunk.
- **`<index>`**: The Splunk index where logs will be stored. This is optional and can be left out if you don’t need to specify a particular index.
- **`<source>`** and **`<sourcetype>`**: These define the source and sourcetype for the logs in Splunk. In this case, we specify `springboot` as the source and `json` as the sourcetype.
- **`<timeStampPattern>`**: Configures the timestamp format for your log events. You can customize this format to match your requirements.

---

## Configure Log Level and Logging Behavior

In the `logback-spring.xml` file, you can adjust the log level (e.g., INFO, DEBUG, ERROR) and specify different loggers for various Spring Boot components. For example:

```xml
<logger name="org.springframework" level="INFO"/>
<logger name="com.yourapp" level="DEBUG"/>
```

You can customize this section based on which classes or packages you want to log at different levels.

#### 4.Test the Integration

Now that you have configured your Spring Boot application to send logs to Splunk, 
you can start your application and check Splunk to ensure that logs are appearing correctly.

- Start your Spring Boot application.
- Generate some logs by accessing various endpoints or triggering events in your application.
- Go to your Splunk UI and search for your logs.
- You can search using the specified index or other fields you defined in the appender configuration.


## Optional: Use Spring Boot Actuator with Splunk

You can enhance the monitoring and health checks of your Spring Boot application by integrating Spring Boot Actuator with Splunk. Spring Boot Actuator provides production-ready features like health checks, metrics, and more, which can be sent to Splunk for advanced monitoring.

### Steps to Integrate Spring Boot Actuator with Splunk

1. **Add the Actuator Dependency**

   First, include the Spring Boot Actuator starter in your `pom.xml`:

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

2. **Configure the Actuator endpoint and metrics in application.properties:**
```properties
management.endpoints.web.exposure.include=health,metrics
management.metrics.export.splunk.enabled=true
```

For more advanced metrics and monitoring, you can configure more detailed logging for the Spring Boot Actuator and send that data to Splunk as well.

### Steps to Log Information in Your Spring Boot Microservice

#### Add SLF4J Logger in Your Class

In each class where you want to log information, you need to create an SLF4J logger instance. This logger will be used to log various levels of messages such as `INFO`, `DEBUG`, `WARN`, and `ERROR`.

Here’s an example of how you can add SLF4J logging to your Spring Boot microservice:

### Example Code:

```java
package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    // Creating an SLF4J logger instance
    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    @GetMapping("/hello")
    public String sayHello() {
        logger.info("Handling 'hello' request");  // Log an INFO message

        try {
            // Simulating some business logic
            String result = performBusinessLogic();
            logger.debug("Business logic result: {}", result);  // Log a DEBUG message
        } catch (Exception e) {
            logger.error("Error occurred in processing request", e);  // Log an ERROR message
        }

        return "Hello, Spring Boot with Splunk!";
    }

    // Example method to simulate business logic
    private String performBusinessLogic() throws Exception {
        logger.info("Performing business logic...");
        // Simulate a delay or business computation
        Thread.sleep(500);
        return "Business logic completed successfully!";
    }
}
```
