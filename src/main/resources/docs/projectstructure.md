When scaffolding a new app, Alpas only creates the basic minimum files and directories for you and no more. However, 
even these few directories could be overwhelming esp. if you are new to web framework world.

With Alpas there is some flexibility on how you could organize your classes and files, but we highly recommend that you 
stick with the standard Alpas conventions for organizing your classes. Of course, you are free to create new 
directories and files/classes as you wish.

A standard Alpas project structure look something like this:

```config

├── src/
    ├── main/
        ├── kotlin/
            ├── configs/
            ├── controllers/
            ├── database/
                ├── migrations/
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


### Directories

**src/main/kotlin/configs**

This is where all your app's configuration classes live.

**src/main/kotlin/controllers**

A place for all your Http controllers. If uou wish you could create subdirectories to better organize your controllers 
and we highly recommend that you do so.

**src/main/kotlin/database/migrations**

A directory for all your database migration files.

**src/main/kotlin/entities**

A home for your database entities.

**src/main/kotlin/RouteServiceProvider**

This is where you should be putting all your service providers.

**src/main/resources/css**

This directory is for putting all your css source files. This directory is referenced only from the `webpack.mix.js`
file. You could change the name or the location of this directory. If you did so for whatever reason, make sure to
modify the `webpack.mix.js` file accordingly.

**src/main/resources/js**

This directory is for putting all your JavaScript source files. This directory is referenced only from the 
`webpack.mix.js` file. You could change the name or the location of this directory. If you did end up changing, 
make sure to modify the `webpack.mix.js` file accordingly. This folder is also where you'd generally put all your 
React/VueJS components.

**src/main/templates**

The home for all your view templates.

**src/main/templates/errors**

This directory should contain HTTP errors related view templates. It comes with two templates for rendering a 404 
error and 500 errors. You could modify these pre-built templates as you wish but don't modify the name or move them to
some other place.

**src/test/kotlin**

You should place all your tests in here. Feel free to organize your tests in subfolders.

**storage**

This directory contains the assets mostly created during the runtime of your app such as all your file based sessions, 
application logs etc.

### Files

Here are some notable core files that you should be paying attention to:

**src/main/kotlin/ConsoleKernel.kt**

This class, which extends the core `dev.alpas.console.ConsoleKernel` class is responsible for registering all the 
service providers that should be loaded only when running `alpas` console commands.

**src/main/kotlin/HttpKernel.kt**

This class, which extends the core `dev.alpas.http.HttpKernel` class is responsible for registering all the service 
providers that should be loaded only when making HTTP requests.

> /info/ <span> You don't have to worry about which kernel class to load. Alpas knows what kernel class to load 
> depending on whether you are running your app in "console mode" or "server mode" and loads the appropriate kernel 
> for you automatically </span>

**src/main/kotlin/routes.kt**

This is where you should add all your application routes. All your routes defined in this file are loaded from 
`providers/RouteServiceProvider#register()` method. 

**src/main/kotlin/start.kt**

This is the entry point of your application. There should be no reason to edit or rename this file. We highly recommend 
leaving this file as it is.

**app_log_config.xml**

This is where you'd configure your logs for your app when it is running in "server mode" i.e. when serving requests.
Alpas scaffolds your project with [Logback](http://logback.qos.ch/) logging library and this xml file is configured 
for it. If you decide to use a different logging library, you might have to tweak this file accordingly.

**console_log_config.xml**

This is where you'd configure your logs for your app when it is running in "console mode" i.e. when executing `alpas`
commands. Alpas scaffolds your project with [Logback](http://logback.qos.ch/) logging library and this xml file 
is configured for it. If you decide to use a different logging library, you might have to tweak this file accordingly.

> /alert/ <span>Both `app_log_config.xml` and `console_log_config.xml` files should be deployed with your app. Just 
> keep both of them next to the fat jar you have created to serve your app. </span>

**webpack.mix.js**

This script is responsible for compiling all your frontend assets such as .js files and .css files. 
