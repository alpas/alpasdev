- [Server Requirements](#server-requirements)
    - [Development](#development)
    - [Production](#production)
    - [Setup](#setup)
        - [Using `sdkman`](#using-sdkman)
- [Installation](#installation)
    - [Creating New Project](#creating-new-project)
    - [Configuring](#configuring)
- [Serving](#serving)
    - [Locally](#serving-locally)
    - [Over Network](#serving-over-network)

<a name="server-requirements"></a>
### [Server Requirements](#server-requirements)

Alpas has only few system requirements. The requirements vary based on whether you want to set up a machine for
development or for production.

<a name="development"></a>
#### [Development](#development)

<div class=“sublist”>

- JDK >= 9.0
- Gradle >= 5.6

> /info/ <span>You need a GitHub account to create your project based on [Alpas’s starter template][template].
> Once created, you can host your project anywhere you would like.</span>

</div>

<a name="production"></a>
#### [Production](#production)

If you have created a [fat JAR ](https://stackoverflow.com/questions/19150811/what-is-a-fat-jar) file for serving your
app, the only requirement is the Java Runtime `jre`. If you intend to run some Alpas console commands, such as
`alpas migrate`, then you can set up your prod machine as if it was your [dev machine](#development).

<a name="setup"></a>
### [Setup](#setup)

<a name="using-sdkman"></a>
#### [Using sdkman](#using-sdkman)

You can install Alpas system requirements anyway you want. However, because of its simplicity and being able
to manage multiple versions, we highly recommend using [sdkman](https://sdkman.io/install).

<div class="ordered-list"> 

1. Install sdkman: `curl -s "https://get.sdkman.io" | bash` <span class="clipboard" data-clipboard-text='curl -s "https://get.sdkman.io" | bash'></span>
2. Restart the terminal or just source the sdk: `source ~/.sdkman/bin/sdkman-init.sh` <span class="clipboard" data-clipboard-text='source ~/.sdkman/bin/sdkman-init.sh'></span>
2. Check the version to be sure: `sdk version` <span class="clipboard" data-clipboard-text='sdk version'></span>
3. Install the minimum version of Java required: `sdk install java 9.0.7-zulu` <span class="clipboard" data-clipboard-text='sdk install java 9.0.7-zulu'></span>
4. Install the minimum version of gradle required: `sdk install gradle 5.6.4` <span class="clipboard" data-clipboard-text='sdk install gradle 5.6.4'></span>

</div>

<a name="installation"></a>
### [Installation](#installation)

After you have set up your machine, it only takes a few steps to have your new Alpas app up and running:

<a name="creating-new-project"></a>
#### [Creating New Project](#creating-new-project)

<div class="ordered-list"> 

1. Visit [Alpas starter repo on GitHub][template] and click the green **Use this template** button.
2. Give it a name. Once done, clone your new repo on your local machine.
4. At the root of the project there is a script named *alpas*. Make it executable: `chmod +x ./alpas` <span class="clipboard" data-clipboard-text='chmod +x ./alpas'></span>
5. Initialize your new project using the full package name: `./alpas init com.example.myapp` <span class="clipboard" data-clipboard-text='./alpas init com.example.myapp'></span>
6. Once done, to [serve your app](#serving-locally), do: `./alpas serve` <span class="clipboard" data-clipboard-text='./alpas serve'></span>
6. Open your new project in IntelliJ IDEA. **Optional but very highly recommended!**

</div>

<a name="configuring"></a>
#### [Configuring](#configuring)

Once the new app is initialized, you can start [configuring your app](/docs/configuration). Start with the
`.env` file that is automatically created under the project’s root directory during the initialization.

<a name="serving"></a>
<a name="serving-locally"></a>
#### [Serving Locally](#serving-locally)

You can serve your app from the command line or import it in IntellJ IDEA and then run it from within the IDE.
To serve it from the command line, use: `./alpas serve`. If everything goes well, your web app will be
available at `http://localhost:8080`

> /info/ <span>All the new apps are by default initialized to serve from port 8080. You will get an error
> if the port is already in use. If you want to use a different port, change the `APP_PORT` value in
> your `.env` file.</span>

<a name="serving-over-network"></a>
#### [Serving Over Network](#serving-over-network)

By default, when you serve your web app it is available at `http://localhost:<port>`. Sometimes it is
very convenient to have your web app accessible from a different device on the same network. This
is very helpful during development esp. if you are trying to access your web app from a mobile
device for, say, testing and debugging the app’s UI.

Alpas makes it really easy to serve your web app over an IP address. To do so set `ENABLE_NETWORK_SHARE`
to `true` in your `.env` file. Build and re-serve your app and it should be available from both
`http://localhost:<port>` and `http://<local-device-ip>:<port>`.

> /power/ <span>Alpas runs on an embedded [Jetty Web Server](https://www.eclipse.org/jetty/).

[template]: https://github.com/alpas/starter
