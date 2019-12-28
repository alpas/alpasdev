- [Server Requirements](#server-requirements)
    - [Development](#development)
    - [Production](#production)
    - [Setup](#setup)
        - [Using `brew`](#using-brew)
        - [Using `sdkman`](#using-sdkman)
- [Installation](#installation)
    - [Creating New Project](#creating-new-project)
    - [Configuring](#configuring)
- [Serving](#serving)
    - [Locally](#serving-locally)
    - [Externally](#serving-externally)

<a name=“server-requirements”></a>

### [Server Requirements](#server-requirements)

Alpas has only few system requirements. The requirements vary based on whether you want to set up a machine for
development or for production.

<a name=“development”></a>

#### [Development](#development)

<div class=“sublist”>

- JDK >= 9.0
- Gradle >= 5.6

> /info/ <span>You need a GitHub account to create your project based on [Alpas’s project template][template].
> Once created, you can host your project anywhere you would like.</span>

</div>

<a name=“production”></a>

#### [Production](#production)

If you have created a [fat JAR ](https://stackoverflow.com/questions/19150811/what-is-a-fat-jar) file for serving your
app, the only requirement is the Java Runtime `jre`. If you intend to run some Alpas console commands, such as
`alpas migrate`, then you can set up your prod machine as if it was your [dev machine](#development).

<a name=“setup”></a>
### [Setup](#setup)

<a name=“using-brew></a>
#### [Using Brew](#using-brew)

If you are a Mac user and prefer installing dependencies using [brew](https://brew.sh/), you can follow these steps:

<div class="ordered-list"> 

1. Update your brew and prepare for the needed installations: `brew update`
2. Install the latest version of Gradle: `brew install gradle`
3. Install the latest version of Java: `brew cask install java`

</div>

<a name=“using-sdkman></a>
#### [Using sdkman]($using-sdkman)

A much better way to install and manage Java dependencies and something that we highly recommend is using
[sdkman](https://sdkman.io/install):

<div class="ordered-list"> 

1. Install sdkman: `curl -s “https://get.sdkman.io” | bash`
2. Restart the terminal or just source the sdk: `source "$HOME/.sdkman/bin/sdkman-init.sh"`
2. Check the version to be sure: `sdk version`
3. Install the minimum version of Java required: `sdk install java 9.0.7-zulu`
4. Install the minimum version of gradle required: `sdk install gradle 5.6.4`

</div>

<a name=“installation”></a>
### [Installation](#installation)

After you have set up your machine, it only takes a few steps to have your new Alpas app up and running:

<a name=“creating-new-project”></a>
#### [Creating New Project](#creating-new-project)

<div class="ordered-list"> 

1. Visit [Alpas app repo on GitHub][template].
2. Click the green **Use this template** button and give it a name.
3. Once the repo is created, clone your new repo on your local machine.
4. At the root of the project there is a script named `alpas`; make it executable:

```bash
    chmod +x ./alpas
```

5. Initialize your new project using the full package name:

```bash
    ./alpas init com.example.myapp
```

6. Import your new project in IntelliJ IDEA. **Optional but highly recommended!**

</div>

> /tip/ <span>If you don’t want to type `./` with every Alpas commands, you could append `.`
> to your `$PATH` variable.</span>

<a name=“configuring”></a>
#### [Configuring](#configuring)

Once the new app is initialized, you can start [configuring your app](/docs/configuration). Start with the
`.env` file that is automatically created under the project’s root directory during the initialization.

<a name=“serving-locally”></a>
#### [Serving Locally](#serving-locally)

You can serve your app from the command line or import it in IntellJ IDEA and then run it from within the IDE.
To serve it from the command line, use: `./alpas serve`. If everything goes well, your web app will be
available at `http://localhost:8080`

> /info/ <span>All the new apps are by default initialized to serve from port 8080. You will get an error
> if the port is already in use. If you want to use a different port, change the `APP_PORT` value in
> your `.env` file.</span>

<a name=“serving-externally”></a>
#### [Serving Externally](#serving-locally)

By default, when you serve your web app it is available at `http://localhost:<port>`. Sometimes it is
convenient to have your web app accessible from a different device on the same network. This is
very helpful during development esp. if you are trying to access your web app from a mobile
device for, say, testing and debugging the app’s UI.

Alpas makes it really easy to serve your web app over an IP address. To do so add `SERVE_EXTERNALLY=true`
in your `.env` file. Build and re-serve your app and it should be available from both
`http://localhost:<port>` and `http://<local-device-ip>:<port>`.

> /power/ <span>Alpas runs on an embedded [Jetty Web Server](https://www.eclipse.org/jetty/).

[template]: https://github.com/ashokgelal/framework
