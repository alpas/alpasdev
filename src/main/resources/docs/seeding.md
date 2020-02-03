- [Getting Started](#getting-started)
- [Creating Seeder](#creating-seeder)
- [Running Seeder](#running-seeder)

You can use a seeder class to quickly populate your database with some dummy data. This is very useful
during testing as well as for a demo apps. Coupled with an [Entity Factory](/docs/entity-factory),
this is a very powerful tool to cook some test data really quick.

<a name="getting-started"></a>
### [Getting Started](#getting-started)

A seeder is just a class, or an object, extending the abstract `dev.alpas.ozone.Seeder` class.
The extending class is required to override the only abstract method — `run()`.

The `run()` methods receives an instance of `Application` to make it easier for you to resolve dependencies if any.
Inside the `run()` method, you can populate your data and insert them into a database in any way you want. However,
you may want to hook it up with an [Entity Factory](/docs/entity-factory) for quickly creating entity instances.

>/info/<span>A seeder can be named anything but they usually end with the suffix `Seeder`. They are also
>conventionally kept in `database/seeds` folder.</span>

<a name="creating-seeder"></a>
### [Creating a Seeder](#creating-seeder)

You can create a seeder by extending the abstract `Seeder` class or use `make:seeder` command
to create one or multiple seeders in a jiffy.

```bash

# creates 3 seeders under database/seeds folder
$ alpas make:seeder ArticlesSeeder UsersSeeder CommentsSeeder

```

<span class="line-numbers" data-start="6" data-file="database/seeds/ArticlesSeeder.kt">

```kotlin

internal class ArticlesSeeder : Seeder() {
    override fun run(app: Application) {
        // Do something
    }
}

```

</span>

<a name="running-seeder"></a>
### [Running a Seeder](#running-seeder)

You can run a seeder by using the `db:seed` Alpas console command. This command optionally takes the name
of the seeder to run. If no name is passed, it runs the `DatabaseSeeder` class. If this class doesn't
exist then it will throw an error.

```bash

# Runs the default DatabaseSeeder
$ alpas db:seed 

# Runs the ArticlesSeeder
$ alpas db:seed ArticlesSeeder

```

You can run multiple seeders by calling their `run()` methods from another seeder's `run()` method.
Here is an example of running 3 seeders from within a `DatabaseSeeder` class.

<span class="line-numbers" data-start="6" data-file="database/seeds/DatabaseSeeder.kt">

```kotlin

internal class DatabaseSeeder : Seeder() {
    override fun run(app: Application) {
        UsersSeeder().run(app)
        ArticlesSeeder().run(app)
        CommentsSeeder().run(app)
    }
}

```

</span>

Now, if you run the `DatabaseSeeder` by using `alpas db:seed`, it will effectively run
`UserSeeder`, `ArticleSeeder`, and `CommentSeeder`.
