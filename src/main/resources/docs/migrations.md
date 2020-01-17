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
- [Migrating-Table Columns](#migrating-table-columns)
    - [Extra Columns](#extra-columns)
    - [Customizing Columns](#customizing-columns)

Migrations are like version control for your database. There are many advantages of using migrations—
being able to easily create a database with ease, being able to share the same database schema with
a team, being able to recreate a production database that is exactly like the development one as
far as the schema is concerned, being able to quickly iterate on a database design etc.

With Alpas's built-in migration support, you can take advantages of all the above
benefits of migrations without breaking a sweat.

<a name="preparing-migrations"></a>
### [Preparing Table for Migration](#preparing-migrations)

Your [Ozone table](/docs/ozone#ozone-table) must extend from `MigratingTable` instead of just `Table` to support
migration. Once it extends from `MigratingTable`, you can further [customize](#customizing-columns) the
table's columns such as marking it nullable, setting the size of a varchar column etc.

<a name="creating-migrations"></a>
### [Creating Migrations](#creating-migrations)

You can create a migration by using `make:migration` Alpas command. It takes the name of the migration,
and the [Ozone table instance](/docs/ozone#ozone-table) to create.

```bash

# Create a migration for creating a table
$ alpas make:migration create_receipts_table --create=receipts

```

<a name="migration-structure"></a>
### [Migration Structure](#migration-structure)

Migrations work by tracking your migration files under `database/migrations` folders in a database
table called `migrations`. Migrations files are named after the time when these files are created.
This helps to keep track of them easily as well as to sort them chronologically.

When a migration is run, it checks if any of the migration files are already migrated or not. All
the outstanding migrations are then run in one batch, which is numbered. When performing the
rollback, all the migrations from the latest batch are picked and performed rollback.

A migration file contains two methods—`up()` and `down()`. The `up()` method is conventionally used
to add new tables or to modify an existing table, while the `down()` method, again conventionally,
used to "undo" the operations of the `up()` method. During the actual migration, Alpas calls the
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

Running migration is as easy as running `db:migrate` Alpas command. This will
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

<a name="migrating-table-columns"></a>
### [Migrating-Table Columns](#migrating-table-columns)

A subclass of `MigratingTable` adds more columns for your convenience and allows further
customization of your table's columns as and when you are declaring them.

<a name="extra-columns"></a>
#### [Extra Columns](#extra-columns)

Here are some more column types it adds on top of the [default ones](/docs/ozone#default-column-types).

| Function Name     | Ozone Type               | Comments                                               |
| ----------------- | ------------------------ | ------------------------------------------------------ |
| `increments()`    | `int()`                  | An auto-incrementing unsigned integer primary key.     |
| `bigIncrements()` | `long()`                 | An auto-incrementing unsigned long primary key.        |
| `string()`        | `text()`                 | A varchar column that accepts a size (255 by default). |

<a name="customizing-columns"></a>
#### [Customizing Columns](#customizing-columns)

You can further customize a column by chaining a number of convenience methods on a column.

| Function Name         | Available On               | Comments                                                  |
| --------------------- | -------------------------- | --------------------------------------------------------- |
| `size(Int)`           | Any column of type string. | Set the size of a string column.                          |
| `default(Any)`        | Any column type.           | Set the default value of a column.                        |
| `useCurrent()`        | Any `Temporal` column.     | Use the current timestamp as a default value.             |
| `unsigned()`          | Any column of type number. | Set the column type as unsigned.                          |
| `autoIncrement()`     | Any column of type number. | Set the column type as as auto-incrementing.              |
| `nullable()`          | Any column type.           | Set the column type is nullable.                          |
| `precision(Int, Int)` | Any column of type number. | Set the total precision and the number of decimal places. |
| `unique()`            | Any column type.           | Add a UNIQUE constraint on the column.                    |
| `index()`             | Any column type.           | Create an index on the column.                            |

<br/>

Here is an example of columns customization of a fictional `Users` table. 

<span class="line-numbers" data-start="21">

```kotlin

object Users : MigratingTable<User>("users") {
    val id by bigIncrements("id")
    val pin by smallInt("pin").unsigned()
    val email by varchar("email").index().unique()
    val password by varchar("password").size(100)
    val name by varchar("name").index().nullable()
    val emailIsVerified by boolean("email_verified").default(false)
    val createdAt by timestamp("created_at").useCurrent()
}

```

</span>

>/info/<span>Alpas currently doesn't support adding, deleting, or modifying existing columns of a table.</span>
