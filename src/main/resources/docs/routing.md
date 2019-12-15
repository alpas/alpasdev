- [Routes Registration](#routes-registration)
- [Defining Routes](#defining-routes)
    - [Closure Routes](#closure-routes)
    - [Controller Routes](#controller-routes)
- [Route Parameters](#route-parameters)
    - [Required Parameters](#required-parameters)
- [Route Attributes](#route-attributes)
    - [Route Name](#route-name)
    - [Route Middleware](#route-middleware)
- [Route Groups](#route-groups)
    - [Group Prefix](#group-prefix)
    - [Nested Groups](#nested-groups)
    - [Group Name](#group-name)
    - [Group Middleware](#group-middleware)
- [Named Middleware Group](#named-middleware-group)
- [Guarded Routes](#guarded-routes)
    - [Authorized Users Only](#authorized-routes)
    - [Guest Only](#guest-routes)
    - [Verified Users Only](#verified-routes)
- [Form Method Spoofing](#spoofing)
- [Route Helpers](#route-helpers)
    - [Template Helpers](#template-helpers)
    - [Controller Helpers](#controller-helpers)
    - [Alpas Command](#alpas-command)

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

<span class="line-numbers" data-start="3">

```kotlin
fun Router.addRoutes() {
    get("/") { call ->
        call.reply("Hello, World!")
    }
}
```

</span>

<a name="controller-routes"></a>
#### [Controller Routes](#controller-routes)

If your code for responding to an HTTP call is more complex, you could pass in the class name of a controller as the
second parameter, and the name of the method to be called as the third parameter.

<span class="line-numbers" data-start="3">

```kotlin
fun Router.addRoutes() {
    // The 'index' method in your DocsController class is 
    // invoked when the request matches /docs route.
    get("/docs", DocsController::class, "index")
}
```

</span>

The name of the method is actually optional. Alpas follows some convenntions to determine what controller method to 
call on when a path matches — **index()** method is called for a `get` request, **store()** method is invoked for a
`post` request, **delete** method is called for a `delete` request, and finally **update** method is invoked for a 
`patch` request.

<span class="line-numbers" data-start="4">

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

</span>

<a name="route-parameters"></a>
### [Route Parameters](#route-parameters)

<a name="required-parameters"></a>
#### [Required Parameters](#required-parameters)

If you want to capture parameters within your route, you could do so by wrapping a parameter name with angle 
brackets `<>`. You can access the captured values for all your parameters by calling `HttpCall#param()` method from 
your controller.

<span class="line-numbers" data-start="5">

```kotlin
fun Router.addRoutes() {
    // page is a required parameter
    get("/docs/<page>", DocsController::class)

    // both post_id and comment_id are required parameters
    get("/posts/<post_id>/comments/<comment_id>", PostController::class)
}
```

</span>

<a name="route-attributes"></a>
### [Route Attributes](#route-attributes)

<a name="route-name"></a>
#### [Route Name](#route-name)

You can set a name for your routes that makes it easy to refer to these routes esp. while generating URLs. Once you
refer a route by its name, it gives you the flexibility of changing the path of your routes without having to refactor
it everywhere. Specifying a name for a route is done by calling `name()` method on the route.

<span class="line-numbers" data-start="2">

```kotlin
fun Router.addRoutes() {
    get("/docs", DocsController::class).name("docs.show")
}
```

</span>

Once a name is set, you can call this route from anywhere using the name instead of the path.

<span class="line-numbers" data-start="7">

```kotlin
// in a controller
fun index(call: HttpCall) {
    // get the url
    val url = route("docs.show")

    // redirect the call to a named route
    call.redirect().toRouteNamed("docs.show")
}
```

</span>

<span class="line-numbers" data-start="20">

```twig
// in templates
<a name="{{ route('docs.show') }}">Docs</a>
```

</span>

<a name="route-middleware"></a>
#### [Route Middleware](#route-middleware)

If you want to apply a middleware or a list of middleware to a route, you can pass the class name of a middleware
by calling `middleware()` method on the route.

<span class="line-numbers" data-start="5">

```kotlin
fun Router.addRoutes() {
    get("/docs", DocsController::class).middleware(MyMiddleware::class)
    get("/admin", DocsController::class)
        .middleware(MyMiddleware::class, AnotherMiddleware::class)
}
```

</span>

<a name="route-groups"></a>
### [Route Groups](#route-groups)

You can use route groups to share common attributes, such as path prefix, name, middleware etc., for a set of routes 
instead of repeating for each route. Grouping routes also helps you better organize your routes making them more
readable and maintainable.

<a name="group-prefix"></a>
#### [Group Prefix](#group-prefix)
You can set a prefix for a group. The prefix gets prepended to each routes as if it was a path.

<span class="line-numbers" data-start="5">

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

</span>

<a name="nested-groups"></a>
#### [Nested Route Groups](#nested-groups)
Not just a route, but you could nest groups within another route group. Each route receives all the merged attributes
from its parents as well as the grand parents.

<span class="line-numbers" data-start="5">

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

</span>

<a name="group-name"></a>
#### [Group Name](#group-name)
You can give route group a name that will get prepended to a route's name with a dot (.) in between.

<span class="line-numbers" data-start="6">

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

</span>

<a name="group-middleware"></a>
#### [Group Middleware](#group-middleware)
Just like assigning middleare to a route, you could assign middleware to a group. The middleware gets applied to all
the routes under it as well as under any other child groups.

> /alert/ <span> The order of middleware really matters when passing an HttpCall through all the assigned middleware 
> to a route. The middleware are applied in the following order: *inside-out* and *left to right*.</span>

<span class="line-numbers" data-start="6">

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

</span>

<a name="named-middleware-group"></a>
### [Named Middleware Group](#named-middleware-group)
Instead of assigning a list of middleware to a group or to a route, sometimes it is more convenient to make a list of
middleware, give it a name, and then assign this name instead. This allows you to uniformly assign a list of middleware
to different routes and routes groups. 

To do this, first register your middleware group inside `HttpKernel#registerRouteMiddlewareGroups()` method
and call `middlewareGroup()` on your routes or your route groups by passing the middleware group name.

<span class="line-numbers" data-start="15">

```kotlin
// HttpKernel.kt
override fun registerRouteMiddlewareGroups(groups: HashMap<String, List<KClass<out Middleware<HttpCall>>>>) {
    groups["secret"] = listOf(SecretMiddleware::class, SuperSecretMiddleware::class)
}
```

</span>

<span class="line-numbers" data-start="5">

```kotlin
// routes.kt
fun Router.addRoutes() {
    group("/admin/profile") {
        // all the middleware defined under key 'secret' 
        // will be applied to this route
        get("/secret", AdminProfileController::class)
    }.middlewareGroup("secret")

    group("/user/profile") {
        // all the middleware defined under key 'secret' 
        // will be applied to this route as well
        get("/secret", UserProfileController::class)
    }.middlewareGroup("secret")
}
```

</span>


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

<a name="verified-routes"></a>
#### [Verified Users Only](#verified-routes)

If you want a route to be accessible only if a user is [verified](/docs/email-verification), you can either 
apply `VerifiedEmailOnlyMiddleware` middleware or call `mustBeVerified()` method on a route or a route group.

<span class="line-numbers" data-start="5">

```kotlin

fun Router.addRoutes() {
    // anyone can access this route
    get("/", WelcomeController::class)
    // only guests can access this route
    get("/docs", DocsController::class).mustBeGuest()
    // only logged in users can access this route
    get("/profile", UserController::class).mustBeAuthenticated()
    // only authenticated users who have verified their email addresses can access this route
    get("/admin", AdminController::class).mustBeAuthenticated().mustBeVerified()
}

```

</span>

<a name="spoofing"></a>
### [Form Method Spoofing](#spoofing)

HTTP forms only support **GET** or **POST** but not **PUT**, **PATCH**, or **DELETE**. To use these methods 
in your form so that the correct route gets matched, you need to spoof that method by passing a hidden field 
named `_method` with your form.

<span class="line-numbers" data-start="20">

```html
<form action="/docs" method="post">
    <input type="hidden" name="_method" value="delete"/>
    <button type="submit">Delete</button>
</form>
```

</span>

Method spoofing is enabled by default. You can disable method spoofing setting `allowMethodSpoofing` property in your
own `AppConfig` class.


<a name="route-helpers"></a>
### [Route Helpers](#route-helpers)

<a name="template-helpers"></a>
#### [Template Helpers](#template-helpers)

<div class="sublist">

* `route(name, params)`

Creates a full URL for a route of `name`. `params` is a map of paramters expressed as 
if like a JSON object. 

All [required path parameters](#/docs/routing#required-parameters) for the route are first extracted and set 
*and then* any remaining values from the map are passed as query parameters.

<span class="line-numbers" data-start="10">

```twig

<!-- the url will be something like: https://example.com/docs/routing?ver=2 -->

<a href="{{ route('docs.show', {'page': 'routing', 'ver': '2'}) }}">
    Show Routing Docs
</a>

```

</span>

* `hasRoute(name)`

Checks if a route `name` exists.

* `routeIs(name)`

Checks if the current request route matches a `name`.

* `routeIsOneOf(names)`

Checks if the current route matches any of the `names`.

<span class="line-numbers" data-start="10">

```twig
{% if routeIsOneOf(['docs.index', 'docs.toc']) %}
    <h1>Hello Index and TOC!</h1>
{% endif %}
```

</span>

<span class="line-numbers" data-start="10">

```twig
{% if not routeIsOneOf(['docs.index', 'docs.toc']) %}
    <h1>Hello, page!</h1>
{% endif %}
```
</span>

</div>

<a name="controller-helpers"></a>
#### [Controller Helpers](#controller-helpers)

<div class="sublist">

* `route(name: String, params: Map<String, Any>? = null, absolute: Boolean = true)` 

Creates a full URL for route of 
`name`. Set `absolute` to false if you want to get a relative URL to server's address i.e. `/docs/toc` instead of 
`https://alpas.dev/docs/toc`.

> /tip/<span> All the controller route helper methods are also available on `HttpCall` object.<span/>

</div>

<a name="alpas-command"></a>
#### [Alpas Command](#alpas-command)

<div class="sublist">

* `alpas route:list` 

Lists all your app's routes with some important route attributes such as *method name*, *path*, *route name*, 
*actual handler*, *guard type* etc.

</div>
