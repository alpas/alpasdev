- [Introduction](#introduction)
- [Retrieving Request Properties](#retrieving-properties)
- [Retrieving Request Parameters](#retrieving-parameters)
- [Retrieving Headers](#retrieving-headers)
- [Retrieving Cookies](#retrieving-cookies)
- [Form Method Spoofing](#spoofing)

Alpas wraps every request that hits your app to an `HttpCall` object. `HttpCall` is no doubt the most powerful object
in Alpas. It has everything you need to get any data and operations you need out of a request.

<a name="retrieving-properties"></a>
### [Retrieving Request Properties](#retrieving-properties)

<div class="sublist">

* `method` - returns you an enum of type `dev.alpas.http.Method` telling you the HTTP verb used by the request.

* `url` - returns you the URL for the incoming request without the query string.
 
* `fullUrl` - gives you the URL for the incoming request **including** the query string.

* `isAjax` - tells you whether a request is an ajax request or not.

* `isPjax` - tells you whether a request is a [Pjax](https://github.com/defunkt/jquery-pjax) request or not.

* `isJson` - tells you whether the request content is of type 'application/json' or not.

* `wantsJson` - tells you whether the request wants a Json response or not.

* `expectsJson` - tells you whether the request is "expecting" a Json response or not. This is true if request
has explicitly mentioned that it wants a Json response **or** if the request is an ajax request.

* `body` - gives you the body of the request as a UTF-8 encoded string.

* `jsonBody` - deserializes the body of the request and returns as a `HashMap<String, Any>`.

</div>

<br/>

<a name="retrieving-parameters"></a>
### [Retrieving Request Parameters](#retrieving-parameters)

<div class="sublist">

* `params : Map<String, List<Any>?>?` - gives you the combined values of all the request parameters - query params, 
form params, and route params.
* `routeParams : Map<String, List<Any>?>?` - gives you the values of just the route parameters.
* `queryParams : Map<String, List<Any>?>?` - gives you the values of just the query parameters.

* `fun params(key: String) : List<Any>?` - returns all the values for the given key as a nullable list.
* `fun onlyParams(key: String, vararg keys: String): Map<String, Any?>` - returns a map of values for only the specified 
keys.
* `fun paramsExcept(key: String, vararg keys: String): Map<String, Any?>` - returns a map of values for everything but the
specified keys.

<span class="line-numbers" data-start="8">

```kotlin

val only = call.onlyParams("username", "email", "phone")
val except = call.paramsExcept("password", "password_confirm")

```

</span>

* `fun routeParams(key: String) : List<Any>?` - returns all the route parameter values for the given key.
* `fun queryParams(key: String) : List<Any>?` - returns all the query parameter values for the given key.

* `fun param(key: String) : Any?` - returns the value of the given key if it exists otherwise returns null.
* `fun routeParam(key: String) : Any?` - returns the route parameter value of the given key.
* `fun queryParam(key: String) : Any?` - returns the query parameter value of the given key.

* `fun paramAsKey(key: String) : String?` - returns the value of the given key as a nullable string.
* `fun paramAsInt(key: String) : Int?` - returns the value of the given key as a nullable int.
* `fun paramAsLong(key: String) : Long?` - returns the value of the given key as a nullable long.
* `fun paramAsBool(key: String) : Long?` - returns the value of the given key as a nullable bool.

> /alert/<span>The `params(key: String, vararg keys: String)` returns only the values for the keys that actually 
> exist on the request.</span>

> /info/<span>Alpas **doesn't** merge the JSON body of a request with other parameters and **doesn't** make them 
> available through one of the above `paramX()` methods. Instead, you need to get the `jsonBody` property and then 
> extract the param you want yourself.</span>

</div>

<a name="retrieving-headers"></a>
### [Retrieving Headers](#retrieving-headers)

<div class="sublist">

* `fun header(key: String) ? String?` - returns the header value for a given key if exists. If multiple values exist for
the given key, it returns the first value.

* `fun headers(key: String) ? List<String>?` - returns all the header values for the given key if exists. 

</div>

<a name="retrieving-cookies"></a>
### [Retrieving Cookies](#retrieving-cookies)

You read the cookies from an incoming request, use the `cookie` method on the `HttpCall` object.

<span class="line-numbers" data-start="8">

```kotlin

call.cookie("username")

```

</span>

> /info/ <span> Alpas encrypts and signs almost all of your outgoing cookies and decrypts them when it receives a 
> request. If the cookies are changed by the client, they will be invalidated and removed automatically as well.

<a name="spoofing"></a>
### [Form Method Spoofing](#spoofing)

HTTP forms only support **GET** or **POST** but not **PUT**, **PATCH**, or **DELETE**. To use these methods 
in your form so that the correct route gets matched, you need to spoof that method by passing a hidden field 
named `_method` with your form.

<span class="line-numbers" data-start="16">

```html

<form action="/docs" method="post">
    <input type="hidden" name="_method" value="delete"/>
    <button type="submit">Delete</button>
</form>

```

</span>
