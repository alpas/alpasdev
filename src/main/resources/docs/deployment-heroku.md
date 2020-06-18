- [Prerequisites](#prerequsities)
- [Preparing Your Alpas App](#preparing-your-alpas-app)
    - [Adding Additional Files](#adding-additional-files)
    - [Altering Existing Files](#altering-existing-files)
- [Preparing Your Heroku Environment](#preparing-your-heroku-environment)
    - [Configuring the Environment](#configuring-the-environment)
    - [Setting Up MySQL](#setting-up-mysql)
- [Deploying and Running Migrations](#deploying-and-running-migrations)
- [Subsequent Deployments](#subsequent-deployments)
- [Troubleshooting](#troubleshooting)
    - [Migrating with the Compiled Project](#migrating-with-the-compiled-project)

These instructions will step you through the requirements to deploy an existing Alpas app to Heroku. 

<a name="prerequsities"></a>
### [Prerequisites](#prerequsities)

<div class="sublist">

* An existing Alpas app running locally with a MySQL database connection, checkout out the [Quick Start Guide](https://alpas.dev/docs/quick-start-guide-todo-list) to get your first Alpas up and running. 
* A [Heroku account](https://heroku.com/) as well as the [Heroku CLI Tools](https://devcenter.heroku.com/articles/heroku-cli) installed

</div>

<a name="preparing-your-alpas-app"></a>
## [Preparing Your Alpas App](#preparing-your-alpas-app)

<a name="adding-additional-files"></a>
### [Adding Additional Files](#adding-additional-files)

Heroku reads from a special `Procfile` to run your application which should be in the root of your project.  This file should contain the command to execute the project's jar file:

>*Procfile*
>
>```web:  java -jar ./myApp.jar```

Additionally, you need a `system.properties` file which will specify for Heroku the Java Runtime Environment (JRE) that is required to run the project:

>*system.properties*
>
>```java.runtime.version=1.9```

Heroku randomly assigns a port in its environment for you to serve your app from. This is available from the system environment variable `"PORT"` but you won't know what it is until runtime, so we can't store it as a concrete environment variable.  Instead, the following allows you to read what the port number is when running and allow your app to be served up there, defaulting to 8080 in your local environment:

>*src/main/kotlin/configs/PortConfig.kt*
>
>```
>package com.example.myApp.configs
>
>import dev.alpas.AppConfig
>import dev.alpas.Environment
>
>@Suppress("unused")
>class PortConfig(env: Environment) : AppConfig(env) {
>    override val appPort = env("PORT", 8080)
>}
>
>```

<a name="altering-existing-files"></a>
### [Altering Existing Files](#altering-existing-files)

We need to ensure that we are using Alpas version >=`0.16.3` since this allows us to explicitly set the `APP_HOST` variable
(required later). Check the following and update accordingly in your `build.gradle` file:

>*build.gradle*
>
>`ext.alpas_version = '0.16.3'` 

Alpas needs a `.env` file in the production environment to run migration scripts amongst other processes. As per the [Alpas docs](https://alpas.dev/docs/configuration#environment) you shouldn't commit one, so we'll create an empty one in our route directory if it doesn't exist whenever the `main` app function is invoked:

>*src/main/kotlin/start.kt*
>
>```
>package com.example.myApp
>
>import dev.alpas.Alpas
>import java.io.File
>
>fun main(args: Array<String>) {
>    val file = File(".env")
>    if (!file.exists()) {
>        file.createNewFile()
>    }
>    return Alpas(args).routes { addRoutes() }.ignite()
>}
>```

Finally - go ahead and rebuild your project:

>`./alpas jar`

<a name="preparing-your-heroku-environment"></a>
## [Preparing Your Heroku Environment](#preparing-your-heroku-environment)

<a name="configuring-the-environment"></a>
### [Configuring the Environment](#configuring-the-environment)

You're now ready to set up your Heroku environment:

>`heroku create`

This will create an app in your account and set it as a remote for this project. Logging into your account via the browser
navigate to the `App > Settings` section and click on `Reveal Config Vars`.
 
You will now be able to add in all the contents of your `.env` file. Note, you can also do this via the command line with `heroku config:set {KEY}="{VALUE}"`. Some additional important variables to add:
 
>* `APP_HOST = 0.0.0.0`  This binds your app to run on `0.0.0.0` rather than localhost (`127.0.0.1`) which is essential for Heroku. Remember, you need to be using Alpas 0.16.3 or greater to get this to work.
>* `GRADLE_TASK = shadowJar` This tells Heroku how to build your gradle project

Some variables will need to be altered/removed compared to your `.env` file:
>* Any of the `DB` configs - we will add these once we have provisioned a Heroku database
>* `APP_PORT` - this should not be added as we're dynamically deriving this from our `PortConfig.kt` file
>* `APP_LEVEL = prod` this will put your app into production mode

<a name="setting-up-mysql"></a>
### [Setting Up MySQL](#setting-up-mysql)

On Heroku navigate to `Resources` and search for mysql.  Heroku supports a number of MYSQL database providers. Once installed click on the add-on in Heroku, this will take you to its dashboard page which has some important information:

* The host URL
* The username - note this will most likely not be root and be automatically provisioned
* The password
* The database name - if you're using JawsDB on the free tier it will automatically create one for you, you cannot create additional dbs without upgrading to a paid plan
* The port number

Add these keys to your Heroku `Settings > Config Vars` with the following values:

>* `DB_HOST = {The host URL}`
>* `DB_CONNECTION = mysql`
>* `DB_DATABASE = {The database name}`
>* `DB_PORT = {The port}`
>* `DB_USERNAME = {The username}`
>* `DB_PASSWORD = {The password}`

<a name="deploying-and-running-migrations"></a>
## [Deploying and Running Migrations](#deploying-and-running-migrations)

You are now ready to deploy to Heroku!  Make sure you have a compiled jar file in your project root:

>`./alpas jar`

Then `git push heroku master` - Heroku will then detect that it needs to install the right JDK version (as per our `system.properties` file) and build a gradle project as per the `shadowJar` value we gave it earlier. This should be up and running at your designated Heroku url.

Navigating to this should give us a big ol' 500 error (but the nice shiny one from Alpas) - time to run a migration!

In order to successfully migrate on the free tier of Heroku, you need to temporarily bring down your app as there is not enough RAM on the dyno to both serve the app and run the migration:

>`heroku ps:scale web=0`

Then run the migration on Heroku:

> `heroku run ./alpas db:migrate`

Once that has successfully executed you can then bring back up the app

> `heroku ps:scale web=1`

Refreshing your browser should bring up your home page and you are up and running in Heroku!

<a name="subsequent-deployments"></a>
## [Subsequent Deployments](#subsequent-deployments)

Having successfully deployed to Heroku, future deployments follow three simple steps:

>1. Commit your changes and recompile the project `./alpas jar`
>2. Run `git push heroku master` to deploy to Heroku
>3. If any migrations are required, follow the [migration steps](https://github.com/GideonBrimleaf/alpacasToDo#part-three---deploying-and-running-migrations) above

<a name="troubleshooting"></a>
## [Troubleshooting](#troubleshooting)

You may find that the migration exits too early because the dyno capacity on the free tier has been maxed out.  If this happens try making a trivial change to your project to force a new deploy (with the Heroku web process set to 0), navigate to `App > More > Restart All Dynos` to reset the box.  Then try to run the migration command.

Alternatively try `App > More > Restart All Dynos` followed by `heroku ps:scale web=0` to ensure that the app is not running when trying to run a migration.

### [Migrating with the Compiled Project](#migrating-with-the-compiled-project)

If the problem persists, you will need to run the `db:migrate` command on the compiled jar file.  The normal `alpas` script recompiles the entire project before running the migration which likely requires too much RAM for the lower/free tier dynos.

1. Create a new `alpas_prod.sh` file in the root of your project.
2. Copy and paste in the contents of [this sample file](https://gist.github.com/GideonBrimleaf/fb57c60f5b10c547d0f88468d4aaa9ad) into your `alpas_prod.sh` file.  This is very similar to the original `alpas` script but runs against the already compiled jar file rather than using gradle commands which recompile the project. Be sure to rename the jar file reference in the script to your project's jar file after copying over. 
3. As per the original `alpas` script, make sure this new file has executable rights with `chmod +x ./alpas_prod.sh`
4. Step through [Deploying and Running Migrations](#deploying-and-running-migrations) above, substituting `heroku run ./alpas_prod.sh db:migrate` in for the migration command.  This will execute the migration without recompiling the project. 