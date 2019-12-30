- [Custom Commands](#custom-commands)
    - [Simple](#simple-commands)
    - [Generator](#generator-commands)
    - [Registering Commands](#register)
- [Writing Output](#writing-output)

As you may have already noticed, Alpas comes with a bunch of console commands—`make:controller`, `make:job`,
`route:list` etc. to name a few—to assist you in performing some tasks from a command-line. You run an
Alpas command by prepending it with `alpas`.

To see a list of all the Alpas commands as well as a short description for each, you can use `alpas list` command.

<a name="custom-commands"></a>
### [Custom Commands](#custom-commands)

If the core Alpas commands are not enough for you, it is easy to create your own. Alpas actually wraps
[clikt][clikt] command library in its own thin wrapper. Clikt is very powerful library that makes
writing command line interfaces simple and intuitive. It has pretty much everything you would
ever need to create powerful command-line interfaces.

<a name="simple-commands"></a>
#### [Simple Commands](#simple-commands)

When writing a simple custom command, all you have to do is extend `dev.alpas.console.Command` class and
override the `run()` method. The `Command` constructor receives a number of optional parameters allowing
you to configure your commands the way you want it. This includes help text, summary text etc.

The easiest way to create a command is by using `make:command` Alpas command, which will
generate a new command under `console/commands` folder.

```bash

$ alpas make:command GreetCommand

```

<span class="line-numbers" data-start="2" data-file="console/commands/GreetCommand.kt">

```kotlin

class GreetCommand : Command(name = "greet", help = "Say hello.") {
    private val name by argument()
    override fun run() {
        success("Hello, $name!")
    }
}

```

</span>

After [registering this new command](#register), you can call it like so:

```bash

$ alpas greet you

> Hello, you!

```

<a name="generator-commands"></a>
#### [Generator Commands](#generator-commands)

Generator commands create some files and folders when invoked. `make:command` is actually an example of a
generator command and so is `make:controller`.

While you can use a simple command like `GreetCommand` above to write a generator
command, you have to wired few things to get it right.

Instead of extending `dev.alpas.console.Command` class, you can extend `dev.alpas.console.GeneratorCommand`
class to make your life much easier while writing generator commands. While it may not always satisfy
all your needs, but most of the times it does and even it doesn't, it's a good place to start.

You can pass `--generator` or `-g` to `make:command` command to create a generator type command
for you. Then all you have to do is override one abstract method—`populateOutputFile()`.

Let's see an example of how we can write a `make:sandwich` generator command that creates a `NameOfSandwich.kt`
file under `sandwiches` folder, creating this folder if it already doesn't exist.

<div class="ordered-list">

1. Create the command itself:

```bash

$ alpas make:command MakeSandwich -g

```

2. Modify `console/commands/MakeSandwich.kt` file to:

<span class="line-numbers" data-start="2" data-file="console/commands/MakeSandwich.kt">


```kotlin

class MakeSandwich(srcPackage: String) :
    GeneratorCommand(srcPackage, name = "make:sandwich", help = "Make a sandwich.") {

    override fun populateOutputFile(
        filename: String,
        actualname: String,
        vararg parentDirs: String
    ): OutputFile {

        val dir = "sandwiches"
        val file = File(sourceOutputPath(dir, *parentDirs), "${filename.toPascalCase()}.kt")

        return OutputFile()
            .target(file)
            .packageName(makePackageName(dir, *parentDirs))
            .stub(
                """
                package StubPackageName
                
                class StubClazzName {
                    fun whoAreYou() {
                        println("I am a StubClazzName sandwich")
                    }
                }
                """.trimIndent()
            )
    }

    override fun onCompleted(outputFile: OutputFile) {
        withColors {
            echo(green("MakeSandwich CREATED 🙌"))
            echo("${brightGreen(outputFile.target.name)}: ${dim(outputFile.target.path)}")
        }
    }
}

```

</span>

Notice that we have a couple of placeholders in the code—`StubPackageName` and `StubClazzName` both
of which will be replaced with proper texts automatically when we actually run the command.

3. Register this command in `ConsoleKernel` class:

<span class="line-numbers" data-start="10" data-file="ConsoleKernel.kt">


```kotlin

// ...

override fun commands(app: Application): List<Command> {
    return listOf(MakeSandwich(app.srcPackage))
}

// ...

```

</span>

4. Now we are ready to make ourselves a sandwich or two:

```bash

$ alpas make:sandwich club

```

This command will generate a `sandwiches/Club.kt` file.

You can actually create multiple sandwiches in one go:

```bash

$ alpas make:sandwich club blt

```

</div>

<a name="register"></a>
#### [Registering Custom Commands](#register)

After you have created your own commands, you must register them with the app otherwise they won't be available
to you. You can register a command in a number of ways—the easiest one is by overriding `commands()` method
in `ConsoleKernel` class and returning a list of all your commands.

<span class="line-numbers" data-start="10" data-file="ConsoleKernel.kt">

```kotlin

// ...

override fun commands(app: Application): List<Command> {
    return listOf(GreetCommand())
}

// ...

```

</span>

An alternative way is by creating and registering a new [Service Provider](/docs/service-providers).

> /tip/ <span>Clikt comes with many other powerful features such as `parameters`, `flags`, `choice options`,
>`input prompts`, `password masking` and much more. The best part of it is that the features are properly
>documented with lots of real-world examples. We highly recommend [consulting its documentation][clikt]
>when creating your own commands.</span>

<a name="writing-output"></a>
### [Writing Output](#writing-output)

When you need to write some output to the console, you can use `info`, `success`, `warning`, and `error`
methods. Most of these methods respect `--quiet` flag and also use proper ANSI colors for their purpose.

```kotlin

// ...

// prints the message in red; does NOT respect --quiet flag
error("Oops! We have a problem.")

// prints the message in green; respects --quiet flag
success("Yay! This is working.")

// ...

```

If you need more control over coloring the output, you can use `withColors()` method. Alpas uses the full-featured
text styling [Mordant][mordant] library for coloring console outputs. This allows you to color the output
anyway you want. Keep in mind that `withColors()` **won't** print anything if `--quiet` flag is set.

> /power/ <span>Alpas Console is proudly powered by [Clikt][clikt] and [Mordant][mordant].

[clikt]: https://ajalt.github.io/clikt/
[mordant]: https://github.com/ajalt/mordant
