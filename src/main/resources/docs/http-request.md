- [Introduction](#introduction)
- [Retrieving Request Properties](#properties)
- [Retrieving Request Parameters](#parameters)
- [Retrieving Headers](#headers)
- [Retrieving Cookies](#cookies)
- [Asynchronous Request](#asynchronous-request)

Alpas wraps every request that hits your app with an `HttpCall` object. `HttpCall` is no doubt the most powerful
object in Alpas. It has everything you need to get any data and operations you need out of a request.

<a name="properties"></a>
### [Retrieving Request Properties](#properties)

<div class="sublist">

- `method` 

Returns you an enum of type `dev.alpas.http.Method` telling you the HTTP verb used by the request.

- `url`

Returns you the URL for the incoming request **without*- the query string.
 
- `fullUrl` 

Gives you the URL for the incoming request **including*- the query string.

- `isAjax` 

Tells you whether the request is an ajax request or not.

- `isPjax` 

Tells you whether the request is a [Pjax](https://github.com/defunkt/jquery-pjax) request or not.

- `isJson` 

Tells you whether the request content is of type 'application/json' or not.

<a name="wants-json"></a>
- `wantsJson` 

Tells you whether the request wants a JSON response or not.

<a name="expects-json"></a>
- `expectsJson` 

Tells you whether the request is "expecting" a JSON response or not. This is true if the request has
explicitly mentioned that it wants a JSON response **or** if the request is an AJAX request.

- `body` 

Gives you the body of the request as a UTF-8 encoded string.

- `jsonBody` 

Deserializes the body of the request and returns as a `HashMap<String, Any>`.

</div>

<a name="parameters"></a>
### [Retrieving Request Parameters](#parameters)

<div class="sublist">

- `params : Map<String, List<Any>?>?` 

Gives you the combined values of all the request parameters—query params, form params, and route params.

- `routeParams : Map<String, List<Any>?>?` 

Gives you the values of just the route parameters.

- `queryParams : Map<String, List<Any>?>?`

Gives you the values of just the query parameters.

- `fun params(key: String) : List<Any>?` 

Returns all the values for the given key as a nullable list.

- `fun params(key: String, vararg keys: String): Map<String, Any?>` 

Returns a map of values for only the specified keys.

- `fun paramsExcept(key: String, vararg keys: String): Map<String, Any?>` 

Returns a map of values for everything but the specified keys.

<span class="line-numbers" data-start="8">

```kotlin

//...

val only = call.params("username", "email", "phone")
val except = call.paramsExcept("password", "password_confirm")

//...

```

</span>

- `fun routeParams(key: String) : List<Any>?` 

Returns all the route parameter values for the given key.

- `fun queryParams(key: String) : List<Any>?`

Returns all the query parameter values for the given key.

- `fun param(key: String) : Any?` 

Returns the value of the given key if it exists otherwise returns null.

- `fun routeParam(key: String) : Any?` 

Returns the route parameter value of the given key.

- `fun queryParam(key: String) : Any?` 

Returns the query parameter value of the given key.

- `fun stringParam(key: String) : String?` 

Returns the value of the given key as a nullable string.

- `fun intParam(key: String) : Int?` 

Returns the value of the given key as a nullable int.

- `fun longParam(key: String) : Long?` 

Returns the value of the given key as a nullable long.

- `fun boolParam(key: String) : Boolean?` 

Returns the value of the given key as a nullable bool.

> /alert/<span>The `params(key: String, vararg keys: String)` returns only
>the values for the keys that actually exist in the request.</span>

> /info/<span>Alpas **doesn't*- merge the JSON body of a request with other parameters and **doesn't**
>make them available through one of the above `paramX()` methods. Instead, you need to get the
>`jsonBody` property and then extract the param you want yourself.</span>

</div>

<a name="headers"></a>
### [Retrieving Headers](#headers)

<div class="sublist">

- `fun header(key: String) ? String?` 

Returns the header value for a given key if exists. If multiple values exist for the given key,
it returns the first value.

- `fun headers(key: String) ? List<String>?` 

Returns all the header values for the given key if exists. 

</div>

<a name="cookies"></a>
### [Retrieving Cookies](#cookies)

To read the cookies from an incoming request, use the `cookie` property of the `HttpCall` object.

<span class="line-numbers" data-start="8">

```kotlin

//...

call.cookie.get("username")
// Alternatively
call.cookie("username")
// Alternatively
call.cookie["username"]

//...

```

</span>

> /info/ <span> Alpas encrypts and signs almost all of your outgoing cookies and decrypts them when it receives a 
> request. If the cookies are changed by the client, they will be invalidated and removed automatically as well.

<a name="asynchronous-request"></a>
### [Asynchronous Request](#asynchronous-request)

Alpas supports asynchronous request processing by passing a `CompletableFuture` instance to the `HttpCall#hold()` method.

<span class="line-numbers" data-start="6">

```kotlin

// ...

fun store(call: HttpCall) {
    call.hold(delay(1000))
}

private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
private fun delay(sleep: Int) : CompletableFuture<String> {
    return CompletableFuture<String>().apply {
        executorService.schedule({ complete("ok") }, sleep, TimeUnit.MILLISECONDS)
    }
}

```

</span>

Your completable future must be either of type `String` or an object of a class implementing `dev.alpas.http.Response`
interface. So, if you want to render a template asynchronously, you have to return an instance of `ViewResponse`,
which already implements the `Response` interface. Similarly, there is `JsonResponse` class for returning
back something to the client as a JSON object.

<span class="line-numbers" data-start="6">

```kotlin

// ...

fun store(call: HttpCall) {
    call.hold(delay(1000))
}

private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
private fun delay(sleep: Int) : CompletableFuture<ViewResponse> {
    return CompletableFuture<ViewResponse>().apply {
        executorService.schedule({ complete(ViewResponse("welcome")) }, sleep, TimeUnit.MILLISECONDS)
    }
}

```

</span>
