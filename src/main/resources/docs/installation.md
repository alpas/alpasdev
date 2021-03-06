- [System Requirements](#system-requirements)
    - [Development](#development)
    - [Production](#production)
    - [Setup](#setup)
        - [Using `sdkman`](#using-sdkman)
- [Installation](#installation)
    - [Creating New Project](#creating-new-project)
    - [Configuring](#configuring)
- [Running](#running)
    - [Command Line](#running-commandline)
    - [IntelliJ](#intellij)
    - [Over the Network](#over-network)
    - [Auto Port Selection](#auto-port-selection)

<a name="system-requirements"></a>
### [System Requirements](#system-requirements)

Alpas has only a few system requirements. The requirements vary based on whether you
want to set up a machine for development or for production.

<a name="development"></a>
### [Development](#development)

Alpas is easy to get started with on any *nix based machines; all you need is a JDK. Windows
is supported but only under WSL or using GitBash.

<div class="sublist">

- macOS or Linux
- Windows support is only available with WSL or GitBash.
- JDK >= 9.0
- Gradle >= 5.6
- [IntelliJ IDEA Community or Ultimate][intellij] (Optional, but highly recommended)

</div>

>/watch/<span> Watch [**Setting up GitBash and SDKMan on Windows**](https://kutt.it/sDS63q) and
>[**Alpas Installation on Windows With WSL**](https://kutt.it/18hxT8) by
>[*AlpasCasts*](https://kutt.it/XnILn0).</span>

> /info/ <span>You need a GitHub account to create your project based on [Alpas’s starter template][template].
> Once created, you can host your project anywhere you would like.</span>

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
6. Once done, to [run your app](#running), do: `./alpas serve` <span class="clipboard" data-clipboard-text='./alpas serve'></span>
6. Open your new project in IntelliJ IDEA. **Optional but very highly recommended!**

</div>

<a name="configuring"></a>
#### [Configuring](#configuring)

Once the new app is initialized, you can start [configuring your app](/docs/configuration). Start with the
`.env` file that is automatically created under the project’s root directory during the initialization.

<a name="running"></a>
### [Running](#running)

<a name="running-commandline"></a>
#### [Running from the Command Line](#running-commandline)

You can serve your app from the command line or import it in IntelliJ IDEA and then run it from within the IDE.
To serve it from the command line, use: `./alpas serve`. If everything goes well, your web app will be
available at `http://localhost:8080`

#### [Running from IntelliJ](#intellij)

To run with IntelliJ, you will need to ensure the project is setup to run Java 9. 

<div class="ordered-list">

1. To check, click on `File > Project Structure > Project` and see what is selected for Project SDK. 
2. To switch to Java 9, from the same window, click `Platform Settings > SDKs > + > Add SDK` and then
locate the path to where the Java 9 folder is located. If you installed Java 9 using *sdkman* per
the setup instructions above, then the path is likely `~/.sdkman/candidates/java`.

Once you have added JDK 9, go back to step one and select Java 9.

</div>

To run the project in IntelliJ, use the project navigation to select `src/main/kotlin/start.kt` and 
`right-click > Run...`. You can also open this file instead and hit the green play (►) button.
If everything goes well, your web app will be available at http://localhost:8080

>/watch/<span> Watch [**Installing IntelliJ IDEA and Serving an Alpas App on Mac**](https://kutt.it/ZdIyO1)
>by [*AlpasCasts*](https://kutt.it/XnILn0).</span>

<a name="over-network"></a>
#### [Serving Over the Network](#over-network)

By default, when you serve your web app it is available at `http://localhost:<port>`. Sometimes it is
very convenient to have your web app accessible from a different device on the same network. This
is very helpful during development especially if you are trying to access your web app from a mobile
device for, say, testing and debugging the app’s UI.

Alpas makes it really easy to serve your web app over an IP address. To do so set `ENABLE_NETWORK_SHARE`
to `true` in your `.env` file. Build and re-serve your app and it should be available from both
`http://localhost:<port>` and `http://<local-device-ip>:<port>`.

> /alert/ <span>All the new apps are by default initialized to serve from port 8080. You will get an error
> if the port is already in use. If you want to use a different port, change the `APP_PORT` value in
> your `.env` file.</span>

<a name="auto-port-selection"></a>
### [Auto Port Selection](#auto-port-selection)

You can set the port on which your app gets served in your `.env` file. However, the port you have set in your
`.env` file may not always be available as there could be another app already running on that port.

In order to not throw an error and to not force you to make a temporary change in your `.env` file,
***in development mode***, Alpas automatically selects a different port in case that port is busy.

When you run your app again, if the selected port is free, it switches back to the original port otherwise it continues
to serve on a temporary port. Pay attention to the port address printed on the console to know which temporary port
is selected to serve your application. This temporary port selection is not available in production mode. 

> /power/ <span>Alpas runs on an embedded [Jetty Web Server](https://www.eclipse.org/jetty/).

[template]: https://github.com/alpas/starter
[intellij]: https://www.jetbrains.com/idea/download
