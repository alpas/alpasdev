### Introduction

When handling a request and especially when handling user input you'd want to make sure that the incoming data is
valid and as expected. If it is not, you'd want to send appropriate error messages back to the user. You'd then want
to have the error messages available in your templates to format and display them nicely. All these need a lot of
wiring. Fortunately, Alpas supports all these out of the box!

### Validation Rules

The simplest and quickest way to validate an `HttpCall` is by applying some validation rules on it for each of the 
input params you want to validate.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.applyRules("username") {
        required()
        min(8)
        max(32) { attr, value ->
            "$attr cannot be more than 32 characters long. It is ${value?.toString()?.length} character long."
        }
    }

    call.applyRules("email") {
        required() { _, _ -> "Email field is required to receive payment reminders." }
        email()
    }

    // don't forget to call validate() once all the rules are applied
    call.validate()
    
    // program reaches here only if all of your validations pass
    call.reply("Everything is good!")
}

```

</span>

Instead of applying multiple rules, you could also pass a map of rules where the key is the name of the field and 
the value is a list of rule objects for the field.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    val rules = mapOf("username" to listOf(Required(), Max(32), Min(8)))
    call.applyRules(rules).validate()

    call.reply("Everything is good!")
}

```

</span>

#### Failing Fast

Sometimes you may wish to not continue with the validation after the first validation fails. To achieve this, set
`failfast` to `true` when applying your rules.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.applyRules("username", failfast = true) {
        required()
        min(8) // this validation won't run if required() validation fails
        max(32) // this validation won't run if either required() or min(8) validations fail
    }.validate()

    call.reply("Everything looks good!")
}

```

</span>

If, for whatever reason, you want to run all the validation rules for an attribute and then only continue validating
the next attribute, you could just call `validate()` after each set of rules.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.applyRules("username") {
        required()
        min(8)
        max(32)
    }.validate() // this validates your call right away and only proceeds if all the validations pass

    // username should be valid at this point
    call.applyRules("email") {
        required()
        email()
    }.validate()

    call.reply("Everything looks good!")
}

```

</span>

### Validation Guard

The in-place validation within your controller may be good enough for simpler validation rules, it is desirable to 
better organize your validation rules to make them more manageable. You may want to do more within the rules, such as 
caching an expensive database lookup to retrieve later, or share the rules easily with other controllers. You 
definitely don't want all these complex logic scattered all over the places in your controller. To facilitate this 
Alpas allows you to validate your call using a dedicated `ValidationGuard` class.

To create a `ValidationGuard` class, you could use the `make:guard` Alpas command:

```bash

# creates CreatePageGuard.kt class under src/main/kotlin/guards folder
alpas make:guard CreatePageGuard

```

If you open the newly generated guard class you'll see that a `rules()` method is already overriden for you. All you
need to do now is to return a map of rules where the key is the name of the field and the value is a list of rule 
objects for the field.

<span class="line-numbers" data-start="3">

```kotlin
import dev.alpas.validation.*

class CreatePageGuard : ValidationGuard() {
    override fun rules(): Map<String, Iterable<Rule>> {
        return mapOf(
            "username" to listOf(Required(), Max(32), Min(8)),
            "email" to listOf(Required(), Email())
        )
    }

    // feel free to add any additional functions as per your need
    fun logSuccess() {
        call.logger.debug { "Validation was a success!" }
    }
}
```

</span>

All that is needed now is to validate a call using this guard.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.validateUsing(CreatePageGuard::class).logSuccess()
    call.reply("Everything is good!")
}

```

</span>


### Bundled Validation Rules

Alpas comes bundled with some validation rules out of the box.

<div class="sublist">

- `Max(var length: Int)`

The value of the attribute under validation must be less than or equal to the given *length*.

- `Min(length: Int)`

The value of the attribute must be greater than or equal to the given *length*.

- `Required`

The attribute must be present **and** the value must not be `null` or `empty`.

- `NotNull`

The attribute must be present **and** the value must not be `null`. However, it can be `empty`.

- `MustBeInteger`

The value must be of type `Integer` or `Long`.

- `MustBeString`

The value must be of type `String`.

- `MatchesRegularExpression(expression: String)`

The value must match the given regular expression format.

- `Email`

The value must be a properly formatted RFC compliant e-mail address. Alpas uses 
[email-rfc2822-validator](https://github.com/bbottema/email-rfc2822-validator) for validating the e-mail address using
RFC 2822 compliant criteria.

- `Confirm`

Validates that there exists a matching *confirm* field and that the value matches the value of this attribute.

For example, if the attribute under validation is `password`, there must existing an attribute, either 
`password_confirm` **or** `confirm_password` , and the value of it matches the value of `password`. The value of the 
*confirm* field can neither be `null` nor `blank`.

- `Unique(table: String, column: String? = null, ignore: String? = null)`

The value must not exists in the given database **table** for the given **column**. If the **column** is null, the 
name of the attribute will be used for the column.

<span class="line-numbers" data-start="8">

```kotlin

