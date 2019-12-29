- [Validation Rules](#validation-rules)
    - [Failing Fast](#failing-fast)
- [Validation Guard](#validation-guard)
- [Bundled Validation Rules](#bundled-validation-rules)
    - [Ignoring Uniqueness Validation](#ignoring-uniqueness)
- [Custom Rules](#custom-rules)
- [Customizing Error Messages](#customizing-error-messages)
- [JSON Validation](#json-validation)
    - [Checking in JSON Body](#json-body)
    - [JSON Field Rules](#json-field-rules)
- [Errors Management](#errors-management)
    - [Intercepting Validation Errors](#intercepting-validation-errors)    
    - [Rendering Validation Errors](#rendering-validation-errors)
    - [Displaying Errors and Old Values](#displaying-errors-and-old-values)

When handling a request, and especially when handling user input, you want to make sure that the incoming data is
valid and as per your expectation. If it is not, you want to send appropriate error messages back to the user.
You want to have the error messages available in your templates to format and display them nicely. All
these need a lot of wiring. Fortunately, Alpas supports all these out-of-the-box!

<a name="validation-rules"></a>
### [Validation Rules](#validation-rules)

The simplest and quickest way to validate an `HttpCall` is by applying some validation rules on it for
each of the input params you want to validate.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.applyRules("username") {
        required()
        min(8)
        max(32)
    }

    call.applyRules("email") {
        required()
        email()
    }

    // don't forget to call validate() once all the rules are applied
    call.validate()
    
    // program reaches here only if all of your validations pass
    call.reply("Everything is good!")
}

```

</span>

Instead of applying multiple rules, you could also pass a map of rules where the key is the
name of the field, and the value is a list of *rule objects* for the field.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    val rules = mapOf("username" to listOf(Required(), Max(32), Min(8)))
    call.applyRules(rules).validate()

    call.reply("Everything is good!")
}

```

</span>

<a name="failing-fast"></a>
#### [Failing Fast](#failing-fast)

Sometimes you may wish to not continue with the validation after the first validation fails i.e.—bail
on the first error. To achieve this, set `failfast` to `true` when applying your rules.

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

If you want to apply and validate rules attribute-by-attribute, continuing to the next attribute only
after each attribute passes a rule set, you can just call `validate()` after each set of rules.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    call.applyRules("username") {
        required()
        min(8)
        max(32)
    }.validate() // validates your call right away and only proceeds if the validation passes

    // username should be valid at this point
    call.applyRules("email") {
        required()
        email()
    }.validate()

    call.reply("Everything looks good!")
}

```

</span>

<a name="validation-guard"></a>
### [Validation Guard](#validation-guard)

Although the in-place application of validation rules within a controller may be good enough for simple
validation rules, it is desirable to better organize validation rules to make them more manageable.
You may also want to do more within the rules, such as caching an expensive database lookup to be
retrieved later, or reuse the validation rules from other places.

You definitely don't want all these complex logic scattered all over the places in your controller.
To facilitate this, Alpas allows you to validate a call using a dedicated `ValidationGuard` class.

To create a `ValidationGuard` class, you can use the `make:guard` Alpas command. This creates
a new validation guard class under `guards` folder.

```bash

alpas make:guard CreatePageGuard

```

If you open the newly generated guard class you'll see that a `rules()` method is already overriden
for you. All you need to do is to return a map of rules where the key is the name of the field,
and the value is a list of rule objects for the field.

<span class="line-numbers" data-start="2" data-file="guards/CreatePageGuard.kt">

```kotlin

import dev.alpas.validation.*

class CreatePageGuard : ValidationGuard() {
    override fun rules(): Map<String, Iterable<Rule>> {
        return mapOf(
            "username" to listOf(Required(), Max(32), Min(8)),
            "email" to listOf(Required(), Email())
        )
    }

    // feel free to add any additional methods and properties your need
    internal fun logSuccess() {
        call.logger.debug { "Validation was a success!" }
    }
}

```

</span>

All that is needed now is to validate a call using this guard.

<span class="line-numbers" data-start="15">

```kotlin

fun create(call: HttpCall) {
    //...

    call.validateUsing(CreatePageGuard::class).logSuccess()
    call.reply("Everything is good!")
}

```

</span>

<a name="bundled-validation-rules"></a>
### [Bundled Validation Rules](#bundled-validation-rules)

Alpas comes bundled with some validation rules out of the box.

<div class="sublist">

- `Max(var length: Int)`

The value of the attribute under validation must be less than or equal to the given `length`.

- `Min(length: Int)`

The value of the attribute must be greater than or equal to the given `length`.

- `Required`

The attribute must be present **and** the value must not be `null` or `empty`.

- `NotNull`

The attribute must be present **and** the value must not be `null`. However, it can be `empty`.

- `MustBeInteger`

The value must be of type `Integer` or `Long`.

- `MustBeString`

The value must be of type `String`.

- `MatchesRegularExpression(expression: String)`

The value must match the given regular `expression` format.

- `Email`

The value must be a properly formatted RFC compliant e-mail address.

Alpas uses [email-rfc2822-validator](https://github.com/bbottema/email-rfc2822-validator)
for validating an e-mail address using RFC 2822 compliant criteria.

- `Confirm`

Validates that there exists a matching *confirm* field and that the value matches the value of this attribute.

For example, if the attribute under validation is `password`, there must existing an attribute,
either `password_confirm` **or** `confirm_password`, and the value of it must match the value
of `password`. The value of the *confirm* field can neither be `null` nor `blank`.

- `Unique(table: String, column: String? = null, ignore: String? = null)`

The value must not exists in the given database *table* for the given *column*.
If the *column* is null, the name of the attribute will be used for the column.

<span class="line-numbers" data-start="8">

```kotlin

// ...

// Validates that `ssn` is unique in `users` table.
call.applyRules("ssn") {
    unique("users", "ssn")
}.validate()

// If the attribute under validation is same as that of the column 
// name of the table, you don't have to mention the column name.
call.applyRules("ssn") {
    unique("users")
}.validate()

// ...

```

</span>

<a name="ignoring-uniqueness"></a>
#### [Ignoring uniqueness validation for a value](#ignoring-uniqueness)

Sometimes you may require to ignore a given value under a column during the unique validation check.
Think of creating a user profile with an `email` column **vs** updating that same profile later.
When creating the profile, you want to make sure no other user exists in the database with
the same email address. Your validation rule for this would look something like:

<span class="line-numbers" data-start="8">

```kotlin

// ...

call.applyRules("email") {
    required()
    unique("users")
}.validate()

// ...

```

</span>

On the other hand, when updating the profile, you want to let the user update their profile **with**or
**without** updating the `email` address field. If the user only wants to update, say, the `name`
field and **not** the `email` field, you do not want to apply email uniqueness validation rule
for this user. Otherwise, validation would fail every time, and the user would't be able to
update their name without also updating their email address.

In this case, you need to ignore the uniqueness check but only against the user's id. You
can achieve this by setting the `ignore` parameter in the format `<column_name>:<value>`.
Your rule for this would look something like:

<span class="line-numbers" data-start="10">

```kotlin

// ...

call.applyRules("email") {
    required()
    unique("users", ignore="id:4")
}.validate()

// ...

```

In the above example, the uniqueness check for user's email is ignored for the row where `id` is *4*.
But if them email matches for any other rows then the validation fails.

</span>

</div>

<a name="custom-rules"></a>
### [Custom Rules](#custom-rules)

If none of the rules that come bundled with Alpas satisfy your needs, you can easily create your own.
To start quickly, use `alpas make:rule` command to create a new rule under `rules` folder.

```bash

alpas make:rule above

```

Let's say that our new `Above` rule makes sure that the attribute under validation is
a number and that it is above a given threshold.

<span class="line-numbers" data-start="3" data-file="rules/Above.kt">

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

> /tip/ <span> If you need more power and flexibility in your custom validation rule,
>override `check(attribute: String, call: HttpCall): Boolean` method instead.

<a name="customizing-error-messages"></a>
### [Customizing Error Messages](#customizing-error-messages)

All the rules that come bundled with Alpas have some sensible error messages set for each validation rule.
If you want to customize a message, you can easily do so by passing a callback for the `message`
parameter when applying a rule. The message is of type `ErrorMessage`, which is a typealias
for `((String, Any?) -> String)?`. It is a function that takes two parameters—the name
of the attribute and the actual value—and returns an error message.

<span class="line-numbers" data-start="16">

```kotlin

// ...
call.applyRules("username") {
    max(32) { attr, value ->
        val length = value?.toString()?.length
        "$attr is $length characters long. It should be no more than 32."
    }.validate()
}
// ...

```

</span>

<a name="json-validation"></a>
### [JSON Validation](#json-validation)

<a name="json-body"></a>
#### [Checking in JSON Body](#json-body)

Alpas doesn't merge a JSON body with the request parameters but instead makes it available as a `jsonBody` property.
During validation, by default, Alpas looks in the query + form + route parameters. If you want to validate a
field that is within the JSON body, you can do so by wrapping your rules in `inJsonBody()` instead.

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

<a name="json-field-rules"></a>
#### [JSON Field Rules](#json-field-rules)

Alpas also provides a `JSONField` rule class that takes a list of rules for which you want to check the
values in the JSON body. This is more convenient when applying a map of rules to a call or when
returning a map of rules from a `ValidationGuard` class.

<span class="line-numbers" data-start="3" data-file="guards/CreatePageGuard.kt">

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

<a name="errors-management"></a>
### [Errors Management](#errors-management)

<a name="intercepting-validation-errors"></a>
#### [Intercepting Validation Errors](#intercepting-validation-errors)

By default, when validation fails, Alpas throws a `ValidationException`. If you want to customize this behavior,
`ValidationGuard` has an open method with signature: `handleError(errorBag: ErrorBag): Boolean` that you can
override and handle error the way you want it.

From this method, returning a `true` or `Nothing` means you handled the error and thus Alpas will continue.
Returning `false` means you want Alpas to do what it usually does after validation fails—throwing a
validation exception.

<span class="line-numbers" data-start="3" data-file="guards/CreatePageGuard.kt">

```kotlin

//...

override fun handleError(errorBag: ErrorBag): Boolean {
    // Let's say we do not want to redirect the user back to previous location 
    // with errors but just want to tell them that we couldn't find the page.

    // `abort()` throws an exception and so returns `nothing`.
    // That's why we don't need to return `true` here.
    call.abort(404)
}

//...

```

</span>

<a name="rendering-validation-errors"></a>
#### [Rendering Validation Errors](#rendering-validation-errors)

Alpas internally catches the `ValidationException` thrown and [renders it](/docs/error-handling#handling-exceptions)
appropriately based on whether the caller [wants a JSON response](/docs/http-request#wants-json) or not.

If it wants a JSON response, the validation errors are returned as a JSON object with **422** status code.
If JSON response is not wanted, it flashes both the validation errors and the current inputs—except for
critical fields such as `password`, `confirm_password`, `password_confirm` etc.—and
[redirects the user back](/docs/http-response/#redirect-back) to the previous page.

You can get the old inputs from the previous request by calling `old()` method and the validation errors
by calling `errors()` method on [HttpCall's session object](/docs/sessions#retrieving-data). These are
also available from your [Pebble templates](/docs/pebble-templates).

<a name="displaying-errors-and-old-values"></a>
#### [Displaying Errors and Old Values](#displaying-errors-and-old-values)

For an improved user experience, you would want to display all the validation errors from the user's
previous request. You'd also want to pre-fill all the fields with previous values so not to punish
users by making them fill the form again. Alpas comes bundled with some great template helpers
to make this a cinch and hassle-free for you!

<div class="sublist">

- `old(key, default)`

If exists, returns an old input value for the given `key`. If not, returns the given `default` value.
If multiple values exist for the given `key`, it returns a list of all values.

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

If exists, returns an error or a list of errors for the given `key`. If not, returns the given `default` value.
`key` is optional when calling this function. If you don't pass it, it just returns the *errors* map.

- `firstError(key, default)`

If exists, returns the first error for the given `key`. If not, return the given `default` value.
Unlike the `errors()` method, the `key` here is required.

- `hasError(key)`

Returns `true` if an error exists for the given `key`. Otherwise, returns `false`.

- `whenError(key, value, default)`

If an error exists for the given `key`, it returns the given `value`. If not, it returns the `default` value.

</div>

