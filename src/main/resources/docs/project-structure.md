- [Directories](#directories)
- [Files](#files)

Alpas allows you to organize your directories, files, and classes the way you want it, but we highly recommend
that you stick with the standard Alpas conventions for organizing your classes. Of course, you are free to create
new directories and files/classes as you wish.

A standard Alpas project structure look something like this:

```config

├── src/
    ├── main/
        ├── kotlin/
            ├── configs/
            ├── controllers/
            ├── database/
                ├── factories/
                ├── migrations/
                ├── seeds/
            ├── entities/
            ├── providers/
            └── ConsoleKernel.kt
            └── HttpKernel.kt
            └── routes.kt
            └── start.kt
        ├── resources/
            ├── css/
            ├── js/
            ├── templates/
                ├── errors/
                ├── layout/
            ├── web/
                ├── css/
                ├── js/
    ├── test/
        ├── kotlin/
├── storage/
└── .env
└── alpas
└── app_log_config.xml
└── console_log_config.xml
└── package.json
└── webpack.mix.js

``` 

<a name="directories"></a>
### [Directories](#directories)

**src/main/kotlin/configs**

This is where all your app's [custom configuration classes](/docs/configuration) live.

**src/main/kotlin/controllers**

A place for all your [HTTP controllers](/docs/controllers). If you wish you could create subdirectories to better 
organize your controllers and we highly recommend that you do so.

**src/main/kotlin/database/factories**

A directory for keeping all your [entity factories](/docs/entity-factory).

**src/main/kotlin/database/migrations**

A directory for all your database [migration files](/docs/migrations).

**src/main/kotlin/database/seeds**

All the [database seeders](/docs/seeding) are kept in this folder.

**src/main/kotlin/entities**

A home for your [Ozone entities](/docs/ozone).

**src/main/kotlin/providers**

This is where you should be put all your [service providers](/docs/service-providers).

**src/main/resources/css**

This directory is for putting all your [css source files](/docs/mixing-assets). This directory is referenced
only from the `webpack.mix.js` file. You could change the name or the location of this directory. If you
did so, make sure to modify the `webpack.mix.js` file accordingly.

**src/main/resources/js**

This directory is for putting all your [JavaScript source files](/docs/mixing-assets). This directory is
referenced only from the `webpack.mix.js` file. You could change the name or the location of this
directory. If you end up making a change, make sure to modify the `webpack.mix.js` file
accordingly. This folder is also where you'd put all your React/VueJS components.

**src/main/templates**

The home for all your [view templates](/docs/pebble-templates).

**src/main/templates/errors**

This directory should contain [HTTP errors](/docs/error-handling) related view templates. It comes with two templates
— one for rendering a 404 error and another for rendering 500 errors. You could modify these pre-built templates as
you wish but **don't** modify the names or relocate them.

**src/test/kotlin**

You should place all your tests in here. Feel free to organize your tests in subfolders.

**storage**

This directory contains the assets mostly created during the runtime of your app such as all your file based
[sessions](/docs/sessions), [application logs](/docs/logging), etc.

<a name="files"></a>
### [Files](#files)

Here are some of the core files of interest.

**src/main/kotlin/ConsoleKernel.kt**

This class extends the core `dev.alpas.console.ConsoleKernel` class and is responsible for registering all the
[service providers](/docs/service-providers) that should be loaded only when running
[Alpas console commands](/docs/alpas-commands).

**src/main/kotlin/HttpKernel.kt**

This class extends the core `dev.alpas.http.HttpKernel` class and is responsible for registering all the 
[service providers](/docs/service-providers) that should be loaded only when making HTTP requests.

> /info/ <span> You don't have to worry about which kernel class to load. Alpas knows what kernel class to load 
> depending on whether you are running your app in "console mode" or "server mode" and loads the appropriate kernel 
> for you automatically </span>

**src/main/kotlin/routes.kt**

This is where you should add all your [application routes](/docs/routing). All your routes defined in this file 
are loaded from `providers/RouteServiceProvider#register()` method. 

**src/main/kotlin/start.kt**

This is the entry point of your application. There should be no reason to edit or rename this file. We highly recommend 
leaving this file as it is.

**app_log_config.xml**

This is where you'd [configure your app logs](/docs/logging) when it is running in "server mode" i.e. when serving
requests.

Alpas scaffolds your project with [Logback](http://logback.qos.ch/) logging library and this xml file is configured
for it. If you decide to use a different logging library, you might have to tweak this file accordingly.

**console_log_config.xml**

This is where you'd  [configure your app logs](/docs/logging) when it is running in "console mode" i.e. when executing
[Alpas commands](/docs/alpas-commands).

> /alert/ <span>Both `app_log_config.xml` and `console_log_config.xml` files should be deployed with your app. Just
> keep both of them next to the fat jar you have created to serve your app. </span>

**webpack.mix.js**

This script is responsible for [compiling all your frontend assets](/docs/mixing-assets) such as .js scripts, 
.css files etc. 
