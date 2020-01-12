- [Registering Database Connections](#registering-database-connections)
    - [Multiple Database Connections](#multiple-database-connections)
- [Transactions](#transactions)
    - [Accessing Transaction Values](#accessing-transcation-values)
    - [Nested Transactions](#nested-transaction)
    - [Different Database Connections](#different-database-connections)
    
Instead of tiring you out with decision fatigue and confusing you with configurations and terminologies,
we have carefully picked a SQL framework library and have done all the setup out-of-the-box for you
to make interaction with a database as easy and as hassle-free as possible.

With an easy single or [multiple database connections](#multiple-database-connections) configuration, the
[fastest JDBC database pool](https://github.com/brettwooldridge/HikariCP#jmh-benchmarks-checkered_flag)
connection, [migrations](/docs/migrations), and [Ozone SQL framework](/docs/ozone), you will look
forward to interacting with your database and making SQL queries like a pro.

<a name="registering-database-connections"></a>
### [Registering Database Connections](#registering-database-connections)

If you open the `configs/DatabaseConfig.kt` file, you'll notice that Alpas has registered one database connection
for you already. If `addConnections()` call is commented out, make sure to uncomment it out as the first thing.

The default connection is a MySQL database connection and should be registered as `mysql`.
However, it could be any of number of other supported database dialects.

<div class="sublist">

- MySQL
- MariaDB
- PostgreSQL
- SQLite
- Oracle
- H2
- SQL Server

</div>

The connection name is important as this is what gets selected based on `DATABASE_CONNECTION` value
in your `.env` file. As you can guess, this is set to `mysql` by default. You can
[add more than one connection](#multiple-database-connections), of course!

You want to make sure that `OzoneProvider::class` is added to the list of
[service providers](/docs/service-providers#registering) in both
the kernel classes-`HttpKernel` and `ConsoleKernel`.

You can create your own database connection by implementing `dev.alpas.ozone.DatabaseConnection` interface.
To help you get started without much fuss and fear, Alpas comes bundled with two such
connections—`MySqlConnection` and `SqliteConnection`.

>/alert/<span>Most of the database related features are disabled unless there exists at least
>one database connection. Make sure you have a `DatabaseConfig` class defined and that
>`addConnections()` method is called from the `init{}` block.</span>

<a name="multiple-database-connections"></a>
#### [Multiple Database Connections](#multiple-database-connections)

You can add multiple database connections under different names and use these names to connect to different databases
during runtime. You are not restricted to creating multiple connections with only different database vendors or
different databases. You can declare multiple connections even for the same vendor or even the same database.
This allows you to, for an example, run different types of database queries on different databases.

You add multiple connections by declaring them in your `DatabaseConfig` class.

<span class="line-numbers" data-start="9" data-file="configs/DatabaseConfig.kt">

```kotlin

// ...

class DatabaseConfig(env: Environment) : DatabaseConfig(env) {
    init {
        addConnection( "mysql", lazy { MySqlConnection(env) } )

        // configure the connection config to connect to a different database
        val readonlyConfig = ConnectionConfig(database = "myreadonlydb", host="192.168.1.11")
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
    val db = call.make<DatabaseConfig>().connect("mysql-readonly")
    try {
        // You can now use the db object in a transaction to run
        // SQL queries on "mysql-readonly" database.
    } finally {
        // don't forget to close the connection once done
        db.connector().close()
    }
}

```

</span>

<a name="transactions"></a>
### [Transactions](#transactions)

Every **CRUD** database operations—**C**reate, **R**etrieve, **U**pdate, and **D**elete—**must**
be called from within a `transaction` block.

<span class="line-numbers" data-start="5">

```kotlin

//...

transaction {

    // Run CRUD operations here
    
}

//...

``` 

</span>

When you wrap your CRUD operations in a transaction, you are invoking them in the context of the default database
connection. This is the `DATABASE_CONNECTION` value from your `.env` file and whatever connection you have
created with that value when [registering the connections](#registering-database-connections).

If you need to run some CRUD operations on a different connection, you can
[do that easily as well](#different-database-connections)!

>/alert/<span> If you don't wrap your CRUD operations with a `transaction` block, you'll get a runtime exception.</span>

<a name="accessing-transcation-values"></a>
#### [Accessing Transaction Values](#accessing-transcation-values)

If you want to access some values of a `transaction` block outside the block, then you can declare some
mutable **var**s outside the block and assign them inside the block. If this sounds awful then
`transaction` block actually returns the value of the last expression in the block. You
can assign this to a **val** and use it outside the block.

<span class="line-numbers" data-start="5">

```kotlin

//...

val users = transaction {
    //...
    // fetch users from the database
}

//...

``` 

</span>

<a name="nested-transaction"></a>
#### [Nested Transactions](#nested-transaction)

Transactions get committed automatically at the end. If an exception occurs before the
transaction gets committed, it will perform an automatic rollback as well.

If you need finer control over transactions, you can use nested transactions. If an exception is thrown
within the nested transaction, only this transaction will rollback but not any of the outer
transactions. Because there is a performance penalty with nested transactions, they are
disabled by default. You can enable it by setting `useNestedTransactions` to `true`
when [registering a database connection](#registering-database-connections).

<span class="line-numbers" data-start="9" data-file="configs/DatabaseConfig.kt">

```kotlin

// ...

class DatabaseConfig(env: Environment) : DatabaseConfig(env) {
    init {
        addConnection(
            "mysql",
            lazy { MySqlConnection(env, ConnectionConfig(useNestedTransactions = true)) }
        )
    }
}

```

</span>

<a name="different-database-connections"></a>
#### [Different Database Connections](#different-database-connections)

By default, `transaction` runs its body on the last database that was connected. In Alpas, the default database
is loaded the first time your application starts. This means, a "naked" `transaction` runs in the context of
this database. However, you can easily run SQL statements on a specific database by passing an instance
of a database object obtained by calling [`connect()` method](#multiple-database-connections).

```kotlin

fun index(call: HttpCall){

    transaction {
        // Run CRUD operations here on the default database 
    }

    val db = call.make<DatabaseConfig>().connect("mysql-readonly")
    try {
        transaction(db) {
            // Run CRUD operations here on "mysql-readonly" database 
        }
    } finally {
        db.connector().close()
    }
}

```
