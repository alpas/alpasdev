- [Ozone Domain Specific Language (DSL)](#dsl)
    - [Ozone Table](#ozone-table)
- [Ozone Data Access Object (DAO)](#dao)
    - [Ozone Entity](#ozone-entity)
- [Creating Tables and Entities](#creating-tables-and-entities)
- [Logs](#logs)
    
Ozone is a thin layer on top of the excellent [Ktorm library][ktorm-github] by Vincent Lau.

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
You create this "virtual" table by extending `Table` class and defining all your columns.

<span class="line-numbers" data-start="5" data-file="tables/Users.kt">

```kotlin

object Users : Table<Nothing>("users") {
   val id by long("id").primaryKey()
   val email by varchar("email")
   val name by varchar("name")
   val createdAt by timestamp("created_at")
}

```

</span>

Once the table object is defined, you can start running your CRUD operations on it.

<span class="line-numbers" data-start="7">

```kotlin

//...

// Create a new user
val id = Users.insertAndGenerateKey {
    it.name to "Jane Doe"
    it.email to "jane@example.com"
    it.createdAt to ZonedDateTime.now().toInstant()
}.toString().toLong()

// Retrieve users and "extract" their email addresses
val emails = Users.select()
    .where { Users.name eq "Jane Doe" }.map { row -> row[Users.email] }

// Update users
Users.update {
    it.name to "Jane M. Doe"
    where {
        Users.id eq id
    }
}

// Delete users
Users.delete { it.id eq id }

//...

```

</span>

>/info/<span>Since there is only one table of a given name in the database, it makes sense to only have one such
>instance of table in your code as well. You do so by representing the table as an [`object`][kotlin-object]
>instead of a `class`. However, this is not a strict requirement. There are certainly few cases where having
>multiple instances makes sense—for an example if there are two tables with an identical schema.</span>

For advanced usage, please [checkout the DSL wiki](https://ktorm.liuwj.me/en/schema-definition.html).

<a name="dao"></a>
### [Ozone Data Access Object (DAO)](#dao)

DSL is very powerful for running queries and doing [advanced queries][dsl-advanced] but it is very convenient to
map a result to an actual object and interact with it directly. Ozone DAO APIs allows you to do exactly that.

<a name="ozone-entity"></a>
#### [Ozone Entity](#ozone-entity)

If you want to interact with your database in more of an ORM way than using SQL statements then you need
to define an interface that extends the `Entity` class, "connect" a [table object](#ozone-table) with
this entity, and bind the table's properties with that of the entity's columns.

Once you have set up an entity, every row in the table is mapped to an instance of this entity.

<span class="line-numbers" data-start="12" data-file="entities/User.kt">

```kotlin

interface User : Entity<User> {
    val id: Long
    val email: String
    val name: String
    val createdAt: Instant?
    
    // this allows us to create an instance of User interface
    companion object : Entity.Factory<User>()
}


// Don't forget to bind the corresponding table to the entity

object Users : Table<User>("users") {
    val id by long("id").primaryKey().bindTo { it.id }
    val email by varchar("email").bindTo { it.email }
    val name by varchar("name").bindTo { it.name }
    val createdAt by timestamp("created_at").bindTo { it.createdAt }
}

```

</span>

Let's see how we can interact with the entity and its table.

<span class="line-numbers" data-start="7">

```kotlin

//...

// Create a new user
val user = User {
    name = "Jane Doe"
    email = "jane@example.com"
    createdAt = ZonedDateTime.now().toInstant()
}

// insert it
Users.add(user)

// Retrieve users
val users = User.find { Users.name eq "Jane Doe" }
// or find a user
val jane = User.findById(5)

// Update users and flush the changes
user.name = "Jane M. Doe"
user.email = "janemdoe@example.com"
user.flushChanges()

// Delete a user
user.delete()

//...

```

</span>

For advanced usage, please [checkout the DAO wiki](https://ktorm.liuwj.me/en/entities-and-column-binding.html).

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
   
interface Activity : Entity<Activity> {
    val id: Long
    val createdAt: Instant?
    val updatedAt: Instant?

    companion object : Entity.Factory<Activity>()
}

object Activities : MigratingTable<Activity>("activities") {
    val id by bigIncrements("id").bindTo { it.id }
    val createdAt by timestamp("created_at").nullable().bindTo { it.createdAt }
    val updatedAt by timestamp("updated_at").nullable().bindTo { it.updatedAt }
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
   
interface Receipt : Entity<Receipt> {
    val id: Long
    val createdAt: Instant?
    val updatedAt: Instant?

    companion object : Entity.Factory<Receipt>()
}

object Invoices : MigratingTable<Receipt>("invoices") {
    val id by bigIncrements("id").bindTo { it.id }
    val createdAt by timestamp("created_at").nullable().bindTo { it.createdAt }
    val updatedAt by timestamp("updated_at").nullable().bindTo { it.updatedAt }
}
   
```

</span>

<br/>

`make:entity` also takes a `-m` flag that creates a [migration](/docs/migrations) file for the entity.

<a name="column-types"></a>
### [Column Types](#column-types)

Your table can define different types of out-of-the-box supported columns.

| Function Name   | Kotlin Type             | Underlying SQL Type |
| --------------- | ----------------------- | ------------------- |
| boolean()       | kotlin.Boolean          | boolean             |
| int()           | kotlin.Int              | int                 |
| tinyInt()       | kotlin.Int              | tinyint             |
| smallInt()      | kotlin.Int              | smallint            |
| bigInt()        | kotlin.Long             | bigInt              |
| long()          | kotlin.Long             | bigint              |
| float()         | kotlin.Float            | float               |
| double()        | kotlin.Double           | double              |
| decimal()       | java.math.BigDecimal    | decimal             |
| varchar()       | kotlin.String           | varchar             |
| text()          | kotlin.String           | text                |
| mediumText()    | kotlin.String           | mediumtext          |
| longText()      | kotlin.String           | longtext            |
| char()          | kotlin.String           | char                |
| blob()          | kotlin.ByteArray        | blob                |
| bytes()         | kotlin.ByteArray        | bytes               |
| jdbcTimestamp() | java.sql.Timestamp      | timestamp           |
| jdbcDate()      | java.sql.Date           | date                |
| jdbcTime()      | java.sql.Time           | time                |
| timestamp()     | java.time.Instant       | timestamp           |
| datetime()      | java.time.LocalDateTime | datetime            |
| date()          | java.time.LocalDate     | date                |
| time()          | java.time.Time          | time                |
| monthDay()      | java.time.MonthDay      | varchar             |
| yearMonth()     | java.time.YearMonth     | varchar             |
| year()          | java.time.Year          | int                 |

<a name="logs"></a>
### [Logs](#logs)

One major downside of using a database framework like Ozone is that the SQL queries that actually get
run on your database are opaque to you. You may be making a small innocent mistake like lazy loading
a relation instead of eagerly loading and running into the trap of [N+1 query problem][n+1].

Once quick way to see behind-the-scenes database queries is by logging them, which, fortunately,
is already done for you. All you need to do is print out the actual queries that was run by
adding the following 2 lines in your [logging configuration files][log-config].

<span class="line-numbers" data-start="32" data-file="app_log_config.xml">

```xml

<!-- ... -->

<logger name="me.liuwj.ktorm.database.Database" level="debug">
</logger>

<!-- ... -->

```

</span>

>/power/<span>Ozone is proudly supercharged by [Ktorm][ktorm].</span>

>/info/<span>[Ktorm][ktorm] is not just a super fun ORM library, it is also superbly documented, including code
>comments. And Vincent's support is top-notch! He is always willing to help people with any SQL related queries.
>We also have a dedicated `#ktorm` [Slack channel][alpas-slack] where Vincent visits regularly to answer
>questions and participate in Ktorm related discussions. Please join our [Slack channel][alpas-slack]
>and say hi to him and thank him for his excellent work.</span>

[kotlin-object]: https://kotlinlang.org/docs/tutorials/kotlin-for-py/objects-and-companion-objects.html#object-declarations
[dsl-advanced]: https://ktorm.liuwj.me/en/query.html
[n+1]: https://stackoverflow.com/questions/97197/what-is-the-n1-selects-problem-in-orm-object-relational-mapping
[log-config]: /docs/logging#configuration
[alpas-slack]: https://join.slack.com/t/alpasdev/shared_invite/enQtODcwMjE1MzMxODQ3LTJjZWMzOWE5MzBlYzIzMWQ2MTcxN2M2YjU3MTQ5ZDE4NjBmYjY1YTljOGIwYmJmYWFlYjc4YTcwMDFmZDIzNDE
[ktorm-github]: https://github.com/vincentlauvlwj/Ktorm
[ktorm]: https://ktorm.liuwj.me/
