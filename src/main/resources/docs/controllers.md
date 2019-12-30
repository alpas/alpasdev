- [Creating Controllers](#creating-controllers)
- [Using Controllers](#using-controllers)

To actually handle a request, you need to set a handler for when it matches one of your routes. While closure
is a quick way to do it, it becomes messy once your handling logic becomes more complex. In such situations,
you need a place to put and organize your logic. Controllers are where you do exactly that. 

Controllers are an integral part of an Alpas web app and go hand-in-hand with [routing](/docs/routing).

Not just for organizing your handler logic, controllers also come with some handy helper
functions—such as `flash`, `queue` etc.—to make it easier to handle an incoming call.

> /tip/ <span>All your app controllers are stored under `controllers` folder by default. You can
>organize your controllers in subfolders. We actually highly recommend that you do so.</span>

<a name="creating-controllers"></a>
### [Creating Controllers](#creating-controllers)

You can manually create a controller by creating a class that extends `dev.alpas.routing.Controller` class.
Or you can use `alpas make:controller` Alpas command to create one or multiple controllers in one shot.

```bash

# creates AdminController.kt file under controllers/admin folder
$ alpas make:controller admin/AdminController

# creates DocsController.kt and HomeController.kt files under controllers folder
$ alpas make:controller DocsController HomeController

```

<span class="line-numbers" data-start="7" data-file="controllers/DocsController">

```kotlin

class DocsController : Controller() {
    fun show(call: HttpCall) {
        call.reply("Hello, Visitor!")
    }
}

```

</span>

<a name="using-controllers"></a>
### [Using Controllers](#using-controllers)

When [referring a controller](/docs/routing#controller-routes) from your router, you should
**never** create an instance of it directly but should only refer it as a `KClass`.

<span class="line-numbers" data-start="3" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    get("/docs", DocsController::class, "show")
}

```

</span>

All the controller's route handler methods receive an `HttpContext` object as an argument.
