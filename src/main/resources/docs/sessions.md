- [Session Drivers](#session-drivers)
    - [Store Drivers](#store-drivers)
    - [Cache Drivers](#cache-drivers)
- [Configuring Sessions](#configuring-sessions)
- [Usage](#usage)
    - [Storing Data](#storing-data)
    - [Retrieving Data](#retrieving-data)
    - [Checking Data](#checking-data)
    - [Deleting Data](#deleting-data)
- [Flashing Data](#flashing-data)
    - [Flash Template Helpers](#flash-template-helpers)

HTTP apps are stateless. Once a response has been sent for a request, the connection is closed and server has no 
knowledge of the request. If there was no way to link subsequent requests from the same user then it would soon
become super annoying.

The server gracefully handles this by attaching a small piece of information to the outgoing response and saving some 
more details about it on its side in its "session". A browser on the other hand, saves the data from the server as a 
cookie and sends it back in subsequent requests. The server then uses the information stored in the incoming cookie to 
lookup more details about the user.

The server could save the session data in different kinds of stores. To speed things when doing lookups it could also
cache session data.

Alpas supports File store and Memory cache driver out of the box.

<a name="session-drivers"></a>
### [Session Drivers](#session-drivers)

<a name="store-drivers"></a>
#### [Store Drivers](#store-drivers)

- `file` - Stores sessions as files under `storage/framework/sessions`
- `skip` - Doesn't save any sessions. Appropriate for API only web apps.

<a name="cache-drivers"></a>
#### [Cache Drivers](#cache-drivers)

- `memory` - Caches sessions in memory.
- `skip` - Doesn't cache any sessions.

<a name="configuring-sessions"></a>
### [Configuring Sessions](#configuring-sessions)

You could set the session drivers for your app by setting the appropriate environment variables.

```toml

...
SESSION_DRIVER=file
SESSION_CACHE_DRIVER=memory
...

```

> /tip/ <span> If your app doesn't need a session, maybe because it's an API only app, instead of setting store 
> driver to **skip**, you should just not register `SessionProviderService` in your `HttpKernel` class. This
> improves the performance of your app.</span>


<a name="usage"></a>
### [Usage](#usage)

<a name="storing-data"></a>
#### [Storing Data](#storing-data)

`HttpCall`'s `session` object allows you to store a data in the session.

<span class="line-numbers" data-start="7">

```kotlin

fun index(call: HttpCall) {
    // retrieve the session value for the given "key", if exists. If not, returns the "default" value.
    call.session.put("key", "value")
    call.session.put(mapOf("key1" to "va1", "key2" to 5))
}

``` 

</span>

<a name="retrieving-data"></a>
#### [Retrieving Data](#retrieving-data)

`HttpCall`'s `session` object allows you to retrieve a session data by its name.

<span class="line-numbers" data-start="7">

```kotlin

fun index(call: HttpCall) {
    // retrieve the session value for the given "key", if exists. If not, returns the "default" value.
    val item = call.session.get("key", "default")

    // Alternatively, since the 'session' object is invokable, you could also do:
    val item = call.session("key")
}

``` 

</span>

To retrieve the data and then remove the data from the session, you can use `pull()` method.

If you want to retrieve an item if it exists but want to add it to the session if it doesn't then use: 

`getOrCreate(key: String, default: () -> T?): T?` or `getOrCreate(key: String, default: T?): T?`

<a name="checking-data"></a>
#### [Checking Data](#checking-data)

Use `has()` method to determine if the session has an item **and** that it is not null.

<span class="line-numbers" data-start="9">

```kotlin
    
    ...
    if(call.session.has("key")) {
        // do something
    }
    ...
``` 

</span>

Use `exists()` method instead to determine whether the session has an item or not, even if its value is null.

<a name="deleting-data"></a>
#### [Deleting Data](#deleting-data)

Call `forget()` method with a list of keys for the items that you want to remove from the session. 

<span class="line-numbers" data-start="10">

```kotlin

    ...
    call.session.forget("key1", "key2", "key3")

    // to retrieve an item and then to remove it from the session:
    val item = call.session.pull("key1")
    ...

``` 

</span>


<a name="flashing-data"></a>
### [Flashing Data](#flashing-data)

You may sometimes want to store data in the session that are available only and only in the next request. Think of
[redirecting back](/docs/http-request#redirects) to the previous form with validation errors or redirecting to a new
page but with some messages. These kinds of data are best suited as flashes. Whether you retrieve/pull these flash
data or not, they are gone after next request.

To flash some data, use `flash()` method. You pass tha name of the message as the first parameter and the payload as 
the second parameter.

<span class="line-numbers" data-start="7">

```kotlin

fun index(call: HttpCall) {
    // flash
    call.flash("message", mapOf("status" to "success", "message" to "Purchase completed!"))

    // Alternatively, for convenience, flash() method is also available on the controller itself
    flash("message", mapOf("status" to "success", "message" to "Purchase completed!"))
}

``` 

</span>

You'd still use `session().get()` method to retrieve as flash message as it is just a session item.

> /tip/ <span> If you want to keep all of your flash data for an additional request, use `reflash()` method. </span>

<a name="flash-template-helpers"></a>
#### [Flash Template Helpers](#flash-template-helpers)

There are a few flash related helpers available in your templates.

<div class="sublist">

- `flash(key, default)`

Retrieve a flash payload for the given key. If it doesn't exist then return the default value.

<span class="line-numbers" data-start="14">

```twig

<h1>{{ flash('success', 'Good job!') }}</h1>

```

</span>

- `hasFlash(key)`

Checks if a flash item for the given key exists.

<span class="line-numbers" data-start="14">

```twig

{% if hasFlash('success') %}
<h1 class="green">{{ flash('success') }}</h1>
{% else %}
<h1 class="red">Oops! There was an error.</h1>
{% endif %}

```

</span>

</div>

> /alert/ <span>Most of the session related features are only available for the routes that have `SessionStart` 
> middleware applied — either applied individually or using `web` 
> [middleware group](/docs/routing#named-middleware-group).</span>
