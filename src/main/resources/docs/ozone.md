- [Ozone Domain Specific Language (DSL)](#dsl)
    - [Ozone Table](#ozone-table)
- [Ozone Data Access Object (DAO)](#dao)
    - [Ozone Entity](#ozone-entity)
- [Creating Tables and Entities](#creating-tables-and-entities)
- [Logs](#logs)
    
Ozone is a thin layer on top of the excellent [Exposed ORM library](https://github.com/JetBrains/Exposed).
Ozone makes setting up and interacting with Exposed seamless and simple. It also adds few other
features on top of Exposed such as [entity commands](#creating-tables-and-entities),
[migrations](/docs/migrations), database pool configuration etc.

In this document, we'll talk mostly about the Ozone layer and won't go too much into the nitty-gritty details
of Exposed. For that, we'd recommend going through its [wiki](https://github.com/JetBrains/Exposed/wiki).

<a name="dsl"></a>
### [Ozone Domain Specific Language (DSL)](#dsl)

DSL allows you to interact with a database in the similar fashion of that of the actual SQL
statements. Instead of working with strings with raw SQL statements, however, DSL allows
you to make and run SQL statements in a very type safe way, as it should!

<a name="ozone-table"></a>
#### [Ozone Table](#ozone-table)

To start using DSL for your CRUD SQL statements, you need a table that is mapped to an actual table in the database.
You create this "virtual" table by extending `Table` class and defining all your columns. Since there is only
one table of the given name in the database, it makes sense to only have one such instance of table in your
code as well. You can do so by representing the table with an [`object`][kotlin-object] instead of a `class`.

<span class="line-numbers" data-start="5" data-file="tables/Users.kt">

```kotlin

object Users : Table("users") {
    val id = long("id").autoIncrement().primaryKey()
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 255).nullable()
    val createdAt = timestamp("created_at").nullable()
}

```

</span>

If your table has a primary auto-incrementing column named `id`, you can make your interactions
with the table much easier by extending it from and `IdTable` instead of `Table`.

<span class="line-numbers" data-start="5">

```kotlin

object Users : LongIdTable("users") {
    // the following is no more needed
    // val id = long("id").autoIncrement().primaryKey()

    //...
    //...
}

```

</span>

Once the table object is defined, you can start running your CRUD operations on it.

<span class="line-numbers" data-start="7">

```kotlin

//...

transaction {
    // Create a new user
    val id = Users.insertAndGetId {
        it[name] = "Jane Doe"
        it[email] = "jane@example.com"
        it[createdAt] = ZonedDateTime.now().toInstant()
    }

    // Retrieve users and "extract" their email addresses
    val emails = Users.select { Users.name eq "Jane Doe" }.map { it[email] }

    // Update users
    Users.update({ Users.id eq id }) { it[name] = "Jane M. Doe" }

    // Delete users
    Users.deleteWhere { Users.id eq id }
}

//...

```

</span>

For advanced usage, please [checkout the DSL wiki](https://github.com/JetBrains/Exposed/wiki/DSL).

<a name="dao"></a>
### [Ozone Data Access Object (DAO)](#dao)

DSL is very powerful for running queries and doing [advanced operations][dsl-advanced] but it is very convenient to
map a result to an actual object and interact with it directly. Ozone DAO APIs allows you to do exactly that.

<a name="ozone-entity"></a>
#### [Ozone Entity](#ozone-entity)

If you want to interact with your database in more of an ORM way than using SQL statements then you need
to define a class that extends the `Entity` class, "connect" it with its corresponding table object,
and map its properties with that of the table's columns. Keep in mind that the properties of the
entity should be mutable i.e. they should be **var**s and not **val**s.

Once you have set up an entity, every row in the table is mapped to an instance of this entity.

<span class="line-numbers" data-start="12" data-file="entities/User.kt">

```kotlin

class User(id: EntityID<Long>) : LongEntity(id) {
    // "connect" this entity with its table using a companion object
    companion object : LongEntityClass<User>(Users)

    // make sure the properties are mutable
    var email by Users.email
    var name by Users.name
    var createdAt by Users.createdAt
}

```

</span>

Now that you have both the entity and its corresponding table set up, you can run CRUD
operations using by interacting with an instance of the entity directly.

<span class="line-numbers" data-start="7">

```kotlin

//...

transaction {
    // Create a new user
    val user = User.new {
        name = "Jane Doe"
        email = "jane@example.com"
        createdAt = ZonedDateTime.now().toInstant()
    }

    // Retrieve users
    val users = User.find { Users.name eq "Jane Doe" }
    // or find a user
    val jane = User.findById(user.id)

    // Update users (will be committed at the end of the transaction)
    user.name = "Jane M. Doe"

    // Delete a user
    user.delete()
}

//...

```

</span>

For advanced usage, please [checkout the DAO wiki](https://github.com/JetBrains/Exposed/wiki/DAO).

<a name="creating-tables-and-entities"></a>
### [Creating Tables and Entities](#creating-tables-and-entities)

As you may have noticed, creating an entity and table with Ozone requires a little boiler code. Rather than
creating an entity and the corresponding table manually, you can use `make:entity` Alpas command.
The created entity and its table will be put in one file under `entities` folder. 

```bash

$ alpas make:entity Activity

```

<span class="line-numbers" data-start="9" data-file="entities/Activity.kt">

```kotlin
   
class Activity(id: EntityID<Long>) : LongEntity(id) {
   companion object : LongEntityClass<Activity>(Activities)

   val createdAt by Activities.createdAt
   val updatedAt by Activities.updatedAt
}

object Activities : LongIdTable("activities") {
   val createdAt = timestamp("created_at")
   val updatedAt = timestamp("updated_at")
}

```

</span>

The name of the table is automatically derived based on the entity's name. If you want to provide
a different table name you can do so by passing the name of the table to `--table` option.

```bash

$ alpas make:entity Receipt --table=invoices

```

<span class="line-numbers" data-start="9" data-file="entities/Receipt.kt">

```kotlin
   
class Receipt(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Receipt>(Invoices)

    val createdAt by Invoices.createdAt
    val updatedAt by Invoices.updatedAt
}

object Invoices : LongIdTable("invoices") {
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
   
```

</span>

<br/>

`make:entity` also takes a `-m` flag that creates a [migration](/docs/migrations) file for the entity.


### [Logs](#logs)

One major downside of using a database framework like Ozone is that the SQL queries that actually get
run on your database are opaque to you. You may be making a small innocent mistake like lazy loading
a relation instead of eagerly loading and running into the trap of [N+1 query problem][n+1].

Once quick way to see behind-the-scenes database queries is by logging them, which, fortunately,
is already done for you. All you need to do is print out the actual queries that was run by
adding the following 3 lines in your [logging configuration files][log-config].

<span class="line-numbers" data-start="32" data-file="app_log_config.xml">

```xml

<!-- ... -->

<logger name="Exposed" level="debug">
    <appender-ref ref="STDOUT"/>
</logger>

<!-- ... -->

```

</span>

> /power/ <span>Ozone is powered by [Exposed](https://github.com/JetBrains/Exposed).

[kotlin-object]: https://kotlinlang.org/docs/tutorials/kotlin-for-py/objects-and-companion-objects.html#object-declarations
[dsl-advanced]: https://github.com/JetBrains/Exposed/wiki/DSL#where-expression
[n+1]: https://stackoverflow.com/questions/97197/what-is-the-n1-selects-problem-in-orm-object-relational-mapping
[log-config]: /docs/logging#configuration
