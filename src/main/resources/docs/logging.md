- [Configuration](#configuration)
- [HttpCall Logger](#httpcall-logger)
- [Dedicated Logger](#dedicated-logger)
    - [Configuring a Dedicated Logger](#configuring-a-dedicated-logger)

Alpas uses Simple Logging Facade, [SLF4J](http://www.slf4j.org/), internally for capturing log messages. This allows 
you to use any implementation of SLF4J of your choice in your own web app. The log files are kept under `storage/logs` 
folder. You can easily configure the location and the name of the output file if you want. However, we recommend 
keeping it the way it is.

When scaffolding a new project, Alpas sets it up to use the venerable [Logback](http://logback.qos.ch/) library. 
You can replace it with any other library but given its power and flexibility, Logback is what we use and recommend. 

<a name="configuration"></a>
### [Configuration](#configuration)

Alpas does away with the traditional way of configuring your app using xml and properties files. However, configuring
of logging is done using couple of xml files. To make it convenient, two xml configuration files for Logback
is created for you when you scaffold a new project. Make sure to copy these files to your production server esp. if 
you are deploying using a fat JAR.

<div class="sublist">

- `app_log_config.xml`

This config file is for when your web app is serving HTTP requests. By default, all the error logs are printed 
to *stdout*, all the internal *info* logs are appended to the log file, and all the warnings from an `HttpCall` 
are appended to the log file as well.

Also, a `TimeBasedRollingPolicy` is configured for you, which performs the daily rollover of your logs and keeps 10
days' worth of history capped at 50MB of total log size. This means your old logs get deleted either after 10 days or
once the combined log files size reaches 50MB, whichever comes first. You are free to configure as per your
requirement, of course!

- `console_log_config.xml`

This config file is used when you are running Alpas console commands. To reduce clutter, only the errors are logged 
to *stdout*, everything else is ignored.

</div>

<a name="httpcall-logger"></a>
### [HttpCall Logger](#httpcall-logger)

Every `HttpCall` gets its own instance of a logger. Most of the times you' use this logger to log a message. There are
different levels of logging - `trace`, `debug`, `info`, `warn`, and `error` and each level has its corresponding
methods for actual logging of a message. You can either log a string message or a `Throwable` object. You can also
log an object of type `Any?` for which the return value of `toString()` will be logged.

<span class="line-numbers" data-start="5">

```kotlin

fun index(call: HttpCall) {

   call.logger.warn("This is a warning!")
   // or lazy log a message. Useful for logging expensive objects as they are 
   // only called if logging is configured to actually log the message.
   call.logger.warn { "This is a warning!" }
    
   call.abort(412, "Something is wrong!")
}

```

</span>

<a name="dedicated-logger"></a>
### [Dedicated Logger](#dedicated-logger)

If instead of using the logger from `HttpCall` doesn't work for you, you can easily create a new instance of a logger
by `val myLogger = KotlinLogging.logger {}`. This creates an instance of `KLogger` the name of which is set to the name
of the class declaring it. If you want to give it your own name to this logger, you could do something like:
`val myLogger = KotlinLogging.logger("myname")`

<a name="configuring-a-dedicated-logger"></a>
#### [Configuring a Dedicated Logger](#configuring-a-dedicated-logger)

Let's say you have a dedicated logger `val myLogger = KotlinLogging.logger {}` inside a class `com.example.LogTest`. 
You could configure this logger by doing something like the following in `app_log_config.xml` file:

```xml

<configuration scan="true">
    ...
    ...

    <logger name="com.example.LogTest" level="error">
        <appender-ref ref="STDOUT"/>
    </logger>

    <logger name="com.example.LogTest" level="warn">
        <appender-ref ref="FILE"/>
    </logger>
</configuration>

```

With the above configuration, the error log messages will be printed to `stdout` and warnings are appended to the
`storage/logs/alpas.log` file.

> /power/ <span>Under the hood, Alpas utilizes [kotlin-logging](https://github.com/MicroUtils/kotlin-logging), which
> is very performant logging library that wraps slf4j with Kotlin extensions. 
