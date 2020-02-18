- [Preparing Table for Migration](#preparing-migrations)
- [Creating Migrations](#creating-migrations)
- [Migration Structure](#migration-structure) 
- [Migrating](#migrating)
- [Rolling Migrations Back](#rolling-migrations-back)
- [Refreshing Database](#refreshing-database)
- [Tables](#tables)
    - [Creating Tables](#creating-tables)
    - [Customizing Tables](#customizing-tables)
    - [Dropping Tables](#dropping-tables)
    - [Modifying Table](#modifying-table)
    - [Executing Queries](#executing-queries)
- [Customizing Columns](#customizing-columns)
    - [Column Binding Conventions](#column-binding-conventions)

Migrations are like version control for your database. There are many advantages of using migrations —
being able to easily create a database with ease, being able to share the same database schema with
a team, being able to recreate a production database that is exactly like the development one as
far as the schema is concerned, being able to quickly iterate on a database design, etc.

With Alpas's built-in migration support, you can take advantages of all the above
benefits of migrations without breaking a sweat.

<a name="preparing-migrations"></a>
### [Preparing Table for Migration](#preparing-migrations)

Your [Ozone table](/docs/ozone#ozone-table) must extend from `OzoneTable` instead of just `Table` to support
migration. Once it extends from `OzoneTable`, you can further [customize](#customizing-columns) the
table's columns such as marking it nullable, setting the size of a varchar column, etc.

<a name="creating-migrations"></a>
### [Creating Migrations](#creating-migrations)

You can create a migration by using the `make:migration` Alpas command. It takes the name of the migration,
and the [Ozone table instance](/docs/ozone#ozone-table) to create.

```bash

# Create a migration for creating a table
$ alpas make:migration create_receipts_table --create=receipts

```

<a name="migration-structure"></a>
### [Migration Structure](#migration-structure)

Migrations work by tracking your migration files under `database/migrations` folders in a database
table called `migrations`. Migrations files are named after the time when these files are created.
This helps keep track of them easily as well as to sort them chronologically.

When a migration is ran, it checks if any of the migration files are already migrated or not. All
the outstanding migrations are then ran in one batch, which is numbered. When performing the
rollback, all the migrations from the latest batch are picked and performed rollback.

A migration file contains two methods — `up()` and `down()`. The `up()` method is conventionally used
to add new tables or to modify an existing table, while the `down()` method, again conventionally,
is used to "undo" the operations of the `up()` method. During the actual migration, Alpas calls the
`up()` method and during the rollback it invokes the `down()` method.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_create_receipts_table.kt">

```kotlin

class CreateReceiptsTable : Migration() {
    override fun up() {
        createTable(Receipts)
    }

    override fun down() {
        dropTable(Receipts)
    }
}

```

</span>

<a name="migrating"></a>
### [Migrating](#migrating)

Running migration is as easy as running the `db:migrate` Alpas command. This will
migrate all of your outstanding migrations in one batch.

```bash

$ alpas db:migrate

```
<a name="rolling-migrations-back"></a>
### [Rolling Migrations Back](#rolling-migrations-back)

To rollback the latest migration batch, which may include multiple migrations, you can run `db:rollback`.

```bash

$ alpas db:rollback

```

<a name="refreshing-database"></a>
### [Refreshing Database](#refreshing-database)

During development, as you are iterating on your database schema and playing with some test data, you might want
to undo all your database changes and start the database state from the scratch, basically re-creating your
database. You can achieve this by rolling back few times and running migration or use the handy`db:refresh`
command, which will drop all your tables and then execute the `migrate` command for you.

```bash

$ alpas db:refresh

```

You can also pass a `--seed` flag to automatically [run the default seeder](/docs/seeding#running-seeder)
after the refreshing is completed.

<a name="tables"></a>
### [Tables](#tables)

<a name="creating-tables"></a>
#### [Creating Tables](#creating-tables)

You can create a new table by calling the `createTable()` method and passing
the object instance of an [Ozone Table](/docs/ozone#ozone-table).

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_create_orders_tables.kt">

```kotlin

class CreateOrdersTables : Migration() {
    override fun up() {
        createTable(Receipts)
        createTable(Orders)
        createTable(Products)
    }
}

```

</span>

>/info/<span>Creating a table from a migration is only supported for SQLite, MySQL, and PostgresSQL at
>this time. For other vendors, you can [run a raw query](#executing-queries) to perform the migration.</span>

<a name="customizing-tables"></a>
#### [Customizing Tables](#customizing-tables)

`createTable()` takes a lambda to let you further customize your table. This is usually helpful to,
for an example, add some indices to your table.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_create_receipts_table.kt">

```kotlin

class CreateReceiptsTable : Migration() {
    override fun up() {
        createTable(Receipts) {
            // add an index to email column
            addIndex("email")
        }
    }
}

```

</span>

<a name="dropping-tables"></a>
#### [Dropping Tables](#dropping-tables)

To drop an Ozone table, call `dropTable()` method, passing object instance of an
[Ozone Table](/docs/ozone#ozone-table) that you want to drop.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_create_orders_tables.kt">

```kotlin

class CreateOrdersTables : Migration() {
    override fun down() {
        dropTable(Products)
        dropTable(Orders)
        dropTable(Receipts)
    }
}

```

</span>

<a name="modifying-table"></a>
#### [Modifying Table](#modifying-table)

Ozone supports some basic table modification operations with `addColumn()` and `dropColumn()` methods.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_modify_users_table.kt">

```kotlin

class AddAvatarColumnsToUsersTable : Migration() {
    override fun up() {
        modifyTable(Users) {
            addColumn(Users.avatarUrl).after(Users.name)
            addColumn(Users.avatarProvider).after(Users.avatarUrl)
        }
    }

    override fun down() {
        modifyTable(Apps) {
            dropColumn("avatar_url", "avatar_provider")
        }
    }
}

```

</span>

You can create a modifying migration by using the `make:migration --modify=<table>` Alpas command.

#### [Executing Queries](#executing-queries)

For advanced use cases and for features that are unavailable for certain database vendors, such as `createTable()`, you
can migrate your database by passing a raw query to `execute()` method. Even if you have run a raw query, you'd still
get the benefits of migrating your database up and down as this operation is tracked in the migrations table as well.

For an example, given that you have executed the proper query in the `down()` method,
it will be applied when calling the `alpas db:rollback` command.


<span class="line-numbers" data-start="4" data-file="database/migrations/2020_01_01_123456_create_users_table.kt">

```kotlin

class CreateUsersTable : Migration() {
    override fun up() {
        val createQuery = """
            CREATE TABLE IF NOT EXISTS users (
              username varchar(45) NOT NULL,
              password varchar(450) NOT NULL,
              enabled integer NOT NULL DEFAULT '1',
              PRIMARY KEY (id)
            )
        """.trimIndent()

        execute(createQuery)
    }

    override fun down() {
        val dropQuery = "DROP TABLE users"
        execute(dropQuery)
    }
}

```

</span>


<a name="customizing-columns"></a>
### [Customizing Columns](#customizing-columns)

You can further customize a column by chaining a number of convenience methods on a column. These
add extra attributes to a column metadata, which are used during migration.


| Function Name         | Available On               | Comments                                                  |
| --------------------- | -------------------------- | --------------------------------------------------------- |
| `autoIncrement()`     | Any column of type number. | Set the column type as as auto-incrementing.              |
| `reference()`         | Reference a foreign table. | Adds a reference constraint to a foreign table.           |
| `default(Any)`        | Any column type.           | Set the default value of a column.                        |
| `index()`             | Any column type.           | Create an index on the column.                            |
| `nullable()`          | Any column type.           | Set the column type is nullable.                          |
| `precision(Int, Int)` | Any column of type number. | Set the total precision and the number of decimal places. |
| `size(Int)`           | Any column of type string. | Set the size of a string column.                          |
| `unique()`            | Any column type.           | Add a UNIQUE constraint on the column.                    |
| `unsigned()`          | Any column of type number. | Set the column type as unsigned.                          |
| `useCurrent()`        | Any `Temporal` column.     | Use the current timestamp as a default value.             |

<br/>

Here is an example of columns customization of a fictional `Users` table. 

<span class="line-numbers" data-start="21">

```kotlin

object Users : OzoneTable<User>("users") {
    val id by bigIncrements("id")
    val pin by smallInt("pin").unsigned()
    val email by varchar("email").index().unique()
    val password by varchar("password").size(100)
    val name by varchar("name").index().nullable()
    val emailIsVerified by boolean("email_verified").default(false)
    val teamId by long("team_id").unsigned().reference { onDeleteCascade() }
    val createdAt by timestamp("created_at").useCurrent()
}

```

</span>


<a name="column-binding-conventions"></a>
#### [Column Binding Conventions](#column-binding-conventions)

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

>/info/<span>Alpas currently doesn't support modifying existing columns of a table.</span>
