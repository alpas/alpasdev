- [One To One](#one-to-one)
    - [belongsTo](#belongs-to)
    - [hasOne](#has-one)
    - [hasOne Relationship Conventions](#hasone-conventions)
    - [Overriding hasOne Expression](#overriding-hasone)
- [One To Many](#one-to-many)
    - [hasMany](#has-many)
    - [hasMany Relationship Conventions](#hasmany-conventions)
    - [Overriding hasMany Expression](#overriding-hasmany)
- [Customizing Relationship Constraints](#customizing-constraints)

Tables in a database are often related to each other. With raw SQL queries you can retrieve these relationships
with JOINs. You want the same kind of flexibility while using entities but without having to resort to 
writing raw queries. Since entities in Ozone are strongly typed, it only makes sense to have the
relationships be strongly typed as well. Ozone and Ktorm makes it easy to retrieve an entity
along with its entity relatives without making you worry about left and right joins.

<a name="one-to-one"></a>
### [One To One](#one-to-one)

The simplest of all the relations is one-to-one. For example, a `User` could have one `Profile` and one profile
belongs to only one `User`. So a one-to-one relationship is best described by **hasOne** and **belongsTo** names.
In our example, `Users` *hasOne* `Profile` and inversely, `Profile` **belongsTo** `User`.

While designing a database schema, only one of the tables would have a column that points to an id of the other
"foreign" table. This key is called a **foreign key** and generally, this column is named
`foreignTable_id` such as "user_id".

The table that has the **foreign key** defined in its table gets to be on the `belongsTo` side of the relationship.
Think of it as a pet dog with a collar tag that says "I belong to this awesome owner with this phone number".
This is true for not just one-to-one but also one-to-many relationships.

On the other side of the relationship, the table that ***does not*** have a foreign key pointing to the other table
gets to be on the `hasOne` side of the relationship.

Understanding the differences between `belongsTo` and `hasOne` is very important as it is a common source of
confusion, especially among beginners.

Let's now see an example of two entities with a one-to-one relationship.

<span class="line-numbers" data-start="3" data-file="entities/User.kt">

```kotlin

interface User : Entity<User> {
    val id: Long
    var name: String

    companion object : Entity.Factory<User>()
}

object Users : MigratingTable<User>("users") {
    val id by bigIncrements("id").bindTo { it.id }
    val name by string("name").bindTo { it.name }
}

```
</span>


<span class="line-numbers" data-start="3"  data-file="entities/Profile.kt">

```kotlin

interface Profile : Entity<Profile> {
    val id: Long
    var handle: String
    var userId: Long
    var isActive: Boolean

    companion object : Entity.Factory<Profile>()
}

object Profiles : MigratingTable<Profile>("profiles") {
    val id by bigIncrements("id").bindTo { it.id }
    val handle by string("handle").bindTo { it.handle }
    val isActive by boolean("s_active").bindTo { it.isActive }
    val userId by long("user_id").bindTo { it.userId }
}

```

</span>

<a name="belongs-to"></a>
#### [belongsTo](#belongs-to)

`belongsTo` relationship can be mentioned by replacing `bindTo` with `belongsTo()` method in the table itself. In
our example, one profile belongs to one user:

<span class="line-numbers" data-start="3"  data-file="entities/Profile.kt">

```kotlin

interface Profile : Entity<Profile> {
    val id: Long
    var handle: String
    var isActive: Boolean

    // We don't want just the id but the entity itself

    /** v̶a̶r̶ ̶u̶s̶e̶r̶I̶d̶:̶ ̶L̶o̶n̶g̶ **/
    var user: User

    companion object : Entity.Factory<Profile>()
}

object Profiles : MigratingTable<Profile>("profiles") {
    val id by bigIncrements("id").bindTo { it.id }
    val handle by string("handle").bindTo { it.handle }
    val isActive by boolean("s_active").bindTo { it.isActive }

    // Add belongsTo() at the end passing the name of the table and the
    // actual property of this entity you want to bind it to

    /** v̶a̶l̶ ̶u̶s̶e̶r̶I̶d̶ ̶b̶y̶ ̶l̶o̶n̶g̶(̶"̶u̶s̶e̶r̶_̶i̶d̶"̶)̶.̶b̶i̶n̶d̶T̶o̶ ̶{̶ ̶i̶t̶.̶u̶s̶e̶r̶I̶d̶ ̶}̶ **/
    val userId by long("user_id").belongsTo(Users) { it.user }
}

```

</span>

Now, whenever a profile instance is fetched from the database, a left join will be used to fetch its user as well.

```kotlin

val profile = Profiles.findById(5)
val user = profile?.user
assertNotNull(user)

```

<a name="has-one"></a>
#### [hasOne](#has-one)

On the other side of `belongsTo` is `hasOne`. This table doesn't have a column referring to the other table. To
bind to a "virtual" entity, it needs to define a property with a custom getter and use `hasOne()` method.

<span class="line-numbers" data-start="3"  data-file="entities/User.kt">

```kotlin

interface User : Entity<User> {
    val id: Long
    var name: String

    val profile: Profile?
        get() = hasOne(Profiles)

    companion object : Entity.Factory<User>()
}

```

</span>


You don't have to do anything in the table object but only in the entity interface. This makes sense if you think
about it — there is nothing in the actual database table to describe this relationship so we have to define a property 
"on the fly".

>/info/<span>`hasOne` caches the related entity so even if you call this property multiple
>times, only one database trip is made.</span>

#### [hasOne Relationship Conventions](#hasone-conventions)

The secret sauce of Ozone's `hasOne()` relationship bindings is some educated guesses on how you have probably
designed the schema. You can override this convention as well if you have defined your table differently.

<div class="sublist">

- `hasOne(table: T, foreignKey: String?, localKey: String?, cacheKey: String?)`

    - **table**: The table object to which the relative entity belongs to. This is required.
   
    - **foreignKey**: The foreign key of the relative table. This is by default derived as: **entityClassName + "_id"**
    where *entityClassName* is the lowercased name of this entity class. So, if a *User* hasOne *Profile*, this
    foreign key would be `user_id` in the *Profiles* table.
   
    - **localKey**: The name of the column that the *foreignKey* points to. By default, it is `id`. This is the "local"
    key of this entity not the entity that has defined the `foreignKey` i.e. not Profile entity's id but User entity's
    id.
    
    - **cacheKey**: The key to use for caching it locally so that you can refer to it without making another database
    trip. Consider changing this value only if the key clashes with something else.
    
</div>

<a name="overriding-hasone"></a>
#### [Overriding hasOne Expression](#overriding-hasone)

If you want complete control over retrieving the related entity, you can use the alternative `hasOneBy()` method and
pass a predicate.

<span class="line-numbers" data-start="3"  data-file="entities/User.kt">

```kotlin

interface User : Entity<User> {
    val id: Long
    var name: String
    val profile: Profile?
        get() = hasOneBy(Profiles) {
            it.userId eq id
        }

    companion object : Entity.Factory<User>()
}

```

</span>

<a name="one-to-many"></a>
### [One To Many](#one-to-many)

One-to-many is very similar to one-to-one relationship. Similar to one-to-one relationship, an entity would belong
to another entity if it has a key in its table that points to the primary key of another table. Nothing is different
here — the key is still called a `foreign_key` and since it has the key, this entity would declare it to be belonging
to another entity, just like one-to-one.

Let's say we want to model a department where it can have many employees and each employee belongs to only one department.

The `belongsTo` side of relationship should look familiar:

<span class="line-numbers" data-start="3"  data-file="entities/Employee.kt">

```kotlin

interface Employee : Entity<Employee> {
    val id: Long
    var fullName: String
    var role: String
    var department: Department

    companion object : Entity.Factory<Employee>()
}

object Employees : MigratingTable<Employee>("employees") {
    val id by bigIncrements("id").bindTo { it.id }
    val fullName by string("full_name").bindTo { it.fullName }
    val role by string("role").bindTo { it.role }
    val departmentId by long("department_id").belongsTo(Departments) { it.department }
}

```

</span>

<a name="has-many"></a>
#### [hasMany](#has-many)

On the other side of the relationship, since multiple entities can now belong to it, it would have to create
a "virtual" property that would get a list of entities owned by it. Since it doesn't have anything in its
table referring to the other table, nothing is there to be done in the entity's table. To convey this
relationship, you can use `hasMany()` method passing the name of the related entity.

<span class="line-numbers" data-start="3"  data-file="entities/Department.kt">

```kotlin

interface Department : Entity<Department> {
    val id: Long
    var name: String
    val employees
        get() = hasMany(Employees)

    companion object : Entity.Factory<Department>()
}

object Departments : MigratingTable<Department>("departments") {
    val id by bigIncrements("id").bindTo { it.id }
    val name by string("name").bindTo { it.name }
}

```

</span>

<a name="hasmany-conventions"></a>
#### [hasMany Relationship Conventions](#hasmany-conventions)

`hasMany` works by making some educated guesses about the schema of your table. You can override these values if you
want.

<div class="sublist">

- `hasMany(table: T, foreignKey: String?, localKey: String?, cacheKey: String?)`

    - **table**: The table object to which the relative entity belongs to. This is required.
   
    - **foreignKey**: The foreign key of the relative table. This is by default derived as: **entityClassName + "_id"**
    where *entityClassName* is the lowercased name of this entity class. So, if a *Department* hasMany *Employees*, this
    foreign key would be `department_id` in the *Employees* table.
   
    - **localKey**: The name of the column that the *foreignKey* points to. By default, it is `id`. This is the "local"
    key of this entity not the entity that has defined the `foreignKey` i.e. not *Department* entity's id but *Employee* entity's
    id.
    
    - **cacheKey**: The key to use for caching it locally so that you can refer to it without making another database
    trip. Consider changing this value only if the key clashes with something else.
    
</div>

<a name="overriding-hasmany"></a>
#### [Overriding hasMany Expression](#overriding-hasmany)

If you want to define how to retrieve the related entities, you can use the alternative `hasManyBy()` method and
pass a predicate.

<span class="line-numbers" data-start="3"  data-file="entities/Department.kt">

```kotlin

interface Department : Entity<Department> {
    val id: Long
    var name: String
    val employees
        get() = hasManyBy(Employees) {
            it.departmentId eq id
        }

    companion object : Entity.Factory<Department>()
}

```

</span>

<a name="customizing-constraints"></a>
### [Customizing Relationship Constraints](#customizing-constraints)

When fetching a related entity, you can further customize the query expression by passing a lambda as the last
parameter to one of `hasOne()` and `hasMany()` methods.

Let's say you only want to retrieve the profile if it is active.

<span class="line-numbers" data-start="3"  data-file="entities/User.kt">

```kotlin

interface User : Entity<User> {
    val id: Long
    var name: String

    val profile: Profile?
        get() = hasOne(Profiles) { it.isActive eq true }

    companion object : Entity.Factory<User>()
}

```

</span>

Similarly, you can further constraint the `hasMany()` query by passing a lambda predicate.

Let's say you only want to retrieve employees if they are also managers.

<span class="line-numbers" data-start="3"  data-file="entities/Department.kt">

```kotlin

interface Department : Entity<Department> {
    val id: Long
    var name: String
    val employees
        get() = hasMany(Employees) {
            it.role eq "manager"
        }

    companion object : Entity.Factory<Department>()
}

```

</span>
