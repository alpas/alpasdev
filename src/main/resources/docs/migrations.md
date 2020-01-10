- [Creating Migrations](#creating-migrations)
- [Migration Structure](#migration-structure) 
- [Migrating](#migrating)
- [Rolling Migrations Back](#rolling-migrations-back)
- [Refreshing Database](#refreshing-database)
- [Tables](#tables)
    - [Creating Tables](#creating-tables)
    - [Dropping Tables](#dropping-tables)
    - [Modifying Tables](#modifying-tables)
    
Migrations are like version control for your database. There are many advantages of using migrations—
being able to easily create a database with ease, being able to share the same database schema with
a team, being able to recreate a production database that is exactly like the development one as
far as the schema is concerned, being able to quickly iterate on a database design etc.

With Alpas's built-in migration support, you can take advantages of all the above benefits of
migrations without breaking a sweat.

<a name="creating-migrations"></a>
### [Creating Migrations](#creating-migrations)

You can create a migration by using `make:migration` Alpas command. It takes the name of the
migration, and the action you want to perform—either `create` or `modify`, which takes the
name of the [table](/docs/ozone#ozone-table) to crate or modify.

```bash

# Create a migration for creating a table
$ alpas make:migration create_receipts_table --create=receipts

# Create a migration for modifying a table
$ alpas make:migration modify_receipts_table --modify=receipts

```

<a name="migration-structure"></a>
### [Migration Structure](#migration-structure)

Migrations work by tracking your migration files under `database/migrations` folders in a database
table called `migrations`. Migrations files are named as the time when these files are created.
These helps to keep track of them easily as well as to sort them chronologically.

When a migration is run, it checks if any of migration files are already migrated or not. All the
outstanding migrations are then run in one batch, which is numbered. When performing the
rollback, all the migrations from the latest batch are picked and performed rollback.

A migration file contains two methods—`up()` and `down()`. The `up()` method is, conventionally, used
to add new tables or modifying an existing table, while the `down()` method, again conventionally,
used to "undo" the operations of the `up()` method. During the actual migration, the `up()`
method is call and during the rollback, the `down()` method is invoked.

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

To rollback the latest batch of migrations, which may include multiple migrations, you can run `db:rollback`.

```bash

$ alpas db:rollback

```

<a name="refreshing-database"></a>
### [Refreshing Database](#refreshing-database)

During development, as you are iterating on your database schema and playing with some test data, you might want
to undo all your database changes and start the database state from the scratch, basically re-creating your
database. You can achieve this by rolling back few times and then migrating or use the handy `db:refresh`
command, which will drop all your tables and then execute the `migrate` command for you.

```bash

$ alpas db:refresh

```

<a name="tables"></a>
### [Tables](#tables)

<a name="creating-tables"></a>
#### [Creating Tables](#creating-tables)

You can create a new table by calling the `createTable()` method and passing
the [object instance of Ozone Table](/docs/ozone#ozone-table).

You are not bound to creating only one table. You can pass multiple tables to `createTable()`
method. You can also set `inBatch` to `true` to create all the tables in one go.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_create_orders_tables.kt">

```kotlin

class CreateOrdersTables : Migration() {
    override fun up() {
        createTable(Receipts, Orders, Products, inBatch = true)
    }
}

```

</span>

<a name="dropping-tables"></a>
#### [Dropping Tables](#dropping-tables)

To drop an Ozone table, call `dropTable()` method, and the table object. You can drop multiple tables
in one call and in one pass by setting `inBatch` to `true`.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_create_orders_tables.kt">

```kotlin

class CreateOrdersTables : Migration() {
    override fun down() {
        dropTable(Products, Orders, Receipts, inBatch = true)
    }
}

```

</span>

<a name="modifying-tables"></a>
#### [Modifying Tables](#modifying-tables)

When you add new columns to a table, you can call `modifyTable()` method by passing the name of a table or multiple
tables to auto-actualize the database schema. This will create any missing tables and adds missing columns
as long as the new columns are nullable or have default values. While this may not be enough for all
your table modification use cases, it is still helpful with new tables and missing columns.

<span class="line-numbers" data-start="6" data-file="database/migrations/2020_01_01_123456_modify_users_table.kt">

```kotlin

class CreateOrdersTables : Migration() {
    override fun up() {
        modifyTable(Users)
    }
}

```

</span>

>/info/ Alpas current doesn't support deleting or modifying existing columns from a database.
