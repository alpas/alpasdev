- [Templates Location](#templates-location)
- [The Template](#the-template)
    - [Nested Templates](#nested-templates)
- [Data Access](#data-access)
    - [Context data passed from a controller](#context-data-passed-from-a-controller)
    - [Sharing view data on HttpCall object](#sharing-view-data-on-httpcall-object)
    - [Configuration Values](#configuration-values)
    - [Environment Variables](#environment-variables)
- [Template Helpers](#template-helpers)
    - [Route Helpers](#route-helpers)
    - [Validation Helpers](#validation-helpers)
    - [Session Helpers](#session-helpers)
    - [Mix Helpers](#mix-helpers)
- [Template Tags](#template-tags)
- [Template Filters](#template-filters)    
- [Extending Pebble](#extending-pebble)
- [Global Variables](#global-variables)
- [Syntax Highlighting](#syntax-highlighting)

Alpas uses [Pebble](https://pebbletemplates.io/) as its templating engine. Pebble is a modern, very powerful
templating engine with great features such as Template inheritance (`layout`, `extends`, `partials` etc.),
Macros, built-in auto-escaping security, rich set of built-in tags, filters, and functions etc.

It also very straightforward to extend the engine with your own filters, tags, and functions.

Pebble is a sub-set of [Twig](https://twig.symfony.com/) and is very similar to [Jinja][jinja] templating engine. If
you already know Twig, or even Jinja, you already know most of Pebble's syntax. You can see the similarities and
differences between both engines in this [compatibility matrix](https://pebbletemplates.io/twig-compatibility/).

Alpas enriches already powerful Pebble with some of its own extensions. This also means, for better
or worse, Pebble is the *only* supported templating engine out-of-the-box and has is tightly
integrated with the core of Alpas. 

While it is certainly possible to use some other templating engines, when it comes to power, features,
simplicity, flexibility, and support, Pebble is the best. We really believe that once you have
ascended the initial learning curve of Pebble, which isn't really steep to begin with,
you'll never go back to any other templating engines. There is a good reason why we
went with Pebble after considering many other options, including dumping our own
in-house built templating engine (may it rest in peace)!

<a name="templates-location"></a>
### [Templates Location](#templates-location)

By default, templates should be kept in `resources/templates` folder and must end with `.peb` extension. If you wish,
you could configure both the location and the extension [by overriding](/docs/configuration#core-configs)
`templatesDirectory` and `templateExtension` properties of `dev.alpas.view.ViewConfig` class.
Unless you have a good reason to do so, we highly recommend using the default values.

> /info/ <span>For performance boost, templates are cached during [production](/docs/configuration#checking-environment)
>but not during development. Any data that you pass to a template are **not cached**. So you can render the same
>template for different requests without worrying about either the performance or stale data.

<a name="the-template"></a>
### [The Template](#the-template)

Here is an example of what a typical layout template looks like in Alpas.

<span class="line-numbers" data-start="1" data-file="resources/templates/layout/app.peb">

```twig

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="{{ mix('css/app.css') }}">
    <title> {{ env('APP_NAME') }} - {{ block('title') }}</title>
    <script src="{{ mix('js/app.js') }}" defer></script>
</head>

<body>

<div id="app">
    <main>
        {% if hasFlash('success') %}
            <div>
                <div class="text-green">{{ flash('success') }}</div>
            </div>
        {% endif %}

        {% block content %} {% endblock %}
    </main>
</div>

</body>
</html>

```

</span>

And, this is what a child template looks like:

<span class="line-numbers" data-start="1" data-file="resources/templates/welcome.twig">

```twig

{% extends "layout/app.twig" %}
{% block title %} Welcome Page  {% endblock %}

{% block content %}
    <div>
        <h1>Hello, {{ name }}!</h1>
    </div>
{% endblock %}
```

</span>

You can render the above template from within a controller by calling `render()` method
and passing just the name of the template without the extension.

<span class="line-numbers" data-start="1">

```kotlin

fun index(call: HttpCall) {
    // you only need to pass the name of the template without its extension
    call.render("index", mapOf("name" to "Alpas"))
}

```

</span>

<a name="nested-templates"></a>
#### [Nested Templates](#nested-templates)

To better organize or to group them logically, templates can be nested in sub-directories. In fact, we highly
encourage you to organize your templates with sub-directories. There are no performance penalties for
using sub-directories. When rendering these nested templates, you need to reference them using
either a **/** "slash" notation (recommended), or using a **.** "dot" notation.

Let's say we have a template `resources/templates/docs/latest/toc.peb`.
You can reference this template from a controller like so:
 
 `call.render("docs/latest/toc")` Or, alternatively, `call.render("docs.latest.toc")`.

> /tip/ <span> During development you will be tweaking your templates and changing the data that get passed
>from a controller a lot. If you are using [IntelliJ IDEA](https://www.jetbrains.com/idea/),
>instead of recompiling and re-running your app again and again, you could just reload your
>changed classes from `Run > Reload Changed Classes` menu and then switch to your browser
>and reload the page. Even better - set a shortcut for this menu like **CMD+R**.
>This is a huge time saver and productivity booster!

<a name="data-access"></a>
### [Data Access](#data-access)

<a name="context-data-passed-from-a-controller"></a>
#### [Context data passed from a controller](#context-data-passed-from-a-controller)

A controller can pass the data to a template as a map of string, and an *any* object. The *any* object could
be a nested map as well. Inside a template you can access each value inside the map using its key.
A nested map's values can be accessed using nested **.** "dot" syntax.

```kotlin

// in your controller:
val address = mapOf("street" to "123 Broadway Ave", "zip" to "123456") 
call.render("index", mapOf("name" to "Jane", "address" to address))


```

```twig

<-- in your template -->
<h1>Hello, {{ name }}!</h1>

<h2>Your address:</h2>
    Street: {{ address.street }}
    Zip: {{ address.zip }}  

```

<a name="sharing-view-data-on-httpcall-object"></a>
#### [Sharing view data on HttpCall object](#sharing-view-data-on-httpcall-object)

A handy method, aptly named `shareWithView(key: String, value: Any?)`, can be used to share some data
with the templates from anywhere an `HttpCall` is accessible—from a helper function, a private
function within a controller, or from within the [ValidationGuard](/docs/validation) etc.

```kotlin

// anywhere from where a call is accessible
call.shareWithView("valuation", 25000000)

```


```kotlin

// you can append additional context data from
// your controller when calling render
call.render("index", mapOf("name" to "John"))

```

```twig

<h1>Hello, {{ name }}!</h1>
Your value is: ${{ valuation }}

```

<a name="configuration-values"></a>
#### [Configuration Values](#configuration-values)

Alpas already makes all your configs available in your view templates using a combo of ``config()`` 
function and a convention that is based on the name of your config class—camelcase class name
with the suffix *Config* stripped off.

Let's say you want to access `appUrl` config defined in `AppConfig` class from one of your templates.
You can access it like so: `{{ config('app.appUrl') }}`.

Notice how `AppConfig` class is simply referred as `app`.
 
Let's see another example. Say you want to access `adminEmail` config defined in an `AdminConfig`
class from one of your templates, you'd do: `{{ config('admin.adminEmail') }}`.

<a name="environment-variables"></a>
#### [Environment Variables](#environment-variables)

Just like your config values, an `env` view function is available in your templates to access
any environment variables.

Let's say you want to access the `APP_URL` environment variable defined in `.env` file from one of your templates.
Simply do: `{{ env('APP_URL') }}`.

<a name="template-helpers"></a>
### [Template Helpers](#template-helpers)

These are some helper functions that come shipped with Alpas. For Pebble template functions, please consult
[the official documentation](https://pebbletemplates.io/wiki/guide/basic-usage/#functions). 

<a name="route-helpers"></a>
#### [Route Helpers](#route-helpers)

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

<a name="validation-helpers"></a>
#### [Validation Helpers](#validation-helpers)

- `old(key, default)`

If exists, returns an old input value for the given `key`. If not, returns the given `default` value.
If multiple values exist for the given `key`, it returns a list of all values.

<span class="line-numbers" data-start="16">

```twig

<form action="/users" method="post">
    {% csrf %}
    <input type="text" name="username" value="{{ old('username') }}" />
    <input type="email" name="email" value="{{ old('email', 'johndoe@example.com') }}" />
    <button type="submit">Create</button>
</form>

```

</span>

- `errors(key, default)`

If exists, returns an error or a list of errors for the given `key`. If not, returns the given `default` value.
`key` is optional when calling this function. If you don't pass it, it just returns the *errors* map.

- `firstError(key, default)`

If exists, returns the first error for the given `key`. If not, return the given `default` value.
Unlike the `errors()` method, the `key` here is required.

- `hasError(key)`

Returns `true` if an error exists for the given `key`. Otherwise, returns `false`

- `whenError(key, value, default)`

If an error exists for the given `key`, it returns the given `value`. If not, it returns the `default` value.

<a name="session-helpers"></a>
#### [Session Helpers](#session-helpers)

- `flash(key, default)`

Retrieve a flash payload for the given `key`. If it doesn't exist then return the given `default` value.

<span class="line-numbers" data-start="14">

```twig

<h1>{{ flash('success', 'Good job!') }}</h1>

```

</span>

- `hasFlash(key)`

Checks if a flash item for the given `key` exists.

<span class="line-numbers" data-start="14">

```twig

{% if hasFlash('success') %}
<h1 class="green">{{ flash('success') }}</h1>
{% else %}
<h1 class="red">Oops! There was an error.</h1>
{% endif %}

```

</span>

<a name="mix-helpers"></a>
#### [Mix Helpers](#mix-helpers)

- `mix(name, directory)`

Returns a URL of an asset identified by the given `name` relative to the `resources/web` directory. The optional
parameter `directory` is the location where the `mix-manifest.json` file is relative to the `resources/web`
directory. It's very rare that you'd have to set the second parameter.

<span class="line-numbers" data-start="3">

```twig

<!-- the url will be something like /css/app.css -->
<link rel="stylesheet" href="{{ mix('css/app.css') }}">

<!-- the url will be something like /js/analytics/ga.js -->
<script src="{{ mix('js/analytics/ga.js') }}"></script>

```

</span>

</div>

<a name="template-tags"></a>
### [Template Tags](#template-tags)

Here are some tags that come shipped with Alpas. For Pebble template tags, please consult
[the official documentation](https://pebbletemplates.io/wiki/tag/autoescape/).

<div class="sublist">

- `{% csrf %}`

Generates a hidden input field with its value set to a CSRF token for current request. This is required to protect you
against [cross-site request forgery (CSRF)](https://en.wikipedia.org/wiki/Cross-site_request_forgery) attacks. If you
just need to the raw **csrf** token rather than generating an input hidden field, use `_csrf` variable instead.

- `{% auth %} ... {% else %} ... {% endauth %}`

If the current user is authenticated, evaluates the block within. Otherwise, evaluates the *else* block.

<span class="line-numbers" data-start="12">

```twig

{% auth %}
    <h1>Hello, {{ auth.user.name }}! You are so awesome! </h1>
{% else %}
    <h1> Welcome, guest! You're awesome!</h1>
{% endauth %}

```

</span>

- `{% guest %} ... {% else %} ... {% endguest %}`

Just opposite of `auth` tag—if the current user is a guest, evaluates the block within.
Otherwise, evaluates the *else* block.

<span class="line-numbers" data-start="12">

```twig

{% guest %}
    <h1> Welcome, guest! You're awesome!</h1>
{% else %}
    <h1>Hello, {{ auth.user.name }}! You are so awesome! </h1>
{% endauth %}

```

</span>

</div>

<a name="template-filters"></a>
### [Template Filters](#template-filters)

Filters modify the output further such as converting a text to uppercase, concatenating all collection
items to a string, etc. Here are some additional filters provided by Alpas. Pebble comes with
[lots of very handy filters](https://pebbletemplates.io/wiki/guide/basic-usage/#filters).

<div class="sublist">

- `int`

Attempts to casts the given variable to an integer otherwise returns null. Internally performs 
`?.toString().toIntOrNull()` on the given input.

- `json_encode`

Converts the given input to JSON using [Jackson](https://github.com/FasterXML/jackson). This is a very
useful filter when passing data as a JSON encoded string to a frontend library like React, VueJS etc.

<span class="line-numbers" data-start="10">

```twig

    <h1> Your Tasks</h1>
    <task-list :tasks="{{ tasks is empty ? '[]' : tasks | json_encode }}"></task-list>

```

</span>

- `ago`

Displays an instance of `LocalDateTime` or `Instant` object as relative time ago language. Internally
uses [TimeAgo](https://github.com/marlonlom/timeago) to perform the actual conversion.

<span class="line-numbers" data-start="8">

```twig

    <h3>This task was created: {{ task.createdAt | ago }}</h3>

```

</span>

</div>

<a name="extending-pebble"></a>
### [Extending Pebble](#extending-pebble)

[Extensibility](https://pebbletemplates.io/wiki/guide/extending-pebble/) is at the heart of Pebble.
Creating your own tags, functions, filters, tests, and a global variable is very straight forward.
Pebble has a whole [page](https://pebbletemplates.io/wiki/guide/extending-pebble/) dedicated on
how to do it.

**We highly recommend that you go through the official Pebble documentation on extensibility to learn more about it.**

Read the [source code](https://github.com/PebbleTemplates/pebble) on how they have implemented
their own filters, tags, functions etc. before writing your own.

Once you have created your own extensions, you need to reigster it with Alpas to make them available in your templates.
You can do this in a few ways. The easiest way is to extend `dev.alpas.view.ViewServiceProvider` class and overriding
`loadExtensions(app: Application): Iterable<AbstractExtension>` method returning a list of your extensions.

>/alert/ <span>Make sure to register your newly created service provider class instead of
>`dev.alpas.view.ViewServiceProvider` in `HttpKernel` otherwise your extensions won't
>be loaded.</span>

>/tip/ Very rarely you'll need to write more than one extension as an extension can return a
>list of filters, tags, functions etc.

Let's see a real example of writing a `ago` custom filter and registering it in four relatively simple steps:

<div class="ordered-list">

1. Create the actual filter.

<span class="line-numbers" data-start="3" data-file="AgoFilter.kt">

```kotlin

class AgoFilter : Filter {
    override fun getArgumentNames(): List<String>? {
        return null
    }

    override fun apply( input: Any?,
        args: Map<String, Any>?,
        self: PebbleTemplate,
        context: EvaluationContext?,
        line: Int
    ): Any? {
        return when (input) {
            is Instant -> TimeAgo.using(input.toEpochMilli())
            is LocalDateTime -> TimeAgo.using(input.toInstant(ZoneOffset.UTC).toEpochMilli())
            else -> {
                val name = self.name
                val message = "ago needs an Instant or LocalDateTime. Called from $name#$line."
                throw Exception(message)
            }
        }
    }
}

```

</span>

2. Create an extension and return a list of filters, tags, functions etc.

<span class="line-numbers" data-start="2" data-file="MyPebbleExtensions.kt">

```kotlin

class MyPebbleExtensions : AbstractExtension() {
    override fun getFilters(): Map<String, Filter> {
        return mapOf("ago" to AgoFilter())
    }
}

```

</span>

3. Create your own version of `ViewServiceProvider` and return a list of all your custom extensions.

<span class="line-numbers" data-start="3" data-file="providers/MyViewServiceProvider.kt">

```kotlin

class MyViewServiceProvider : dev.alpas.view.ViewServiceProvider() {
    override fun loadExtensions(app: Application): Iterable<AbstractExtension> {
        return listOf(MyPebbleExtensions())
    }
}

```

</span>

4. Register your new service provider in `HttpKernel` and remove `dev.alpas.view.ViewServiceProvider`.

<span class="line-numbers" data-start="3" data-file="HttpKernel.kt">

```kotlin

class HttpKernel : HttpKernel() {
    override fun serviceProviders(app: Application): Iterable<KClass<out ServiceProvider>> {
        return listOf(
            // ...,
            // ...,
            // ...,
            MyViewServiceProvider::class,
            // ...,

            // Don't forget to remove the following
            // ViewServiceProvider::class
        )
    }
}

```

</span>

</div>

<a name="global-variables"></a>
### [Global Variables](#global-variables)

If you want some variables to be globally available to all your templates, you can override `getGlobalVariables()`
method in your custom extension and return a `Map<String, Any>`. This map will be merged into the data passed
from a controller to a template when rendering. 

<a name="syntax-highlighting"></a>
### [Syntax Highlighting](#syntax-highlighting)

One great advantage of Pebble being a sub-set of Twig templating engine is that JetBrains has an 
[official Twig Plugin](https://plugins.jetbrains.com/plugin/index?xmlId=com.jetbrains.twig) that
provides a handful of twig features—syntax highlighting, auto-completion, live templates etc.
To apply this plugin, your templates must end with a `.twig` extension and IntelliJ IDEA
will pick it up automatically.

Unfortunately, this plugin is only available for the Ultimate Edition of InteliJ IDEA and not for the free
community version. Fortunately, there is another free extension that is equally good but is totally free
and open-source and is made specifically for Pebble. The plugin, aptly named Pebble IntelliJ, is
available on [GitHub](https://github.com/bjansen/pebble-intellij) for free.

To apply this plugin, your templates must end with a `.peb` extension. Make sure to read the 
[official doc](https://github.com/bjansen/pebble-intellij/blob/master/README.md) on
how to set it up properly.

> /power/ <span>Alpas's templating is proudly powered by [Pebble Templates](https://pebbletemplates.io/).

[jinja]: https://palletsprojects.com/p/jinja/
