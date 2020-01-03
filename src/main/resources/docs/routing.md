- [Getting Started](#getting-started)
- [Defining Routes](#defining-routes)
    - [Lambda Routes](#lambda-routes)
    - [Controller Routes](#controller-routes)
- [Route Parameters](#route-parameters)
    - [Required Parameters](#required-parameters)
- [Route Attributes](#route-attributes)
    - [Route Name](#route-name)
    - [Route Middleware](#route-middleware)
- [Route Groups](#route-groups)
    - [Group Path Prefix](#group-prefix)
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
    - [Alpas Route Commands](#alpas-command)

<a name="getting-started"></a>
### [Getting Started](#getting-started)

Start by registering your app routes on an instance of `dev.alpas.routing.Router`. Although you could do this
from anywhere, Alpas convention is to add them in the `routes.kt` file and then load them in
`providers/RouteServiceProvider.kt` class. When [scaffolding a project](/docs/installation)
both these files are created for you and is already wired to load your routes. All you
need to do is add your routes in `routes.kt` file.

Alpas routing supports all the routes that respond to any HTTP verbs: `get`, `post`, `put`, `patch`, `delete`, and
`options`.

<a name="defining-routes"></a>
### [Defining Routes](#defining-routes)

<a name="lambda-routes"></a>
#### [Lambda Routes](#lambda-routes)

At the very minimum, you can register a route where the first parameter is a path, and the second
parameter is a [function literal](https://kotlinlang.org/docs/reference/lambdas.html#function-literals-with-receiver)
with [`HttpCall`](/docs/request-response#httpcall) as the receiver.

<span class="line-numbers" data-start="3" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    get("/") { 
        reply("Hello, World!")
    }

    post("/ping") {
        reply("pong")
    }
}

```

</span>

<a name="controller-routes"></a>
#### [Controller Routes](#controller-routes)

If your code for responding to an HTTP call is more complex, you can pass in the class name of a
[controller](/docs/controllers) as the second parameter, and the name of the action as the
third parameter.

<span class="line-numbers" data-start="3" data-file="routes.kt">

```kotlin
fun Router.addRoutes() {
    // The 'index' method in your DocsController class is 
    // invoked when the request matches /docs route.
    get("/docs", DocsController::class, "index")
}
```

</span>

While using controller routes, the name of the method is actually optional. Alpas follows some conventions to
determine what controller action to call when a path matches:

<div class="sublist">

- `index()` method for a `get` request
- `store()` method for a `post` request
- `delete()` method for a `delete` request
-  `update()` method for a `patch` request

</div>

<span class="line-numbers" data-start="4" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    get("/docs", DocsController::class) // call DocsController#index()
    post("/docs", DocsController::class) // calls DocsController#store()
    delete("/docs", DocsController::class) // calls DocsController#delete()
    update("/docs", DocsController::class) // calls DocsController#update()

    // You can pass your own method name if you want to invoke a different method.
    get("/docs", DocsController::class, "show") // call DocsController#show()
}

```

</span>

<a name="route-parameters"></a>
### [Route Parameters](#route-parameters)

<a name="required-parameters"></a>
#### [Required Parameters](#required-parameters)

If you want to capture parameters within your route, you could do so by wrapping a parameter name with angle brackets
`<>`. You can later [access these captured values](/docs/http-request#parameters) from your controller.

<span class="line-numbers" data-start="5" data-file="routes.kt">

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

You can set names for your routes to make it easy to refer them from your code esp. while generating URLs.
Setting a name for a route gives you the flexibility of changing its path without having to refactor
everywhere it is referenced. To give a name to a route, just call `name()` method on the route.

<span class="line-numbers" data-start="2" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    get("/docs", DocsController::class).name("docs.show")
}

```

</span>

Once a name is set, you can call this route from anywhere by its name instead of its actual path.

<span class="line-numbers" data-start="7" data-file="controllers/DocsController.kt">

```kotlin

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

If you want to apply a [middleware](/docs/middleware) to a route, you can pass the class name of the middleware
by calling `middleware()` method on the route. If you want, you can also pass a list of middleware classes.

<span class="line-numbers" data-start="5" data-file="routes.kt">

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

Instead of repeating common attributes, such as path prefix, name, middleware etc., for each route, you can
instead use route groups. Grouping routes also helps you better organize your routes making them more
readable and maintainable.

<a name="group-prefix"></a>
#### [Group Path Prefix](#group-prefix)

You can set a prefix for a group. The prefix gets prepended to each route as if it was a path.

<span class="line-numbers" data-start="5" data-file="routes.kt">

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
Not just a route, but you can also nest groups within another route group. Each route within a group
and sub-groups receives all the merged attributes from its parents as well as its grand parents.

<span class="line-numbers" data-start="5" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    group("/docs") {
        group("/latest") {
              // Receives all the attributes from both "docs" and "latest" groups.
             // matches /docs/latest path
            get(DocsController::class)
        }
    }
}

```

</span>

<a name="group-name"></a>
#### [Group Name](#group-name)

You can give a name to a route group, which will then get prepended to each route's name within the group
with a dot (.) in between the names.

<span class="line-numbers" data-start="6" data-file="routes.kt">

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

Just like assigning middleware to a route, you can also assign middleware to a group. The middleware
gets applied to all its grandchildren routes.

> /alert/ <span> The order of middleware really matters when passing an `HttpCall` through all the assigned
> middleware of a route. They are applied in *inside-out* and *left-to-right* order.</span>

<span class="line-numbers" data-start="6" data-file="routes.kt">

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

Instead of assigning a list of middleware to a group or to a route, sometimes it is more convenient to
make a list of middleware, give it a name, and then assign this name. This allows you to share a
middleware group and uniformly apply it to different routes and routes groups. 

To create a named middleware route, you need to first register your middleware group inside `HttpKernel`'s
`registerRouteMiddlewareGroups()` method with a name and then call `middlewareGroup()` on your routes,
or your route groups, by passing the name.


<div class="ordered-list">

1. Register the middleware group

<span class="line-numbers" data-start="15" data-file="HttpKernel.kt">

```kotlin

//...

override fun registerRouteMiddlewareGroups(
    groups: HashMap<String,
    List<KClass<out Middleware<HttpCall>>>>) {

    groups["secret"] = listOf(SecretMiddleware::class, SuperSecretMiddleware::class)
}

//...

```

</span>

2. Apply the middleware group name

<span class="line-numbers" data-start="5" data-file="routes.kt">

```kotlin

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

</div>

<a name="guarded-routes"></a>
### [Guarded Routes](#guarded-routes)

<a name="authorized-routes"></a>
#### [Authorized Users Only](#authorized-routes)

If you want a route to be accessible only to [authorized users](/docs/authentication), you can either apply
`AuthOnlyMiddleware` middleware or call `mustBeAuthenticated()` method. Just like other route attributes,
the `mustBeAuthenticated()` method can be called either on a route or on a route group.

<a name="guest-routes"></a>
#### [Guests Only](#guest-routes)

Similarly, if you want a route to be accessible only if a user is not authenticated, such as a login
route, you can either apply `GuestOnlyMiddleware` middleware or call `mustBeGuest()` method.

<a name="verified-routes"></a>
#### [Verified Users Only](#verified-routes)

If you want a route to be accessible only if a user is [verified](/docs/email-verification), you can either 
apply `VerifiedEmailOnlyMiddleware` middleware or call `mustBeVerified()` method on a route or a route group.

<span class="line-numbers" data-start="5" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    // anyone can access this route
    get("/", WelcomeController::class)

    // only guests can access this route
    get("/docs", DocsController::class).mustBeGuest()

    // only logged in users can access this route
    get("/profile", UserController::class).mustBeAuthenticated()

    // only authenticated users who have also verified
    // their email addresses can access this route
    get("/admin", AdminController::class).mustBeAuthenticated().mustBeVerified()
}

```

</span>

<a name="spoofing"></a>
### [Form Method Spoofing](#spoofing)

HTTP forms only support **GET** or **POST** but not **PUT**, **PATCH**, or **DELETE**. To use these methods
in your form so that the correct route gets matched, you need to spoof it by passing a hidden field named
`_method` with your form.

<span class="line-numbers" data-start="20">

```html

<form action="/docs" method="post">
    <input type="hidden" name="_method" value="delete"/>
    <button type="submit">Delete</button>
</form>

```

</span>

Method spoofing is enabled by default. But you can disable it by setting `allowMethodSpoofing`
property to `false` in your `AppConfig` class.

<a name="route-helpers"></a>
### [Route Helpers](#route-helpers)

<a name="template-helpers"></a>
#### [Template Helpers](#template-helpers)

<div class="sublist">

* `route(name, params)`

Creates a full URL for the route of the given name `name`. `params` is a map of parameters
expressed as if like a JSON object: `{'page': 'routing', 'ver': '2'}`

All [required path parameters](#/docs/routing#required-parameters) for the route are first extracted and
set **and then** any remaining values from the map are passed as query parameters to the route.

<span class="line-numbers" data-start="10">

```twig

<!-- the url will be something like: https://example.com/docs/routing?ver=2 -->

<a href="{{ route('docs.show', {'page': 'routing', 'ver': '2'}) }}">
    Show Routing Docs
</a>

```

</span>

* `hasRoute(name)`

Checks if a route of `name` exists.

* `routeIs(name)`

Checks if the name of the current request route matches `name`.

* `routeIsOneOf(names)`

Checks if the name of the current route matches any of the `names`.

<span class="line-numbers" data-start="10">

```twig

{% if routeIsOneOf(['docs.index', 'docs.toc']) %}
    <h1>Hello Index and TOC!</h1>
{% endif %}

```

</span>

You can negate the check like so:

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

Creates a full URL for a route of the `name`. Set `absolute` to `false` if you want to get a URL
relative to server's address i.e. `/docs/toc` instead of `https://alpas.dev/docs/toc`.

</div>

<a name="alpas-command"></a>
#### [Alpas Route Commands](#alpas-command)

<div class="sublist">

* `alpas route:list` 

Lists all your app's routes with some route attributes such as *method name*, *path*,
*route name*, *actual handler*, *auth channel type* etc.

</div>
