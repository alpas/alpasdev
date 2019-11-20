- [Routes Registration](#routes-registration)
- [Defining Routes](#defining-routes)
    - [Closure Routes](#closure-routes)
    - [Controller Routes](#controller-routes)
- [Route Attributes](#route-attributes)
    - [Name](#route-name)
    - [Middleware](#route-middleware)
- [Route Groups](#route-groups)
    - [Group Prefix](#group-prefix)
    - [Nested Groups](#nested-groups)
    - [Group Name](#group-name)
    - [Group Middlware](#group-middleware)
- [Guarded Routes](#guarded-routes)
    - [Authorized](#authorized-routes)
    - [Guest](#guest-routes)

<a name="routes-registration"></a>
### [Routes Registration](#routes-registration)

You register your app routes on an instance of `dev.alpas.routing.Router`. The convention is to add all your routes
in a `routes.kt` file and load them in `RouteServiceProvider` class. When scaffolding a project, both these files
are created for you and is already wired to load your routes. All you need to do is define your routes in `routes.kt`
file.

Alpas routing supports all the routes that respond to any HTTP verbs: `get`, `post`, `put`, `patch`, `delete`, and 
`options`.

<a name="defining-routes"></a>
### [Defining Routes](#defining-routes)

<a name="closure-routes"></a>
#### [Closure Routes](#closure-routes)

At the very basic, you can register a route where the first parameter is the path and the second parameter is a closure
that receives an [`HttpCall`](/docs/request-response#httpcall) instance.

```kotlin
fun Router.addRoutes() {
    get("/") { call ->
        call.reply("Hello, World!")
    }
}
```

<a name="controller-routes"></a>
#### [Controller Routes](#controller-routes)

If your code for responding to an HTTP call is more complex, you could pass in the class name of a controller as the
second parameter and the name of the method to be called as the third parameter.

```kotlin
fun Router.addRoutes() {
    // The 'index' method in your DocsController class is 
    // invoked when the request matches /docs route.
    get("/docs", DocsController::class, "index")
}
```

The name of the method is actually optional. Alpas follows some conventions to determine what controller method to 
call on when a path matches -- **index()** method is called for a `get` request, **store()** method is invoked for a
`post` request, **delete** method is called for a `delete` request, and finally **update** method is invoked for a 
`patch` request.

```kotlin
fun Router.addRoutes() {
    get("/docs", DocsController::class) // invokes index() method
    post("/docs", DocsController::class) // invokes store() method
    delete("/docs", DocsController::class) // invokes delete() method
    update("/docs", DocsController::class) // invokes update() method

    // You can pass your own method name if you 
    // want to invoke a different method.
    get("/docs", DocsController::class, "show") // invokes show() method
}
```

<a name="route-attributes"></a>
### [Route Attributes](#route-attributes)

<a name="route-name"></a>
#### [Name](#route-name)

You can set a name for your routes that makes it easy to refer to these routes esp. while generating URLs. Once you
refer a route by its name, it gives you the flexibility of changing the path of your routes without having to refactor
it everywhere. Specifying a name for a route is done by calling `name()` method on the route.

```kotlin
fun Router.addRoutes() {
    get("/docs", DocsController::class).name("docs.show")
}
```

Once a name is set, you can call this route from anywhere using the name instead of the path.

```kotlin
// in controllers
fun index(call: HttpCall) {
    // get the url
    val url = route("docs.show")

    // redirect the call to a named route
    call.redirect().toRouteNamed("docs.show")
}
```

```twig
// in templates
<a name="{{ route('docs.show') }}">Docs</a>
```

<a name="route-middleware"></a>
#### [Middleware](#route-middleware)

If you want to apply a middleware or a list of middleware to a route, you can pass the class name of a middleware
by calling `middleware()` method on the route.

```kotlin
fun Router.addRoutes() {
    get("/docs", DocsController::class).middleware(MyMiddleware::class)
    get("/admin", DocsController::class)
        .middleware(MyMiddleware::class, AnotherMiddleware::class)
}
```

<a name="route-groups"></a>
### [Route Groups](#route-groups)

You can use route groups to share common attributes, such as path prefix, name, middleware etc., for a set of routes 
instead of repeating for each route. Grouping routes also helps you better organize your routes making them more
readable and maintainable.

<a name="group-prefix"></a>
#### [Group Prefix](#group-prefix)
You can set a prefix for a group. The prefix gets prepended to each routes as if it was a path.

```kotlin
fun Router.addRoutes() {
    group("/docs") {
         // matches /docs path
        get(DocsController::class, "index")
         // matches /docs/toc path
        get("/toc", DocsController::class, "showToc")
         // matches /docs/latest path
        get("/latest", DocsController::class, "showLatest")
    }
}
```

<a name="nested-groups"></a>
#### [Nested Route Groups](#nested-groups)
Not just a route, but you could nest groups within another route group. Each route receives all the merged attributes
from its parents as well as the grand parents.

```kotlin
fun Router.addRoutes() {
    group("/docs") {
        group("/latest") {
             // matches /docs/latest path
            get(DocsController::class)
        }
    }
}
```

<a name="group-name"></a>
#### [Group Name](#group-name)
You can give route group a name that will get prepended to a route's name with a dot (.) in between.

```kotlin
fun Router.addRoutes() {
    group("/docs") {
         // will be available as "docs.show"
        get(DocsController::class).name("show")
    }.name("docs")

    group("/admin") {
        group("/profile") {
             // will be available as "admin.profile.show"
            get(ProfileController::class).name("show")
        }.name("profile")
    }.name("admin")
}
```

<a name="group-middleware"></a>
#### [Group Middleware](#group-middleware)
Just like assigning middleare to a route, you could assign middleware to a group. The middleware gets applied to all
the routes under it as well as under any other child groups.

> /alert/ <span> The order of middleware really matters when passing an HttpCall through all the assigned middleware 
> to a route. The middleware are applied in the following order: *inside-out* and *left to right*.</span>

```kotlin
fun Router.addRoutes() {
    group("/admin") {
        group("/profile") {

            get(ProfileController::class)

            // The middleware assigned to this route, in order, are:
            // 1. SecretMiddleware      2. SuperSecretMiddleware
            // 3. ProfileMiddleware     4. AdminMiddleware     
            // 5. AuthOnlyMiddleware
            get("/secret", ProfileController::class)
              .middleware(SecretMiddleware::class, SuperSecretMiddleware::class)

        }.middleware(ProfileMiddleware::class)
    }.middleware(AdminMiddleware::class, AuthOnlyMiddleware::class)
}
```

<a name="guarded-routes"></a>
### [Guarded Routes](#guarded-routes)

<a name="authorized-routes"></a>
#### [Authorized Users Only](#authorized-routes)

If you want a route to be accessible only to authorized users, you could either apply `AuthOnlyMiddleware` middleware
or call `mustBeAuthenticated()` method. Just like other attributes, the `mustBeAuthenticated()` method can be called 
either on a route or on a route group.

<a name="guest-routes"></a>
#### [Guests Only](#guest-routes)

Similarly, if you want a route to be accessible only if a user is not authenticated, such as a login route, you can
either apply `GuestOnlyMiddleware` middleware or call `mustBeGuest()` method on a route or a route group.

