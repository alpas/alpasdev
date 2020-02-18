- [Ozone Domain Specific Language (DSL)](#dsl)
    - [Ozone Table](#ozone-table)
- [Ozone Data Access Object (DAO)](#dao)
    - [Ozone Entity](#ozone-entity)
- [Creating Tables and Entities](#creating-tables-and-entities)
- [Inserting New Entity](#inserting-new-entity)
    - [Batch Inserting Entities](#batch-inserting)
- [Finding Entity](#finding-entity)
    - [Finding Or Creating](#finding-creating-entity)
- [Updating Entity](#updating-entity)
- [Deleting Entity](#deleting-entity)
- [Default Column Types](#default-column-types)
- [Column Binding Conventions](#column-binding-conventions)
- [Query Logging](#query-logging)
    
Ozone is a thin layer on top of the excellent [Ktorm][ktorm-github] library. Ozone comes bundled with lots of
features needed to write a modern web app such  as [entity commands](#creating-tables-and-entities),
[migrations](/docs/migrations), database pool configuration, [entity factories](/docs/entity-factory),
[seeding](/docs/seeding) etc.

<a name="dsl"></a>
### [Ozone Domain Specific Language (DSL)](#dsl)

DSL allows you to interact with a database in the similar fashion of that of the actual SQL
statements. Instead of working with strings with raw SQL statements, however, DSL allows
you to make and run SQL statements in a very type safe way, as it should!

<a name="ozone-table"></a>
#### [Ozone Table](#ozone-table)

To start using DSL for your CRUD SQL statements, you need a table that is mapped to an actual table in the database.
You create this "map" table—basically, a schema—by extending the `OzoneTable` class and defining all your columns.

<span class="line-numbers" data-start="5" data-file="tables/Users.kt">

```kotlin

object Users : OzoneTable<Nothing>("users") {
   val id by long("id").primaryKey()
   val email by varchar("email")
   val name by varchar("name")
   val createdAt by timestamp("created_at")
}

```

</span>

Once the table object is defined, you can start running your CRUD operations on it.

```kotlin

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

```

>/info/<span>Since there is only one table of a given name in the database, it makes sense to only have one such
>instance of table in your code as well. You do so by representing the table as an [`object`][kotlin-object]
>instead of a `class`. However, this is not a strict requirement. There are certainly a few cases where having
>multiple instances makes sense — for an example, if there are two tables with an identical schema.</span>


<a name="dao"></a>
### [Ozone Data Access Object (DAO)](#dao)

DSL is very powerful for running queries and doing [advanced queries][dsl-advanced], but it is very convenient to
map a result to an actual object and interact with it directly. Ozone DAO APIs allows you to do exactly that.

<a name="ozone-entity"></a>
#### [Ozone Entity](#ozone-entity)

If you want to interact with your database in more of an ORM way than using SQL statements then you need
to define an interface that extends the `OzoneEntity` class, "connect" a [table object](#ozone-table) with
this entity, and bind the table's properties with that of the entity's columns.

Once you have set up an entity, every row in the table is mapped to an instance of this entity.

<span class="line-numbers" data-start="12" data-file="entities/User.kt">

```kotlin

interface User : OzoneEntity<User> {
    val id: Long
    val email: String
    val name: String
    val createdAt: Instant?
    
    // this allows us to create an instance of User interface
    companion object : OzoneEntity.Of<User>()
}

// Don't forget to bind the corresponding table to the entity
object Users : OzoneTable<User>("users") {
    val id by long("id").primaryKey().bindTo { it.id }
    val email by varchar("email").bindTo { it.email }
    val name by varchar("name").bindTo { it.name }
    val createdAt by timestamp("created_at").bindTo { it.createdAt }
}

```

</span>

Let's see how we can interact with the entity and its table.

```kotlin

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

```

<a name="creating-tables-and-entities"></a>
### [Creating Tables and Entities](#creating-tables-and-entities)

As you may have noticed, creating an entity and table with Ozone requires a little boilerplate code.
Rather than creating an entity and the corresponding table by hand, you can use `make:entity`
Alpas command. The entity and its table will be put in one file under the `entities` folder. 

```bash

$ alpas make:entity Activity

```

<span class="line-numbers" data-start="9" data-file="entities/Activity.kt">

```kotlin
   
interface Activity : OzoneEntity<Activity> {
    val id: Long
    val createdAt: Instant?
    val updatedAt: Instant?

    companion object : OzoneEntity.Of<Activity>()
}

object Activities : OzoneTable<Activity>("activities") {
    val id by bigIncrements()
    val createdAt by createdAt()
    val updatedAt by updatedAt()
}

```

</span>

The name of the table is automatically derived based on the entity's name. 

`make:entity` also takes a `-m` flag that creates a [migration](/docs/migrations) file for the entity.

<a name="inserting-new-entity"></a>
### [Inserting New Entity](#inserting-new-entity)

You can insert a new entity into the database in a number of ways. Let's go through them one-by-one:

<a name="strongly-typed-create"></a>
#### [Strongly typed `create()` method](#strongly-typed-create)

To crate an entity instance simply call `create()` method, which takes a closure as its only parameter where
you need to assign values to the columns. This method returns the new entity that was created.

```kotlin

val user = Users.create {
    it.fullName to "Jane Doe"
    it.email to "jane@example.com"
}

// If you don't need the new entity back, you can use `insertAndGenerateKey()` instead.
// It returns the id of the new entity. This method is more performant than the
// counterpart `create()` method as it only makes one trip to the database.

val userId = Users.insertAndGenerateKey {
    it.fullName to "Jane Doe"
    it.email to "jane@example.com"
}

```
<a name="creating-entity-attribte-map"></a>
#### [Creating an entity using an attribute map](#creating-entity-attribte-map)

If you'd rather create an entity with just a map of attributes, you can use `create()` method that takes a
map instead of a closure. The keys of this attribute map corresponds to the **column** names in the
table. If a corresponding column name doesn't exist for a key, it will simply be ignored.

```kotlin

val attributes = mapOf("full_name" to "Jane Doe", "email" to "jane@example.com")
val user = Users.create(attributes)

```

<a name="attribute-map-plus-assignment-builder"></a>
#### [Creating an entity using an attribute map + an assignment builder](#attribute-map-plus-assignment-builder)

When you are creating an entity by using an attribute map but then have to set some additional attributes then
you can use the overloaded `create()` method that takes an attribute map as the first parameter and
an assignment builder closure as the second parameter.

This is very helpful when you are creating a new entity with some user inputs
but you also have to, for an example, set some foreign key values.

```kotlin

fun savePost(call: HttpCall) {
    val attributes = call.params("title", "body", "slug")
    val authorId = call.user.id
    val post = Posts.create(attributes) {
        it.author_id to authorId
    }

    call.reply("success")
}

```

>/alert/<span>Be careful creating an entity using attribute map when the values come from an external source. You maybe
>exposed to [mass assignment attacks](https://owasp.org/www-project-cheat-sheets/cheatsheets/Mass_Assignment_Cheat_Sheet).</span>

<a name="adding-entity-to-table"></a>
#### [Adding an entity to a table](#adding-entity-to-table)

Even though `OzoneEntity` is an interface, thanks to the companion `OzoneEntity.Of<>()` object, it is possible to
create an instance of it as if it was a class. Once created, you can call `add()` method on its table to insert
this entity into the database.

```kotlin

val user = User {
    fullName = "Jane Doe"
    email = "jane@example.com"
}

Users.add(user)

```

<a name="batch-inserting-entities"></a>
#### [Batch Inserting Entities](#batch-inserting-entities)

If you have multiple instances to save in the database, you might want to consider using `batchInsert` rather
than inserting one entity at a time.

```kotlin

Users.batchInsert {
    item {
        it.name to "Jane"
        it.email to "jane@example.com"
    }

    item {
        it.name to "John"
        it.email to "john@example.com"
    }
}

```

<a name="finding-entity"></a>
### [Finding Entity](#finding-entity)

To find many entities that meet a condition, use the `findList()` method and pass a predicate closure.

```kotlin

val users = Users.findList {
    it.email like "%example.com%"
}

```

Use `findAll()` method to fetch all the records from a table. This auto left joins all the referencing tables as well.

Use `findOne()` method to find only one record if it exists or return null. If more than one record exists, then
this method will throw an exception.

If you already have a primary ID of an entity, you can use `findById()` method to fetch an entity. If you have
a list of IDs and you want to fetch the corresponding entities, you can use `findListByIds()` method instead.

<a name="finding-or-creating-entity"></a>
#### [Finding or creating an entity](#finding-or-creating-entity)

Sometimes you might be trying to find an entity with some given constraints and if it doesn't exist, you might want
to insert a new record instead. While you can do this by manually using one of the `find()` methods and then use
one of the `insert()` methods to create a new record. Or you can use one of the Ozone's `findOrCreate()` methods.

```kotlin

val slug = param("slug")
val whereAttributes = mapOf("slug" to slug)
val extraAttributes = params("title", "body")

// Try to find a post by given. If it doesn't exist, create a new post by combining
// both 'whereAttributes' and 'extraAttributes' maps.
val post = Posts.findOrCreate(whereAttributes, extraAttributes)

// You can also pass the 'extraAttributes' using an assignment builder
// by passing a closure to take advantage of strong typing
Posts.findOrCreate(whereAttributes) {
    it.title to "this is a new title"
    it.body to "this is new body"
}

```

<a name="updating-entity"></a>
### [Updating Entity](#updating-entity)

You can update an entity or multiple entities using a table, or you can update an entity directly
and flush the changes, or you can update an entity using an attribute map.

<a name="updating-using-table"></a>
#### [Updating using a table](#updating-using-table)

Update an entity or entities by calling `update()` on the table and passing a closure.

```kotlin

Users.update {
    it.name to "Jane M. Doe"
    it.email to "newjane@example.com"
    where {
        it.id eq 1
    }
}

```

<a name="updating-entity-directly"></a>
#### [Updating an entity directly](#updating-entity-directly)

If you already have an instance an entity, you can set its new values and then call `flushchanges()` to persist
the changes to the database.

Keep in mind that this entity must be associated with a table first and it also should have a primary key defined.

```kotlin

val user = Users.findById(1)

user.name = "Jane M. Doe"
user.email = "janemdoe@example.com"
user.flushChanges()

```

<a name="updating-entity-using-map"></a>
#### [Updating an entity using an entity map](#updating-entity-using-map)

If you have an attribute map of new values, you can update an entity or entities using
this map and passing a closure that conditionally selects the entities to be updated.

```kotlin

fun updatePost(call: HttpCall) {
    val attributes = call.params("title", "body")
    val postId = call.longParam("id").orAbort()

    Posts.update(attributes) {
        it.id eq postId
    }

    call.reply("success")
}

```

<a name="deleting-entity"></a>
#### [Deleting Entity](#deleting-entity)

To delete multiple entities you can use `delete()` method on the table and pass a closure.

```kotlin

Users.delete {
    it.email like "%example.com%"
}

```

To delete an entity that is already associated with a table and also has a primary key defined, you can call
`delete()` method on it directly.

```kotlin

val user = Users.findById(1)
user?.delete()

```

<a name="default-column-types"></a>
### [Default Column Types](#default-column-types)

Your table can define different types of out-of-the-box supported column types.

| Function Name     | Kotlin Type             | Underlying SQL Type |
| ----------------- | ----------------------- | ------------------- |
| `bigInt()`        | kotlin.Long             | bigInt              |
| `blob()`          | kotlin.ByteArray        | blob                |
| `boolean()`       | kotlin.Boolean          | boolean             |
| `bigIncrements()` | kotlin.Long             | bigInt              |
| `bytes()`         | kotlin.ByteArray        | bytes               |
| `char()`          | kotlin.String           | char                |
| `createdAt()`     | java.time.Instant       | timestamp           |
| `date()`          | java.time.LocalDate     | date                |
| `datetime()`      | java.time.LocalDateTime | datetime            |
| `decimal()`       | java.math.BigDecimal    | decimal             |
| `double()`        | kotlin.Double           | double              |
| `float()`         | kotlin.Float            | float               |
| `increments()`    | kotlin.Int              | int                 |
| `int()`           | kotlin.Int              | int                 |
| `jdbcDate()`      | java.sql.Date           | date                |
| `jdbcTime()`      | java.sql.Time           | time                |
| `jdbcTimestamp()` | java.sql.Timestamp      | timestamp           |
| `long()`          | kotlin.Long             | bigint              |
| `longText()`      | kotlin.String           | longtext            |
| `mediumText()`    | kotlin.String           | mediumtext          |
| `monthDay()`      | java.time.MonthDay      | varchar             |
| `smallInt()`      | kotlin.Int              | smallint            |
| `string()`        | kotlin.String           | varchar with size   |
| `text()`          | kotlin.String           | text                |
| `time()`          | java.time.Time          | time                |
| `timestamp()`     | java.time.Instant       | timestamp           |
| `tinyInt()`       | kotlin.Int              | tinyint             |
| `updatedAt()`     | java.time.Instant       | timestamp           |
| `varchar()`       | kotlin.String           | varchar             |
| `year()`          | java.time.Year          | int                 |
| `yearMonth()`     | java.time.YearMonth     | varchar             |

<a name="column-binding-conventions"></a>
### [Column Binding Conventions](#column-binding-conventions)

There are some columns that show up regularly in a database, especially with a web application — auto incrementing
primary key, a *created_at* timestamp field, and an *updated_at* timestamp field. Ozone has a shortcut
for declaring all of these fields within a table.

<div class="sublist">

* `bigIncrements() / increments()`

Declare an auto-incrementing unsigned primary key field of name *id* and bind it to its entity's `id` property.

* `createdAt() / updatedAt()`

Declare and bind a nullable timestamp field of name *created_at / updated_at* and bind to its entity's property
of the same name.

<span class="line-numbers" data-start="7" data-file="entities/Users.kt">

```kotlin

object Users: OzoneTable<User>() {

// Instead of declaring a primary this way...
val id by bigInt("id").autoIncrement().unsigned().primaryKey().bindTo{ it.id }
val id by int("id").autoIncrement().unsigned().primaryKey().bindTo{ it.id }

// ...you can declare it like this
val id by bigIncrements()
val id by increments()

// You can override the name of the column and the 
// consequently the name of the field it binds to
val id by bigIncrements("uid")

// Instead of declaring created_at and updated_at fields this way...
val createdAt by timestamp("created_at").nullable().useCurrent().bindTo { it.createdAt }
val updatedAt by timestamp("updated_at").nullable().useCurrent().bindTo { it.updatedAt }

// ...you can declare them like this:
val createdAt by createdAt()
val updatedAt by updatedAt()

// You can customize some of its properties as well
val createdAt by createdAt(name="created_date", nullable=false, useCurrent=false)

// ...

}

```

</span>

</div>

<a name="query-logging"></a>
### [Query Logging](#query-logging)

One major downside of using a database framework like Ozone/Ktorm is that the SQL queries that actually
get run on your database are opaque to you. You may be making a small innocent mistake like lazy loading
a relation instead of eagerly loading and running into the trap of [N+1 query problem][n+1].

One quick way to see behind-the-scenes database queries is by logging them, which, fortunately,
is already done for you. All you need to do is print out the actual queries that were ran by
adding the following 1 line to your [logging configuration files][log-config].

<span class="line-numbers" data-start="32" data-file="app_log_config.xml">

```xml

<!-- ... -->

<logger name="me.liuwj.ktorm.database" level="debug"></logger>

<!-- ... -->

```

</span>

For advanced DAO usage, please [checkout the DAO wiki](https://ktorm.liuwj.me/en/entities-and-column-binding.html).
For advanced DSL usage, please [checkout the DSL wiki](https://ktorm.liuwj.me/en/schema-definition.html).

>/power/<span>Ozone is proudly supercharged by [Ktorm*][ktorm].</span>

<span class="text-gray-600 text-base">

*[Ktorm][ktorm] is not just a super fun ORM library, it is also superbly documented, including commented code. We also
have a dedicated `#ktorm` [Slack channel][alpas-slack] where the author visits regularly to answer questions and
participate in Ktorm related discussions. We welcome you to ask questions on Slack if you need help.

</span>

[kotlin-object]: https://kotlinlang.org/docs/tutorials/kotlin-for-py/objects-and-companion-objects.html#object-declarations
[dsl-advanced]: https://ktorm.liuwj.me/en/query.html
[n+1]: https://stackoverflow.com/questions/97197/what-is-the-n1-selects-problem-in-orm-object-relational-mapping
[log-config]: /docs/logging#configuration
[alpas-slack]: https://join.slack.com/t/alpasdev/shared_invite/enQtODcwMjE1MzMxODQ3LTJjZWMzOWE5MzBlYzIzMWQ2MTcxN2M2YjU3MTQ5ZDE4NjBmYjY1YTljOGIwYmJmYWFlYjc4YTcwMDFmZDIzNDE
[ktorm-github]: https://github.com/vincentlauvlwj/Ktorm
[ktorm]: https://ktorm.liuwj.me/

