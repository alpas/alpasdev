- [Getting Started](#getting-started)
- [Defining a Factory](#defining-factory)
- [Using Factories](#using-factories)
    - [Creating and Persisting an Entity](#persisting-entity)
    - [Manually Saving an Entity](#manual-saving)
    - [Overriding Entity's Blueprint Properties](#overriding-properties)
- [Creating Entities Using `from` Method](#from-method)
### [Transforming Values Before Persisting](#transforming-values)

An entity factory allows you to define a blueprint for an Ozone entity, which can then be used to make one or
multiple copies of the entity. You can combine an entity factory with a [seeder](/docs/seeding) to easily populate
your database with some quick dummy data.

<a name="getting-started"></a>
### [Getting Started](#getting-started)

A factory is simply a class, or preferably, an object, extending the abstract `EntityFactory` class. You are
required to provide an instance of an [Ozone table](/docs/ozone#ozone-table) instance and must override `entity()`
method. This method is where you would return an [Ozone Entity](/docs/ozone/#ozone-entity) instance.

The quickest way to create an entity factory is by using the `make:factory` Alpas console command. This
command takes the name of the entity, or a list of entities for which the corresponding factory needs
to be created. All the entity factories are conventionally kept under `database/factories` directory. 

```bash

# Creates a factory for the Account entity
$ alpas make:factory Account

# Create multiple factories for multiple entitites
$ alpas make:factory Project Contact Post

```
<span class="line-numbers" data-start="8" data-file="database/factories/UserFactory.kt">

```kotlin

internal object UserFactory : EntityFactory<User, Users> {
    override val table = Users
    
    override fun entity(): User {
        return User {
            name = faker.name().name()
            updatedAt = faker.date().past(1, TimeUnit.HOURS).toInstant()
            createdAt = Instant.now()
        }
    }
}

```

</span>

<a name="defining-factory"></a>
### [Defining a Factory](#defining-factory)

Usually, while defining the blueprint of an entity, you'd use some fake data. To make it easy to create some fake,
dummy data, Alpas comes bundled with the [Java Faker](https://github.com/DiUS/java-faker) library and a global
`faker` object is available for you to use inside your factories. You can create your faker instance if you need.

As mentioned before, the only method you are required to override in your concrete factory is the `entity()` method.
From this method, just return an instance of the actual entity **without actually persisting in the database** as
you may not always want to persist it in the database.

<span class="line-numbers" data-start="11" data-file="database/factories/UserFactory.kt">

```kotlin

override fun entity(): User {
    // Don't persist; just return an instance!
    // Since User is an interface, notice how we are creating the instance
    return User {
        name = faker.name().name()
        email = faker.internet().emailAddress()
        updatedAt = faker.date().past(1, TimeUnit.HOURS).toInstant()
        createdAt = Instant.now()
    }
}

```

</span>

Feel free to fill all the properties of your entity in this method. You can override any
properties later when you are actually calling this factory.

<a name="using-factories"></a>
### [Using Factories](#using-factories)

Since a factory is just an object instance (or a class), you can use it directly.
There are few things you can do with a factory.

<a name="persisting-entity"></a>
#### [Creating and Persisting an Entity](#persisting-entity)

The `create()` method is responsible for actually creating an instance of an entity from the blueprint
and persist in the database. It also returns a fresh copy of the entity from the database to
make sure all the relationship bindings are available on it, if any.

```kotlin

// This will create and save an entity record in the database
val user = UserFactory.create()

```

<a name="manual-saving"></a>
#### [Manually Saving an Entity](#manual-saving)

If you just built an instance of an entity without persisting it, you can call the `saveAndRefresh()`
method to save it to the database and return a fresh copy of it.

```kotlin

// This will only create a User instance without persisting in the database
val user = UserFactory.entity()

// This saves and then fetches a fresh copy
val freshUser = UserFactory.saveAndRefresh(user)

```

<a name="overriding-properties"></a>
#### [Overriding Entity's Blueprint Properties](#overriding-properties)

Sometimes, for more controlled testing, you may want to use some specific data for some specific attributes,
say for an email, instead of using some fake data. 

The real power of a factory is in being able to override an entity's properties defined in the blueprint on the
fly. Even if you have defined the blueprint completely with fake and random data, you can always override
the values by passing a map of attributes to override when calling the factory.

```kotlin

// Create a User instance by overriding email and name fields that are defined in the blueprint
val user = UserFactory.create(mapOf("email" to "jane@example.com", "name" to "Jane Doe"))

// Build a new user by overriding some properties
val anotherUser = UserFactory.build(mapOf("name" to "Jane Doe"))
// This saves and then fetches a fresh copy
val refreshedUser = UserFactory.saveAndRefresh(anotherUser)

```

<a name="from-method"></a>
### [Creating Entities Using `from` Method](#from-method)

You can use a slightly convenient `from()` method to create one or multiple instances of an entity. This
will persist the instances in the database. So, in a way, it is just a proxy for the `create()` method.

```kotlin

// Create a user instance, override the properties with values from
// the given map, persist the instance, and fetch a fresh copy of it.
val user = from(UserFactory, mapOf("name" to "Jane Doe"))

// Create 5 instances of the User entity.
val users = from(UserFactory, 5, mapOf("country" to "USA"))

```

While using `from`, you can also override the properties using strongly typed assignment builder:

```kotlin

// Create a user instance, override the properties with values from
// the given map, persist the instance, and fetch a fresh copy of it.
val user = from(UserFactory) {
    it.name to "Jane M. Doe"
    it.email to "differentjane@example.com"
}

```


<a name="transforming-values"></a>
### [Transforming Values Before Persisting](#transforming-values)

You can transform the value of an entity's property before saving it to database by overriding `transform()` method
and returning a new value. This is useful, for an example, to hash a password before it gets saved in the database.

There are other ways you can achieve this but doing it inside the `trasnsform()`
method keeps all those transformations in one place.

Let's see an example of a `UserFactory` that hashes a password before saving.

<span class="line-numbers" data-start="11" data-file="database/factories/UserFactory.kt">

```kotlin

internal class UserFactory(private val hasher: Hasher) : EntityFactory<User, Users>() {
    override val table = Users

    override fun entity(): User {
        return User {
            firstName = faker.name().firstName()
            lastName = faker.name().lastName()
            email = faker.internet().safeEmailAddress()
            password = "secret"
        }
    }

    override fun transform(name: String, value: Any?): Any? {
        return if (name == "password") {
            hasher.hash(value.toString())
        } else {
            value
        }
    }
}

```

</span>

The "secret" password now will be hashed before persisting. Moreover, you can override the password
while applying this factory, and the transformation will still be applied.

```kotlin

val hasher = app.make<Hasher>()

// The overriden password "mysupersecretpassword" will be transformed as well
val user = from(UserFactory(hasher), mapOf("password" to "mysupersecretpassword"))

```
