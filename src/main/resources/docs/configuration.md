- [Configs](#configs)
    - [Overriding Core Configs](#core-configs)
    - [Custom Configs](#custom-configs)
- [Environment](#env)
    - [System Environment Variables](#system-env-variables)
    - [Checking your environment](#checking-environment)
    
<a name="configs"></a>
### [Configs](#configs)

Alpas uses strongly typed config classes for configuring the framework. All these configs live under `configs` folder.

<a name="custom-configs"></a>
#### Custom Configs

Any classes that implement `dev.alpas.config` interface will be discovered and loaded automatically without you
having to do anything to load it. You can then later ask for a singleton copy of your config class using a 
[DI container](/docs/ioc-container).

Here is an example of how you could create your own config class and then how to access its properties
from a controller:

<span class="line-numbers" data-start="5">

```kotlin

// configs/AdminConfig.kt
class AdminConfig(env: Environment) : Config {
    val adminEmail = env("ADMIN_EMAIL", "admin@example.com")
}

// controllers/AdminController.kt
class AdminController : Controller() {
    fun show(call: HttpCall) {
        val email = call.make<AdminConfig>().adminEmail
    }
}

```

</span>

> /tip/ <span> Instead of hard coding configurable variables that you use in different parts of your app, we highly
recommend that you use a config class instead. Configs are strongly typed, get loaded automatically, and, if
you want, you can easily overwrite values using [environment variables](#env).</span>

<a name="core-configs"></a>
#### [Overriding Core Configs](#core-configs) 

Since Alpas is a convention based web framework, almost everything is configured with some sane defaults.
In case you want to change the default values or add new values to one of the core config classes, you
can do it easily.

To change a core config, create a new config class that extends the core config and override the
properties you want to change.

Let's say you want to change the default extension of your [view templates](/docs/pebble-templates) from `.peb`,
which is set in `dev.alpas.view.ViewConfig` class, to `.twig`. To do this first create a new class
under `configs` folder and override the value like so:

<span class="line-numbers" data-start="5">

```kotlin

// configs/ViewConfig.kt
class ViewConfig(env: Environment) : dev.alpas.view.ViewConfig(env) {
    // change existing config
    override val templateExtension = ".twig"

    // add new config
    val layoutTemplate = "layouts/app"
}

```

</span>

> /info/ Just like custom config classes, once declared, Alpas automatically loads these extended config classes
instead of the parent core config classes. So don't worry about how to load them; just declare and forget!

> /tip/ <span>Instead of extending core config classes, you might be able to change the values of by simply
setting appropriate variables in your `.env` file.</span>

<a name="env"></a>
### [Environment](#env)

There are situations when you want to set some configs used in your app based on an environment. This allows
you, for an example, to set values based on whether you are developing your app or deploying it.

You also don't want these configs to be compiled with your app as you want to easily deploy your app without
having to recompile a deployment copy after changing some configurations.

Also, if you are using some third-party API keys/secrets, you don't want to hard code these and push it to
your version control. This is very risky from a security standpoint.

To make this flexible, Alpas allows you to configure values in an `.env` file that lives in the root of
your project. The variables in this file gets loaded automatically and is available for you in pretty
much anywhere you need.

> /info/ <span>When you initialize your app using `alpas init` command, an `.env` file with some defaults is
automatically created for you.</span>

It is very important that you don't commit this `.env` file in the app's source control for mainly two
reasons—first, this file could contain some secret API keys that you don't want to accidentally
make available for everyone. Secondly, different developers on your team might want to use
different environment configurations. This is true for different servers as well. 

When you need to share some configurations with your team, the convention is to use `.env.example` file that
usually contains the same keys but with different placeholder values.

> /alert/ <span>You must have an `.env` file in your root project during development and right next to your
final jar file during deployment.</span>

> /info/ <span>If an `.env.testing` file is present, this gets loaded instead of `.env` in test mode. This
allows you to have the flexibility of running tests with a different set of configurations.</span>

<a name="system-env-variables"></a>
#### [System Environment Variables](#system-env-variables)

Not just the variables defined in your `.env` file, Alpas loads all your system wide environment variables and
makes them available in the same manner as well. In fact, system wide environment variables take precedence
over the variables defined in your `.env` file.

> /tip/ <span>We highly recommend using system wide environment variables for more critical configurations
such as secret keys and API keys rather than defining them in your `.env` file.</span>

<a name="accessing-variables"></a>
#### [Accessing Variables](#accessing-variables)

In some places, such as the config classes, if you ask for it as a constructor parameter, Alpas automatically
injects an instance of `dev.alpas.Environment`. In other places, such as your controllers, you can access
any available environment variables through an `env` object. 

Here is an example of how you could access an environment variable named `SSO_SHARED_SECRET` from your
controller:

<span class="line-numbers" data-start="6">

```kotlin

// controllers/AdminController.kt
class SsoLoginController : Controller() {
    fun login(call: HttpCall) {
        val ssoSecret:String? = call.env('SSO_SHARED_SECRET')
    }
}

```

</span>

<a name="checking-environment"></a>
#### [Checking your environment](#checking-environment)

The `dev.alpas.Environment` class has few more convenient properties that you could use to determine the current
environment and query some of its properties.

<div class="sublist">

<a name="production"></a>
* `isProduction`
 
Check if your app is running in production mode. If `APP_LEVEL` environment variable is set to one of **prod**,
**production**, or **live** then it is considered to be in production mode.

<a name="dev"></a>
* `isDev`

Check if your app is in development mode. If `APP_LEVEL` environment variable is set to one of **dev**, **debug**,
or **local** then it is considered to be in development mode. 

* `storagePath`

The full path to a folder named `storage` where the "byproducts" created during the runtime such as logs, file
sessions etc. are saved. This folder should always be at the root of your project during development and
next to your jar file during production.

</div>