...

// Validates that `ssn` is unique in `users` table.
call.applyRules("ssn") {
    unique("users", "ssn")
}.validate()

// If the attribute under validation is same as that of the column 
// name of the table, you don't have to mention the column name.
call.applyRules("ssn") {
    unique("users")
}.validate()

...

```

</span>

#### Ignoring uniqueness validation for a value

Sometimes you may require to ignore a given value under a column during the unique validation check. Think of creating 
a user profile with an **email** column vs updating that profile later. When creating the profile, you want to make 
sure that no other user exists in the database with the same email address. Your rule for this would look something 
like:

<span class="line-numbers" data-start="8">

```kotlin

...

call.applyRules("email") {
    required()
    unique("users")
}.validate()

...

```

</span>

On the other hand, when updating the profile, you want to let the user update her profile **with** or **without**
updating the **email** address field. If the user only wants to update, say, the name field and **not** the email field,
you do not want to apply email uniqueness validation for this user. Otherwise, the validation would fail every time and
the user won't be able to update her name without also updating her email address.

In this case, you'd want to ignore the uniqueness check but only against this user's id. You could do this by setting 
the `ignore` parameter in the format `<column_name>:<value>`. Your rule for this would look something like:

<span class="line-numbers" data-start="10">

```kotlin

...

call.applyRules("email") {
    required()
    unique("users", ignore="id:4")
}.validate()

...

```

In the above example, the email's uniqueness check is ignored for the row where `id` is *4*. If the email
matches for any other row then the validation fails.

</span>

</div>

### Custom Rules

If none of the rules that come bundled with Alpas satisfy your needs, you can easily create your own. Use
`alpas make:rule` command to quickly create a new rule and modify it as per your need.

```bash

# creates Above.kt class under src/main/kotlin/rules folder
alpas make:rule above

```

Let's say that our new `Above` rule makes sure that the attribute under validation is a number and that it is above 
the given threshold.

<span class="line-numbers" data-start="3">

```kotlin

import dev.alpas.validation.*

class Above(private val threshold: Number, private val message: ErrorMessage = null) : Rule() {
    override fun check(attribute: String, value: Any?): Boolean {
        val valueStr = value?.toString()
        val isValid = valueStr?.toDoubleOrNull()?.let { it > threshold.toDouble() } ?: false

        if (!isValid) {
            error = message?.let { it(attribute, value) } ?: "Above validation failed."
        }

        return isValid
    }
}

// Always provide a ValidationGuard extension method with the same name to make it 
// easy to quickly apply this rule to a call without resorting to a guard class.
fun ValidationGuard.above(threshold: Number, message: ErrorMessage = null): Rule {
    return rule(Above(threshold, message))
}

```

</span>

Now let's apply our new `Above` validation rule to a call.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.applyRules("age") {
        required()
        above(18)
    }.validate()

    call.reply("Welcome adult visitor!")
}

```

</span>

> /tip/ <span> If you need more power and flexibility in your custom validation rule, override 
> `check(attribute: String, call: HttpCall): Boolean` method instead.


### Customizing Error Messages

All the rules that come bundled with Alpas have some sensible error messages set for each validation rule. If you want
to customize it, you can easily do so by passing a callback for the `message` parameter while applying a rule.
The message is of type `ErrorMessage`, which is a typealias for `((String, Any?) -> String)?`— a function
that takes two parameters, the name of the attribute and the actual value—and returns the actual error message.

<span class="line-numbers" data-start="16">

```kotlin

...
call.applyRules("username") {
    max(32) { attr, value ->
        "$attr should be no more than 32 characters long. It is ${value?.toString()?.length} character long."
    }.validate()
}
...

```

