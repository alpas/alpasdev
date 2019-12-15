- [Creating Middleware](#creating-middleware)
- [Before/After Middleware](#before-after)
- [Registering Middleware](#registering-middleware)
    - [Route Entry Middleware](#route-entry-middleware)
    - [Server Entry Middleware](#server-entry-middleware)

A Middleware, as the name implies, sits in the middle of a request lifecycle and lets you introspect an incoming request 
and act on it. Think of middleware as like filters that an `HttpCall` must pass through. 

For example, a logging middleware might log the details of an incoming request to a file or an authentication 
middleware might check your session and decide whether to let you continue or return an error. In fact 
`dev.alpas.auth.middleware.AuthOnlyMiddleware` that Alpas includes does exactly that. Alpas ships with many more
such middleware.

<a name="creating-middleware"></a>
### [Creating Middleware](#creating-middleware)
You create a middleware by extending `dev.alpas.Middleware<T>` class and overriding `invoke()` method. You can use
`make:middleware` Alpas command to create a new middleware.

```bash
alpas make:middleware AdminOnlyMiddleware
```

This command will create a new `AdminOnlyMiddleware` middleware class under `middleware` folder. Let's
say this middleware checks if the logged in user is actually an admin. If the checks passes it forwards the call but 
if not, throws an `AuthenticationException` exception.

<span class="line-numbers" data-start="8">

```kotlin

// middleware/AdminOnlyMiddleware.kt
class AdminOnlyMiddleware : Middleware<HttpCall>() {
    override fun invoke(call: HttpCall, forward: Handler<HttpCall>) {
        if (call.user.isAdmin()) {
            forward(call)
        } else {
            throw AuthenticationException()
        }
    }
}

```

</span>

This middleware can then be [applied to a route](/docs/routing#route-middleware) forcing the route to pass through it.

Here's another example of a middleware that just does some logging.

<span class="line-numbers" data-start="7">

```kotlin

// middleware/LoginMiddleware.kt
class LoggingMiddleware : Middleware<HttpCall>() {
    val logger: Logger = Logger()

    override fun invoke(call: HttpCall, forward: Handler<HttpCall>) {
        logger.info(call.referrer)
        forward(call)
    }
}

```

</span>

As you can see, your middleware doesn't always have to be "active" taking an action on the call but could be "passive" 
-- just doing something and forwarding the call.

<a name="before-after"></a>
### [Before/After Middleware](#before-after)

Inside of a middleware, you can decide what chunks of code gets run before a request hits a matching route and what 
chunks of code run after the request is handled. Any chunks of code before `forward(call)` call gets run before and 
any chunks of code after `forward(call)` gets called after the call has been handled.

<span class="line-numbers" data-start="7">

```kotlin

class LoggingMiddleware : Middleware<HttpCall>() {
    val logger: Logger = Logger()

    override fun invoke(call: HttpCall, forward: Handler<HttpCall>) {
        logger.info("Hello! This runs before matching the route")
        forward(call)
        logger.info("This runs after handling the call. Bye!")
    }
}

```

</span>

<a name="registering-middleware"></a>
### [Registering Middleware](#registering-middleware)

<a name="route-entry-middleware"></a>
#### [Route Entry Middleware](#route-entry-middleware)

Route middleware are invoked for each request once the target route has matched but before actually hitting the route.
These middleware can be assigned to a [route](#/docs/routing#route-middleware), to a 
[route group](#/docs/routing#group-middleware), or can be [grouped together with a name](#named-middleware-group) 
and then be assigned it to either a route.

<a name="server-entry-middleware"></a>
#### [Server Entry Middleware](#server-entry-middleware)

Server entry middleware are invoked for each request regardless of whether the target route has matched or not. 
Register you server entry middleware by overriding `HttpKernel#serverEntryMiddleware()` method and adding your
middleware to the list passed.

<span class="line-numbers" data-start="18">

```kotlin

// HttpKernel.kt
override fun registerServerEntryMiddleware(middleware: MutableList<KClass<out Middleware<HttpCall>>>) {
    middleware.add(SecretServerMiddleware::class)
    middleware.add(SuperSecretServerMiddleware::class)
}

```

</span>
