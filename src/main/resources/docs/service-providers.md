- [Getting Started](#getting-started)
    - [`register()`](#register)
    - [`boot()`](#boot)
    - [`commands()`](#commands)
- [Registering Service Provider](#registering)

At its core, service providers are nothing more than just classes that allow you to register and boot your services
in a more controlled way in one central place. They have few lifecycle methods that you can hook into to register
and bootstrap your bindings.

Your service provider class is also where you declare Alpas console commands that are meant be to run from the
command line.

The core of Alpas framework itself is built with many service providers.

> /alert/ <span>Unlike [configs](/docs/configuration), service providers are not automatically registered by the 
> framework. It's up to you to register your service providers in either one or both of `HttpKernel` and `ConsoleKernel`.


<a name="getting-started"></a>
### [Getting Started](#getting-started)

A service provider is just a class that implements `dev.alpas.ServiceProvider` interface:

<span class="line-numbers" data-start="3">

```kotlin

// providers/ExceptionReportingServiceProvider.kt
class ExceptionReportingServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        // Register a service.
        // Not all service providers are registered yet.
    }

    override fun boot(app: Application) {
        // Boot a service.
        // All service providers are registered.
    }

    override fun commands(app: Application): List<Command> {
        // Return a list of Alpas console commands.
        // Called right after the Register method.
        // Not all the service providers are registered yet.
    }
}

```

</span>

> /tip/ <span> All service provider methods receives the global app instance, which is an
[IoC Container](/docs/ioc-container). Use this app instance to register your bindings
or to resolve dependencies.</span>

<div class="sublist">

<a name="register"></a>
- `register()`

The `register` method is where you should register your bindings with the [IoC Container](/docs/ioc-container).
Since not all the service providers are necessarily registered by the time this register method is called,
you shouldn't try to use any other bindings inside this method.

<a name="boot"></a>
- `boot()`

The `boot` method is called when all the providers have been registered (but not necessarily booted). You should be
able to use other bindings provided by other service providers inside this method.

<a name="commands"></a>
- `commands()`

Your service providers may have some Alpas console commands. The `commands` method is the perfect place
to return a list of all those commands. This method is called right after the `register` method so
not all the service providers are registered yet.

</div>

<a name="registering"></a>
### [Registering Service Provider](#registering)

Once you have created a service provider, you must register it by appending it to the list of service providers
returned by `serviceProviders()` method in either one or both of `HttpKernel` or `ConsoleKernel` classes.

Whether to register your service provider in one or both the kernels depends on what mode you want your service
provider to be available in. For an example, by default the `ViewServiceProvider` class is added to only
`HttpKernel` and not to `ConsoleKernel` as it doesn't make sense to have any view related
services available in console mode.

Let's see a complete example of writing a service provider. Say we want to have an error reporter service that we
can call whenever we want to log an exception. Also, let's assume that it provides a console command `error:send` that
sends any cached errors immediately.

<span class="line-numbers" data-start="1">

```kotlin

// providers/ErrorReportingServiceProvider.kt
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

// RemoteErrorReporter.kt
class RemoteErrorReporter {
    fun report(e: Exception) {
        // sends the error somehow
    }

    fun prepare(env: Environment) {
        // do something
    }
}

// console/commands/SendAndClearErrorsCommand.kt
class SendAndClearErrorsCommand : Command(name = "error:send")

```

</span>

Register the service provider

<span class="line-numbers" data-start="8">

```kotlin

// HttpKernel.kt
class HttpKernel : HttpKernel() {
    override fun serviceProviders(app: Application): Iterable<KClass<out ServiceProvider>> {
        return listOf(
            // ...,
            // ...,
            // ...,
            ErrorReportingServiceProvider::class,
            // ...,
            // ...
        )
    }
}

```

</span>

Use the registered service

<span class="line-numbers" data-start="6">

```kotlin

// controllers/AdminController.kt
class AdminController : Controller() {
    fun greet(call: HttpCall) {
        if (!call.isAuthenticated) {
            val ex = AuthenticationException("Suspicious activity 😱")
            call.make<RemoteErrorReporter>().report(ex)
            call.abort()
        } else {
            call.reply("Hello, Admin!")
        }
    }
}

```

</span>

