- [Introduction](#introduction)
- [Creating Controllers](#creating-controllers)
- [Using Controllers](#using-controllers)

<a name="introduction"></a>
### [Introduction](#introduction)

You need to set a handler for when a request matches one of your routes. While closure is a quick way to handle a call,
it becomes messy once your handling logic becomes more complex. In such a situation you need a place to put and organize
your logic. Controllers are where you'd do exactly that. 

Controllers are an integral part of an Alpas web app and go hand-in-hand with [routing](/docs/routing).

Not just for organizing your handler logic, controllers also come with some handy helper function to make it easier
to handle an `HttpCall`.

> /tip/ <span>All your app controllers are stored under `controllers` folder by default. You can organize 
> your controllers in subfolders. We actually highly recommend that you do so.</span>


<a name="creating-controllers"></a>
### [Creating Controllers](#creating-controllers)

You could manually create a controller by creating a class and then extending `dev.alpas.routing.Controller` class. Or
you could use `alpas make:controller` Alpas command to make one or multiple controllers in one shot.

```bash
# creates AdminController.kt file under controllers/admin folder
alpas make:controller admin/AdminController

# creates DocsController.kt and HomeController.kt files under controllers folder
alpas make:controller DocsController HomeController
```

<span class="line-numbers" data-start="7">

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

You should never create an instance of a controller but should only refer it as a `KClass` in your routes.

<span class="line-numbers" data-start="3">

```kotlin
fun Router.addRoutes() {
    get("/docs", DocsController::class, "show")
}
```

</span>

All the controller's handler methods receive an `HttpContext` object as an argument.
