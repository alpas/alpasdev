- [Registering Database Connections](#registering-database-connections)
    - [Multiple Database Connections](#multiple-database-connections)
- [Transactions](#transactions)
    - [Accessing Transaction Values](#accessing-transcation-values)
    - [Different Database Connections](#different-database-connections)
    
Just like with any other libraries, Java and Kotlin ecosystem is very rich when it comes to interacting
with databases. There are many choices for ORMs, connection pools, configurations, migrations etc.

Instead of tiring you out with decision fatigue and confusing you with configurations and terminologies,
we have done all the database setup out-of-the-box for you. This means, with Alpas, interaction
with a database is as easy and as hassle-free as it can be.

With a simple to configure single or [multiple database](#multiple-database-connections) connections, the
[fastest JDBC database pool connection][hikari], [migrations](/docs/migrations), and the
[Ozone SQL framework](/docs/ozone) all packed and ready to go, you'll be looking
forward to interacting with your database and running queries like a pro.

<a name="registering-database-connections"></a>
### [Registering Database Connections](#registering-database-connections)

If you open the `configs/DatabaseConfig.kt` file, you'll notice that Alpas has registered one database connection
for you already. If `addConnections()` call is commented out, make sure to uncomment it out as the first thing.

The default connection that is configured for you is a MySQL database connection and is registered
as `mysql`. However, it could be any of number of other supported database dialects.

<div class="sublist">

- MySQL
- MariaDB
- PostgreSQL
- SQLite
- Oracle
- H2
- SQL Server

</div>

The database connection name in the config file is very important as this is what gets selected based
on `DATABASE_CONNECTION` value in your `.env` file. As you might have guessed already, this is set to
`mysql` by default. You can [add more than one connection](#multiple-database-connections), of course!

You want to make sure that `OzoneServiceProvider::class` is added to the list of
[service providers](/docs/service-providers#registering) in both
`HttpKernel` and `ConsoleKernel` classes

You can create your own database connection by implementing `dev.alpas.ozone.DatabaseConnection`
interface. To help you get started without any fuss or fear, Alpas comes bundled with two
such connections—`MySqlConnection` and `SqliteConnection`.

>/alert/<span>Most of the database related features are disabled unless there exists at least
>one database connection. Make sure you have a `DatabaseConfig` class defined and that
>`addConnections()` method is called from within the `init{}` block.</span>

<a name="multiple-database-connections"></a>
#### [Multiple Database Connections](#multiple-database-connections)

You can add multiple database connections under different names and use these names to connect or switch between
them during runtime. You are not restricted to creating multiple connections with only different database vendors
or different databases. You can declare multiple connections even for the same vendor or even the same database.
This allows you to, for an example, run different types of database queries on different databases.

You add multiple connections by declaring them in your `DatabaseConfig` class.

<span class="line-numbers" data-start="9" data-file="configs/DatabaseConfig.kt">

```kotlin

// ...

class DatabaseConfig(env: Environment) : DatabaseConfig(env) {
    init {
        addConnection( "mysql", lazy { MySqlConnection(env) } )

        // configure the connection config to connect to a different database
        val readonlyConfig = ConnectionConfig(database = "myreadonlydb", host="192.168.1.1")
        addConnection("mysql-readonly", lazy { MySqlConnection(env, readonlyConfig) })

        addConnection("sqlite", lazy { SqliteConnection(env) })
    }
}

```

</span>

Once these connections are added, you can easily connect to the database of your
choice by calling `connect()` method on an instance of `DatabaseConfig`.

<span class="line-numbers" data-start="6">

```kotlin

fun index(call: HttpCall){
    val readonlyDb = call.make<DatabaseConfig>().connect("mysql-readonly")
    readonlyDb {
        // You can now use the readonlyDb object to run SQL
        // queries on the "mysql-readonly" database.
    }
}

```

</span>

<a name="transactions"></a>
### [Transactions](#transactions)

In case of some errors, if you want to recover gracefully from any kind of **CRUD** database operations—**C**reate,
**R**etrieve, **U**pdate, and **D**elete—you can wrap it in a `useTransaction` block. If an exception is thrown
within this block, an auto rollback of the database will be performed and hence ensuring that your
database is in the correct state and that the data consistency is maintained.

<span class="line-numbers" data-start="5">

```kotlin

//...

useTransaction {

    // Run CRUD operations here

}

//...

``` 

</span>

When you wrap your CRUD operations in a `useTransaction` block, you are invoking them in the context of the default
database connection. This is the `DATABASE_CONNECTION` value from your `.env` file and whatever connection you
have created with that value when [registering the connections](#registering-database-connections).

If you need to run some CRUD operations on a different connection, you can
[do that easily as well](#different-database-connections)!

<a name="accessing-transcation-values"></a>
#### [Accessing Transaction Values](#accessing-transcation-values)

If you want to access some values of a `useTransaction` block outside the block, then you can declare
some mutable **var**s outside the block and assign them inside the block. If this sounds awful
then `useTransaction` block actually returns the value of the last expression in the block.
You can assign this to a **val** and use it outside the block.

<span class="line-numbers" data-start="5">

```kotlin

//...

val users = useTransaction {
    //...
    // fetch users from the database, for an example
}

//...

``` 

</span>

<a name="different-database-connections"></a>
#### [Different Database Connections](#different-database-connections)

By default, **CRUD operations run on the last database that was connected**. In Alpas, the default database
is loaded the first time your application starts. This means, a "naked" database operation—operation
outside a db block—runs in the context of this database. However, you can easily run SQL
statements on a specific database by passing an instance of a database object
obtained by calling [`connect()` method](#multiple-database-connections).

```kotlin

fun index(call: HttpCall){

    // CRUD operations here are run on the default database 
    useTransaction {
        // CRUD operations here are run on the default database in a transaction
    }

    val dbmysql1 = call.make<DatabaseConfig>().connect("mysql")

    // use a new connection and also switch to it
    val dbmysql2 = call.make<DatabaseConfig>().connect("mysql-readonly")

    dbmysql1 {
        // CRUD operations here are run on the "mysql" database 
    }

    dbmysql1.useTransaction {
        // CRUD operations here are run on the "mysql" database in a transaction
    }

    // CRUD operations here are run on the "mysql-readonly" database 

    useTransaction {
        // CRUD operations here are run on the "mysql-readonly" database in a transaction
    }
}

```

>/alert/<span>Don't forget that the "naked" database operations are always run on the last connected
>database. This is the common source of bugs and confusion when working with multiple databases.

[hikari]: https://github.com/brettwooldridge/HikariCP#jmh-benchmarks-checkered_flag
