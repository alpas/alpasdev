- [Returning Response Payload](#response-payload)
- [Rendering Templates](#rendering-templates)
- [Aborting Calls](#abort)
- [Headers](#headers)
    - [Attaching Headers](#attaching-headers)
    - [Content Type](#content-type)
- [Cookies](#cookies)
    - [Attaching Cookies](#attaching-cookies)
    - [Forgetting Cookies](#forgetting-cookies)
- [Redirects](#redirects)

Just like with an [HTTP request](/docs/http-request), `HttpCall` object is what you'd use for
sending responses back to a client.

<a name="reesponse-payload"></a>
### [Returning Response Payload](#response-payload)

You can send response back to the client with a "payload" by using one of `reply()` or `replyAsJson()`
methods. There are some overloaded variants of these methods as well.

<div class="sublist">

- `reply(payload: T? = null, statusCode: Int = 200)`

Returns a payload back to the client in a UTF-8 encoded HTML format. The payload is first converted
to a string by calling`toString()` method on it. You can pass a status code if you want it to be
something other than the default 200.

- `replyAsJson(payload: T? = null, statusCode: Int = 200)`

Returns the given payload to the client in a JSON format.

- `replyAsJson(obj: JsonSerializable, statusCode: Int = 200)`

A convenient method for returning an object that implements `dev.alpas.JsonSerializable` interface.
The returned format is JSON, of course!

- `acknowledge(statusCode: Int = 204)`

Sometimes you may just want to acknowledge a request call and not necessarily have any payload to return.
In such situations you can use this convenient method. Since, the acknowledgement without a payload
is mostly applicable in a JSON request, the content type is set to JSON when using this method.

- `status(code: Int)`

Use this method if you want to set/change the status code of your response *after* calling one of the above methods.

</div>

<a name="rendering-templates"></a>
### [Rendering Templates](#rendering-templates)

Instead of returning a string or a `JsonSerializable` object, you can render a template instead
using one of render methods.

<div class="sublist">

- `render(templateName: String, args: Map<String, Any?>?, statusCode: Int)`

Renders a template of the given name with some optional contextual arguments for the template.
The location of the template, by default, is under `resources/templates`. You can change
the default location by [overriding](/docs/configuration#core-configs)
`templatesDirectory` property of `dev.alpas.view.ViewConfig` class.

</div>

<a name="abort"></a>
### [Aborting Calls](#abort)

Instead of returning a valid response, you may want to abort a call and return an error response
back to the client. You can do so by using `abort()` or `abortUnless()` method.

<div class="sublist">

- `abort(statusCode: Int, message: String?, headers: Map<String, String>): Nothing`

Aborts the call and sends back an exception appropriate for the given status code. **404** code is converted
to `NotFoundHttpException`, **405** to `MethodNotAllowedException`, **500** to `InternalServerException`,
and all other exceptions to `HttpException`.

- `abortUnless(condition: Boolean, statusCode: Int, message: String?, headers: Map<String, String>)`

Aborts the call if the given condition is `false`.

- `abortIf(condition: Boolean, statusCode: Int, message: String?, headers: Map<String, String>)`

Aborts the call if the given condition is `true`.

> /tip/ <span>You can simply throw a subclass of `dev.alpas.exceptions.HttpException` if you
>want to have more control over the exception being thrown when aborting a call.</span>

</div>

<a name="headers"></a>
### [Headers](#headers)

<a name="attaching-headers"></a>
#### [Attaching Headers](#attaching-headers)

Use `addHeader(key: String, value: String)` to attach a header or `addHeaders(headers: Map<String, String>)` 
to attach multiple headers to an outgoing response. You can call these methods multiple times to attach
additional headers.

<span class="line-numbers" data-start="7">

```kotlin

fun index(call: HttpCall) {
    call.apply {
        addHeader("Content-Type", "application/json")
        addHeader("X-H1", "Some Value")
        addHeader("X-H2", "Some Value")
    }.reply("Headers Galore!")
 }

```

</span>

<a name="content-type"></a>
#### [Content Type](#content-type)

<div class="sublist">

- `asHtml()`

Set the response's content type to **text/html; charset=UTF-8**.

- `asJson()`

Set response's content type to **application/json; charset=UTF-8**.

- `contentType(type: String)`

Set response's content type to the given type.

<span class="line-numbers" data-start="7">

```kotlin

fun index(call: HttpCall) {
    val content = mapOf("say" to "Hello, Json!").toJson()
    call.reply(content).asJson()   
    // alternatively
    call.replyAsJson(content)
 }

```

</span>
</div>

<a name="cookies"></a>
### [Cookies](#cookies)

Cookie is a small piece of information that gets sent with a response back to the client. The client would then
return unexpired cookies [back to the server in the subsequent requests](#/docs/http-request#retrieving-cookies).

> /info/ <span> Alpas encrypts and signs almost all of your outgoing cookies and decrypts them when it receives a 
> request. If the cookies are changed by the client, they will be invalidated and removed automatically as well.

> /tip/ <span>You can return the names of the cookies that you **don't** want to be encrypted by [extending 
> `SessionConfig`](/docs/configuration#core-configs) class and then overriding `encryptExcept` value.</span>

<a name="attaching-cookies"></a>
#### [Attaching Cookies](#attaching-cookies)

Send a cookie back to the client by calling one of `addCookie()` methods.

<div class="sublist">

- `fun addCookie(name: String, value: String?, lifetime: Duration, path: String?, domain: String?, secure: Boolean, httpOnly: Boolean)`

    * **name** - The name of the cookie. Must not be empty.
    * **value** - The payload of the cookie.
    * **lifetime** - Maximum age for this cookie. Default is -1 second. A negative duration means this cookie will be
                     deleted when the browser exits. A zero duration means the cookie will be deleted right away.
    * **path** - The path for the cookie to which the client should return the cookie. Default is `null`.
    * **domain** - The domain within which the cookie should be presented. Default is `null`.
    * **secure** - Whether the browser should return this cookie only using a secure protocol, such as HTTPS or SSL,
                   or not. Default is `false`.
    - **httpOnly** - Whether to mark this cookie as *HttpOnly* or not. This directs a browser to not expose this cookie
                     to client-side scripting code. Default is `true`.

</div>

<a name="forgetting-cookies"></a>
#### [Forgetting Cookies](#forgetting-cookies)

You can forget/ clear a cookie by calling `forgetCookie()` method and passing the name of the cookie
that you would want to clear. Optionally, you can also pass the cookie's `path` and/or the `domain`.

<a name="redirects"></a>
### [Redirects](#redirects)

The `HttpCall#redirect()` method returns an implementation of `dev.alpas.http.Redirectable`, which
has everything you need to redirect a call to somewhere else—either internal or external.

<div class="sublist">

- `fun to(to: String, status: Int = 302, headers: Map<String, String> = emptyMap())`

Redirects a call to the given **to** url.

<a name="redirect-back"></a>
- `fun back(status: Int = 302, headers: Map<String, String> = emptyMap(), default: String = "/")`

Redirects a call to the previous location. The previous location is determined by first looking at the
referral header in the request. If it is null then it looks the value of a previous url from the
session. Alpas automatically saves this location in the current user session for every request.
If both of these values are absent, then it will redirect to the given *default* location.

> /tip/ <span>This method is very handy for sending a user back to a form if the form input
>is invalid. In fact, this is exactly what Alpas does to redirect a user back to the
>previous page if a form fails the [validation checks](/docs/validation).</span>

- `fun intended(default: String = "/", status: Int, headers: Map<String, String>)`

Redirects a call to the location that a user initially intended to go to. Let's say a user is trying to
access a page that requires the user to be authenticated. In this case, if user isn't authenticated,
we would normally take them first to a login page so that they could be authenticated. After
they successfully login, we would want to take them to the page they originally intended.
In this case you'd want to use `intended()` method.

- `fun toRouteNamed(name: String, params: Map<String, Any>, status: Int, headers: Map<String, String>)`

Redirects a call to a route that matches the given **name**. **params** is a map of parameters for that route.

<span class="line-numbers" data-start="7">

```kotlin

fun index(call: HttpCall) {
    call.redirect().toRouteNamed("docs.showPage", mapOf("page" to "http-response"))
}

```

</span>

- `fun home(status: Int = HttpStatus.MOVED_TEMPORARILY_302, headers: Map<String, String>)`

A convenient method that redirects a call to a route named **home**, if exists. If not, it redirects to **/**.

- `fun toExternal(url: String, status: Int = 302, headers: Map<String, String>)`

Redirects a call to the given external url.

</div>

> /alert/ <span>Some redirects methods such as `back()`, `intended()` etc. depend on [sessions](/docs/sessionn) 
>and hence they are available only for the routes that have `SessionStart` middleware applied—either
>applied individually or using `web` [middleware group](/docs/routing#named-middleware-group).</span>