</span>


### JSON Validation

#### Checking in JSON body

Alpas doesn't merge a JSON body with the request parameters but instead makes it available as a `jsonBody` property.
While validating, by default, Alpas looks in the query + form + route parameters. If you want to validate a field 
that is within the JSON body, you can easily do so by wrapping your rules in `inJsonBody()` instead.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    // the age value is retrieved from the params; possibly passed as a query param
    call.applyRules("age") {
        required()
        above(18)
    }


    call.applyRules("name") {
        // the name value is retrieved from the JSON body
        inJsonBody {
            notNull()
            mustBeString()
        }
    }

    call.validate()

    call.reply("Welcome adult visitor with a name!")
}

```

</span>


#### JSON Field Rules

Alpas also provides a `JSONField` rule class that takes a list of rules for which you want to check the values in 
the JSON body. This is more convenient when applying a map of rules to a call or when returning a map of rules from
a `ValidationGuard` class.

<span class="line-numbers" data-start="3">

```kotlin
import dev.alpas.validation.*

class CreatePageGuard : ValidationGuard() {
    override fun rules(): Map<String, Iterable<Rule>> {

        return mapOf(
            // Validate both Max(32) and Min(8) rules by 
            // looking up the username value in the JSON body
            "username" to listOf(JsonField(Max(32), Min(8))),

            // Validate both Required() and Email() rules by 
            // looking up the email value in the params list
            "email" to listOf(Required(), Email())
        )
    }
}
```

</span>

### Errors Management

#### Intercepting Validation Errors

By default, when validation fails, Alpas throws a `ValidationException`. If for some reason you want to customize 
this behavior, `ValidationGuard` has an open method of signature: `handleError(errorBag: ErrorBag): Boolean` that you
could override and handle error the way you want it.

If you return a `true` or `Nothing` that means you handled the error and thus Alpas will continue. Returning `false` 
means you want Alpas to do what it usually does after validation fails—throwing an exception.

<span class="line-numbers" data-start="3">

```kotlin

override fun handleError(errorBag: ErrorBag): Boolean {
    // let's say we don't want to redirect back to previous location with errors 
    // but just want to tell the user that we couldn't find the page.

    // `abort()` throws an exception and so returns `nothing`.
    // That's why we don't need to return `true` here.
    call.abort(404)
}

```

</span>

#### Rendering Validation Errors

Alpas internally catches the `ValidationException` thrown and renders it appropriately based on the whether the call 
wants a JSON response or not.If it wants a JSON response then all the generated validation errors are returned as a 
JSON object with a status code of **422**. If JSON response is not wanted, it flashes both the validation errors and
the inputs—except for critical fields such as `password`, `confirm_password`, `password_confirm` etc.—and redirects
the user back to the previous page.

You can get the old inputs from the previous request by calling `old()` method and the validation errors by calling 
`errors()` method on [HttpCall's session object](/docs/sessions#retrieving-data). These are also available from within
your view templates.

#### Displaying Errors and Old Values

For an improved user experience, you'd want to display all the validation errors from user's previous request. You'd
also want to pre-fill all the fields with previous values so not to frustrate users by making them fill the form again.
Alpas comes bundled with some great template helpers to make this a cinch and hassle-free for you!

<div class="sublist">

- `old(key, default)`

If exists, returns an old input value for the given *key*. If not, returns the given *default* value. If multiple 
values exist for the given *key*, it returns a list of values.

<span class="line-numbers" data-start="16">

```twig

<form action="/users" method="post">
    {% csrf %}
    <input type="text" name="username" value="{{ old('username') }}" />
    <input type="email" name="email" value="{{ old('email', 'johndoe@example.com') }}" />
    <button type="submit">Create</button>
</form>

```

</span>

- `errors(key, default)`

If exists, returns an error or a list of errors for the given *key*. If not, returns the given *default* value. `key`
is optional when calling this function. If you don't pass the *key*, it just returns the *errors* map.

- `firstError(key, default)`

If exists, returns the first error for the given *key*. If not, return the given *default* value. Unlike the `errors()` 
method, the `key` here is required.

- `hasError(key)`

Returns `true` if an error exists for the given *key*. Otherwise, returns `false`.

- `whenError(key, value, default)`

If an error exists for the given *key*, it returns the given *value*. If not, it returns the given *default* value.

</div>

