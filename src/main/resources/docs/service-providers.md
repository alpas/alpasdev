- [Introduction](#introduction)
- [Writing Service Providers](#writing-service-providers)
    - [The `register` method](#register)
    - [The `boot` method](#boot)
    - [The `commands` method](#commands)
    - [Registering Service Provider](#registering)

<a name="introduction"></a>
### [Introduction](#introduction)

Service providers are like Lego blocks that come all together to bootstrap your Alpas web app.

At its core service providers are nothing more than just classes that allow you to register and boot your services 
in a more controlled way in one central place. They have few lifecycle methods that are used to register and bootstrap 
your bindings. Your service provider class is also where you return a list of commands available to be run from the
command line.

The core of Alpas framework itself is built with many service providers.

> /alert/ <span>Unlike [configs](/docs/configuration), service providers are not automatically registered by the 
> framework. It's upto you to register your service providers in either one or both of `HttpKernel` and `ConsoleKernel`.


<a name="writing-service-providers"></a>
### [Writing Service Providers](#writing-service-providers)

A service provider is just a class that implements `dev.alpas.ServiceProvider` interface:

<span class="line-numbers" data-start="3">

```kotlin
class ExceptionReportingServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        // Register a service.
        // not all the service providers are registered yet.
    }

    override fun boot(app: Application) {
        // Boot a service.
        // All the service providers are registered.
    }

    override fun commands(app: Application): List<Command> {
        // Return a list of Alpas console commands.
        // Called right after the register method.
        // Not all the service providers are registered yet.
    }
}
```

</span>

> /tip/ <span> `register` and `boot` methods are passed the global app instance, which is an 
> [IoC Container](/docs/ioc-container). Use this app instance to register your bindings or to resolve dependencies.
></span>

<a name="register"></a>
#### [The `register` method](#register)

The `register` method is where you should register your bindings into the [IoC Container](/docs/ioc-container). Since
not all the service providers are necessarily registered by the time this register method is called, you shouldn't
try to use any other bindings inside this method.

<a name="boot"></a>
#### [The `boot` method](#boot)

The `boot` method is called when all the providers have been registered (but not necessarily booted). You should be
able to use any other bindings provided by other service providers.

<a name="commands"></a>
#### [The `commands` method](#commands)

You service providers may have some commands that are available through the Alpas console. The `commands` method is the
perfect place to return a list of all those commands. This method is called right after the `register` method for
this service provider is called and so not all the service providers are registered yet.

<a name="registering"></a>
#### [Registering Service Provider](#registering)

Register your service provider by appending it to a list of service providers returned by `serviceProviders()` method
in either one or both of `HttpKernel` or 'ConsoleKernel' classes. Whether to register your service provider in one or
both the kernels depends on in what mode you want your service provider to be available. For an example, by default
`ViewServiceProvider` class is added to only `HttpKernel` and not to `ConsoleKernel` as it doesn't make sense to have
any view related services available in console mode.

Let's see a complete example of writing a service provider. Say we want to have an error reporter service that we
can call whenever we want to log an exception. Also, let's assume that it provides a console command `error:send` that
sends any cached errors immediately.

<span class="line-numbers" data-start="15">

```kotlin
class ErrorReportingServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        app.bind(RemoteErrorReporter())
    }

    override fun boot(app: Application) {
        app.make<RemoteErrorReporter>().prepare(app.env)
    }

    override fun commands(app: Application): List<Command> {
        return listOf(SendAndClearErrors())
    }
}

class RemoteErrorReporter {
    fun report(e: Exception) {
        // sends the error somehow
    }

    fun prepare(env: Environment) {
        // do something
    }
}

class SendAndClearErrors : Command(name = "error:send")

// Use the registered service
class AdminController : Controller() {
    fun all(call: HttpCall) {
        if (!call.isAuthenticated) {
            call.make<RemoteErrorReporter>()
                .report(AuthenticationException("Suspicious activity 😱"))
            return
        }
        call.reply("Hello, Admin!")
    }
}

// Register the service provider
// src/main/kotlin/HttpKernel.kt
class HttpKernel : HttpKernel() {
    override fun serviceProviders(app: Application): Iterable<KClass<out ServiceProvider>> {
        return listOf(
            ...,
            ...,
            ...,
            ErrorReportingServiceProvider::class,
            ...,
            ...
        )
    }
}
```

</span>
