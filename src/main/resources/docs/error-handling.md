- [Default Error Templates](#default-error-templates)
- [Handling Exceptions](#handling-exceptions)
- [Throwing HTTP Exceptions](#throwing-http-exceptions)
- [Stacktrace Dump](#stacktrace-dump)

When your web application throws an exception, whether user initiated or not,
Alpas tries its best to handle it gracefully.

Based on the exception's status code, it first converts it to a proper HTTP exception type, such as 
`NotFoundHttpException` for **404**, `ValidationException` for **422**, `MethodNotAllowedException`
for **405**, `InternalServerException` for **500** etc. It then gives each exception a chance
to [report](#report) and then [render](#render) the exception.

Each exception type does what is appropriate for it. Most of the exceptions return a message and a status code if the
[expected response is JSON](/docs/http-request#expects-json).

For non-JSON responses, different exceptions do different things—`ValidationException`
[flashes errors and inputs](/docs/validation#errors-management) making it available
to the next request, both `NotFoundHttpException` and `InternalServerException`
render a beautiful error page.
 
 <a name="default-error-templates"></a>
 ### [Default Error Templates](#default-error-templates)
 
When you scaffold a new Alpas web app, two error templates for the most common HTTP exceptions—**404 Page Not Found**
and **500 Internal Server Exception**—are created for you under `resources/errors` directory.

These error pages look great out of the box! However, you are more than welcome to
modify it to make it fit more with your app's overall branding.

> /alert/ <span>**Do not** change the name or the location of the default error templates.</span>

<a name="handling-exceptions"></a> 
### [Handling Exceptions](#handling-exceptions)

Alpas's default way of handling the exceptions may or may not work for you. You may want to tweak it or completely
change the behavior. Alpas allows you to easily intercept an exception being thrown and handle it the way you
want it. This applies for both the built-in exceptions and your own custom exceptions.

To intercept an exception, you need to provide your own subclass of `dev.alpas.exceptions.ExceptionHandler`
class and then override one or more of `report()`, `render()`, or `handle()` methods.

<div class="sublist">

<a name="report"></a> 
- `report(exception: HttpException, call: HttpCall)`

Override this method to log or send the given HTTP exception to external services like [Bugsnag](https://bugsnag.com),
[Sentry](https://sentry.io) etc. By default, the `report()` method passes the exception to the base class which
then calls the `report()` method on the exception itself. Most of the built-in exceptions just logs a message
either as a warning or as an error.

<span class="line-numbers" data-start="5" data-file="exceptions/ExceptionHandler.kt">

```kotlin

class ExceptionHandler : dev.alpas.exceptions.ExceptionHandler() {
    override fun report(exception: HttpException, call: HttpCall) {
        when (exception) {
            is NotFoundHttpException -> call.logger.warn { "This resource is missing!" }
            is ValidationException -> call.logger.warn { "The input is invalid!" }
            is MyCustomException -> ThirdPartyLogger.log(exception)
            else -> super.report(exception, call)
        }
    }
}

```

</span>

<a name="render"></a> 
- `render(exception: HttpException, call: HttpCall)`

Override this method to generate an HTTP response for a given exception and to send it back to the client.
If you want to render a template for an exception, this is the place to do it. In fact, this is how
the [default 404 and 500 error pages](#default-error-templates) are rendered by Alpas internally.

<span class="line-numbers" data-start="5" data-file="exceptions/ExceptionHandler.kt">

```kotlin

class ExceptionHandler : dev.alpas.exceptions.ExceptionHandler() {
    override fun render(exception: HttpException, call: HttpCall) {
        when (exception) {
            is MyCustomException -> call.render("errors/custom_error", 418)
            else -> super.render(exception, call)
        }
    }
}

```

</span>

- `handle(exception: Throwable, call: HttpCall)`

Override this method if you want to handle non-http exceptions. You may be throwing a particular type of
exception from different parts of your web app. This method is the perfect place to handle those types
of exceptions in one central place. By default, this method converts any non-HTTP exception to a
catch-all `InternalServerException` and reports and renders it accordingly.

</div>

>/tip/ <span> Your custom `ExceptionHandler` class can live anywhere in your package, but we
>recommend putting it under `exceptions` directory. Don't worry about loading this class.
>Alpas will automatically discover, load, and use this class for you!</span>

<br/>

<a name="throwing-http-exceptions"></a>
### [Throwing HTTP Exceptions](#throwing-http-exceptions)

Use `HttpCall`'s one of many [abort()](/docs/http-response#abort) methods to throw an HTTP exception.

<a name="stacktrace-dump"></a>
### [Stacktrace Dump](#stacktrace-dump)

When rendering an `InternalServerException`, if your app environment is set to dev mode, it dumps the stacktrace
to your web page. This makes it easy to just see what's going on without having to resort to your IDE's
console.

You can control whether the stacktrace gets dumped to your web page or not by setting a proper value for
`APP_LEVEL` variable in your `.env` file. Setting it to one of **dev**, **debug**, or **local** dumps
the stacktrace but setting it to one of **prod**, **production**, or **live** doesn't.

>/alert/ <span>Make sure to set `APP_LEVEL` to one of **prod**, **production**, or **live** once your app is
>deployed before it goes live to avoid exposing any internal data through a dumped stacktrace.</span>

