- [Introduction](#introduction)
- [Binding Dependencies](#binding)
    - [Simple Bindings](#simple-bindings)
    - [Singleton Bindings](#singleton-bindings)
    - [Instance Bindings](#instance-bindings)
    - [Abstract Bindings](#abstract-bindings)
    - [Factory Bindings](#factory-bindings)
- [Resolving Dependencies](#resolving)
    - [Constructor Injection](#constructor-injection)
    - [Requesting Dependency using `make`](#make-resolve)
- [HttpCall DI Container](#httpcall-container)

<a name="introduction"></a>
### [Introduction](#introduction)

Dependency Injection allows you to manage your dependencies in a much cleaner way than creating and wiring them 
yourself. Once you have setup the bindings, you could have the dependencies injected into your class via the 
constructor.

The dependencies don't always need to be get injected in your class via a constructor. If you have access to the
container that holds all the bindings, you could simply ask for an instance of it. Depending on how you have done the
bindings, you could either get a new copy every time you ask or could get a singleton instance of the dependency.

The proper use of dependency injection frees you from the hassle of wiring all the dependencies as well as makes
testing easy.

Here is an example of a config class where a singleton instance of `Environment` class gets injected
automatically:

```kotlin
// src/main/kotlin/configs/AdminConfig.kt
class AdminConfig(env: Environment) : Config {
    val adminEmail = env("admin_email", "admin@example.com")
}
```

In the following example, nothing gets injected in the constructor. We instead ask an instance of `HttpCall` to make 
us an instance of `AdminConfig`. This is possible because `HttpCall` is a container and hence it is very capable of 
resolving dependencies.

```kotlin
// src/main/kotlin/controllers/AdminController.kt
class AdminController : Controller() {
    fun show(call: HttpCall) {
        val adminConfig = call.make<AdminConfig>()
    }
}
```

<a name="binding"></a>
### [Binding](#binding)

Binding is nothing more than putting a dependency into a container mentioning how you want to have it accessed. All you
need is the container that can be accessed from somewhere else so that dependency could be asked.

More often than not, you will be doing bindings from within [service providers](#/docs/service-providers) and service 
providers are always injected the global app instance, which is a container itself. Use this app instance to register 
your dependencies.

<a name="simple-bindings"></a>
#### [Simple Bindings](#simple-bindings)

Register a binding on a container using the `bind` method and pass the java classname of the class you are trying 
to bind.

```kotlin

   // in one of your service providers
   override fun register(app: Application) {
        app.bind(HttpClient::class.java)
        app.bind(StripePaymentProcessor::class.java)
    }

    class StripePaymentProcessor(client: HttpClient) {
        init {
            println("StripePaymentProcessor instance Created")
        }
    }

    class HttpClient {
        init {
            println("HttpClient instance Created")
        }
    }
```

After above bindings when you ask for `StripApi`, you'd get a new copy of it every time with a new copy of 
`HttpClient` injected to `StripePaymentProcessor` constructor as well.


<a name="singleton-bindings"></a>
#### [Singleton Bindings](#singleton-bindings)

To get one single shared instance of a dependency, you could use container's `singleton` method. A singleton dependency
gets resolved only once.

```kotlin
   override fun register(app: Application) {
        app.singleton(StripePaymentProcessor::class.java)
    }

    class StripePaymentProcessor {
        init {
            println("StripePaymentProcessor instance Created")
        }
    }
```

<a name="instance-bindings"></a>
#### [Instance Bindings](#instance-bindings)

If you already have an object instance and want to bind that instance, you could just use container's `bind` method
for binding this object. This instance will always be returned when asked for it. In a way this is like a singleton 
binding without auto-injecting the dependencies of this instance that you are binding, which is upto you.

```kotlin
   override fun register(app: Application) {
        val processor = StripePaymentProcessor()
        app.singleton(processor)
    }
```

<a name="abstract-bindings"></a>
#### [Abstract Bindings](#abstract-bindings)

Instead of binding a concrete class, you could also bind a concrete implementation to its abstract class or its 
interface instead. This way you don't have to depend on a specific implementation of a class but only on the "api" 
that you need from a class. When resolving you would refer to the abstraction instead of the concrete implementation,
of course!


```kotlin
   override fun register(app: Application) {
        app.bind(PaymentProcessor::class.java, StripePaymentProcessor::class.java)
    }

    interface PaymentProcessor {
        fun process()
    }

    class StripePaymentProcessor : PaymentProcessor {
        override fun process() {
            println("Payment processed through Stripe")
        }

        init {
            println("StripePaymentProcessor instance Created")
        }
    }
```

> /tip/ <span>Even with interface bindings you could either do [Simple Bindings](#simple-bindings), 
> [Singleton Bindings](#singleton-bindings), or [Instance Bindings](#instance-bindings) based on whether you want a 
> new copy, a shared single copy, or an object instance.</span>


<a name="factory-bindings"></a>
#### [Factory Bindings](#factory-bindings)

Instead of binding a class name or an instance, you could also bind a callback function that gets invoked every time 
a dependency needs to be resolved. Just remember that the actual binding that gets resolved is whatever the last 
statement of this factory callback is.

```kotlin
   override fun register(app: Application) {
        app.bindFactory {
            println("StripePaymentProcessor factory binding called")

            // This is the actual binding. In this case we are returning 
            // a new instance of StripePaymentProcessor every time this 
            // factory callback gets invoked.
            StripePaymentProcessor()
        }
    }

    class StripePaymentProcessor {
        init {
            println("StripePaymentProcessor instance Created")
        }
    }
```

> /alert/ <span>Be careful binding "mutating" objects with the app container. Since these bindings will be shared
 with all the incoming requests, if the bindings mutate their states, you might run into race conditions. For these
kinds of mutating global objects it is upto you to guarantee the thread safety of your app by taking appropriate 
measures.</span>

<a name="resolving-dependencies"></a>
### [Resolving Dependencies](#resolving-dependencies)

There are two ways to resolve a dependency. Via constructor injection or using `make` method.

<a name="constructor-injection"></a>
#### [Constructor Injection](#constructor-injection)

If a class depends on some other classes, it could just "ask" for the dependencies in its constructor to get them 
automatically injected. As long as this class itself is registered in the container, the container will be able to
resolve the dependencies including all the transitive dependencies.


```kotlin
   override fun register(app: Application) {
        app.bind(Logger::class.java)
        app.bind(HttpClient::class.java)
        app.bind(StripePaymentProcessor::class.java)
    }

    // an instance of HttpClient get automatically injected
    class StripePaymentProcessor(client: HttpClient) {
        init {
            println("StripePaymentProcessor instance Created")
        }
    }

    // When resolving this class as a dependency of some other class(es), 
    // an instance of Logger class gets automatically injected as well.
    class HttpClient(logger: Logger) {
        init {
            println("HttpClient instance Created")
        }
    }

    class Logger {
        init {
            println("Logger instance Created")
        }
    }
```

<a name="make-resolve"></a>
#### [Requesting Dependency using `make`](#make-resolve)
Another way to have a dependency resolved out of a container is to use the generic `make` method. To resolve a 
dependency using `make`, just like binding, you need a shared container that contains the bindings that you have
wired.

```kotlin
// src/main/kotlin/controllers/SubscriptionController.kt
class SubscriptionController : Controller() {
    fun show(call: HttpCall) {
        val processor = call.make<PaymentProcessor>()
    }
}
```

<a name="httpcall-container"></a>
### [HttpCall as a DI Container](#httpcall-container)

Just like `dev.alpas.AlpasApp`, `HttpCall` is a DI container as well. 
[`HttpCall` is created for every request](/docs/request-response#httpcall) but since it is a child container 
created off of the app instance, any bindings that are registered in the app instance container is also available 
from an `HttpCall` object. However, the reverse is not true — bindings registered in an `HttpCall` instance 
are **not** available via app instance container.

Also, any bindings that are registered with an `HttpCall` object gets "closed" at the end of the call. This makes sense
because an `HttpCall` is created for each request and gets closed when the request is fulfilled.

> /alert/ <span> Only add new bindings to an `HttpCall` if you want to share those bindings within the same request 
lifecycle. These kinds of bindings are usually thread safe as they are only shared within one request call. </span>

