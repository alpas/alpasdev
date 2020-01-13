- [Lifecycle](#lifecycle)
- [HttpCall](#httpcall)
    
**Request ⇄ Response** chain is at the core of every web application — a browser makes a request that gets
received by your web app, which in turn sends back a response based on what the request was. 

As simple as this sounds on the surface, a lot of things have to happen in the correct order to successfully
respond to a request call. There are many layers through which a request passes before returning a successful
response to the client.

"Successful response" doesn't always mean returning with a response that the client wants/expects. It could also
mean an error message with a status code — something very different from what the client was asking for.

Different frameworks implement this middle layers of converting request into a response differently. However, if you
are coming from a dynamic language framework like Laravel, one main difference that you have to understand is that
with those dynamic frameworks every conversion of request to response means, to simplify, starting a new copy of your
app and passing the request through the app to make a response out of it and then shutting down the app.
 
 A static language framework like Alpas works a bit differently!

Instead of starting a new app from scratch every time, you start your app once and then wait for a request. When it
receives a request it spins up a new thread and processes the request until it gets converted to a response or
the server dies because of an unhandled exception.

<a name="lifecycle"></a>
### [Request ⇄ Response Lifecycle](#lifecycle)

At the high level this is what happens when your Alpas web app receives a request:

![Request Response Flow](/images/request-response.png)

<a name="httpcall"></a>
### [HttpCall](#httpcall)

Notice how Alpas wraps a request in an `HttpCall` object as soon as the application receives it. `HttpCall` is
really the center character of the framework and the one you will interact with the most. It gets passed around
pretty much everywhere and contains everything to [handle a request](/docs/http-request) — query parameters,
headers, cookies, form request fields, [session](/docs/sessions) etc. `HttpCall` is also a
[service container](/docs/ioc-container) and so you could ask it for any core dependencies or any other dependencies
that you have set the bindings for.

Not just the request, `HttpCall` is also where you would set a reply to an [outgoing response](/docs/http-response),
headers, cookies etc.
