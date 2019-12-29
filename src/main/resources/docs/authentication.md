- [Auth Channels and User Providers](#auth-channels-providers)
    - [Auth Channel](#authchannel)
    - [User Provider](#userprovider)
    - [Auth Channel Registration](#auth-channel-registration)
- [HttpCall and Authentication](#httpcall-and-authentication)
- [Custom Auth Channel](#custom-auth-channel)
- [Route Auth Channels](#route-auth-channels)
- [Authentication Middleware](#authentication-middleware)

> /info/ This section contains some advanced concepts, and many times dives deeper into some internal workings
>of Alpas. This is done intentionally to teach how authentication works to those who are unfamiliar with
>it as well as talk about Alpas's internal implementation for the curious among us. It is okay if you
>get overwhelmed and or get confused. Just read it once, take a break, and come back again when you
>feel like it and read it again. Eventually it will click!

Alpas comes an authentication system setup right out-of-box and requires you to do the absolute minimal to integrate
it with your app. It is easy to get started with just one simple [Alpas command](/docs/authentication-scaffolding).

Authentication sounds magical and complex but at the core mechanism is a very simple—you look for an
id, and you match the id. If the id matches, you fetch more information about that id and mark the
current session being authenticated. That's it!

However, there are few moving parts that make this magic work. Before we get into scaffolding an authentication
boilerplate, let's talk about some core concepts that you need to understand about authentication first.

<a name="auth-channels-providers"></a>
### [Auth Channels and User Providers](#auth-channels-providers)

At the core, Alpas's authentication is divided into two parts—`auth channels` and `user providers`. Channels define
how users are authenticated for each incoming request. `SessionAuthChannel` is an example of an authentication
channel that determines whether a user is authenticated or not based on sessions and cookies.

For a modern web app, only determining whether a user is authenticated or not isn't enough. You need to know
more about the user as well. This is where providers come into play. They are responsible for retrieving
user information from a persistent storage such as a database. Alpas ships with one such `UserProvider`
class already wired to retrieve user info from a database.

<a name="authchannel"></a>
#### [Auth Channel](#authchannel)

Each `HttpCall` gets its own instance of an `AuthChannel` that you can use for authentication related tasks.
`SessionAuthChannel` is one of the concrete implementations of `AuthChannel` but there could be more
including one of your own.

The following methods are few of the methods available on `AuthChannel`.
Let's see them through the lens of `SessionAuthChannel` implementation.

<div class="sublist">

- `attempt(id: String, password: String): Boolean`

Attempts to find a user with the given `id` and `password`. If the user is found by the `UserProvider`
by matching the id then it matches the given password's hash with the hashed password saved by the
`UserProvider`. If they match, it logs the user in and returns `true` otherwise returns `false`. 

Use this method to login a user if you already know the *id* and the *password*. For an example,
the `LoginController` uses this method to login a user when the user signs in using a form.

- `check(): Boolean`

Checks whether a user is logged in or not. If not, it attempts to login the user by looking up
an id in the current session and fetching a user by that id from the `UserProvider` class.

Use this method if you want to check if the current session matches with a valid user or not. For an example,
`AuthOnlyMiddleware` uses this method to check the logged in user and forwards the call if session
matches to an existing user in the `UserProvider` otherwise it throws an exception.

- `isLoggedIn(): Boolean`

This call simply checks whether the session is an authenticated session or not.
It doesn't attempt to lookup and login a user.

- `login(user: Authenticatable)`

Logs in the given user. Use this method if you already have an `Authenticatable` object.

- `loginUsingId(id: Any) : Authenticatable?`

Attempts to fetch a user for the given `id` from the `UserProvider`. If it exists, it logs the
user in and returns it as well. If the user couldn't be fetched, returns `null` instead.

- `logout()`

Logs out a user by invalidating the session. `LoginController` invokes this method when logging out a user. 

If you use this method directly, make sure to redirect the user to a different route as soon as you logout
and make sure to not touch the current session after logging out as the session is no longer valid.

</div>

<a name="userprovider"></a>
#### [User Provider](#userprovider)

A concrete implementation of `UserProvider` interface is responsible for fetching a user from a persistent store.
 
When we were [talking about `AuthChannel`](#auth-channel), notice how we were careful not talking
retrieving user from **a database**!

While for most of the apps it is true that you would be fetching users from a database but that just the
implementation details. `UserProvider`'s consumer doesn't care how you retrieve a user as long as you
correctly retrieve it. In fact, you can write your own implementation of `UserProvider` that looks
up a user in a hashmap if you want as long as your implementation provides following two methods:

<div class="sublist">

- `findByUsername(username: Any): Authenticatable?`

Find a user by the given `username`. The username could mean anything for your app and *you* get to define it.
For most of the web apps, it is an email address.

- `findByPrimaryKey(id: Any): Authenticatable?`

Find a user by the given primary key id. The primary key doesn't mean it has to be a primary key constraint
in your database but for most of the apps it is.

</div>

<a name="auth-channel-registration"></a>
#### [Auth Channel Registration](#auth-channel-registration)

You can configure what `AuthChannel` to use by default in multiple ways. 

When scaffolding a new project, Alpas creates an `AuthConfig` class for your. If you open it, you'll see
that it adds a new channel under `session` name. Notice how the value of the channel is **not** an
instance of `SessionAuthChannel` but is a callback that creates the actual instance. 

As discussed earlier, a new instance of an auth channel is created for every call. This makes sense
because each call could be associated with different users; we don't want to share it among calls.

Also, notice how the second parameter is a `Users` object, which implements `UserProvider` interface. This
means `SessionAuthChannel` uses `Users` object when it needs to fetch an `Authenticatable` object. It
just happens that `Users` object fetches it from a database, but remember it doesn't need to!

Remember that when adding an auth channel all we are doing is registering a channel but **not** using
it at this time. This means you can add another auth channel of your own and give it a name.

So how does Alpas know what auth channel to use?

Well, you can decide what auth channel to apply by default to all calls by setting `AUTH_CHANNEL`
variable in your `.env` file. If you didn't set any, Alpas uses the name called `session` by
default. This is the reason why when registering an auth channel in `AuthConfig`, we named
it `session` because that's the default! We told you, it will click :)

<a name="httpcall-and-authentication"></a>
### [HttpCall and Authentication](#httpcall-and-authentication)

For most of the apps you'll never have to deal with all the concepts we talked about upto this point. Most of the
times you are only concerned about whether the current user is authenticated or not and, if authenticated,
who is authenticated. `HttpCall` proxies all these. All you need is to know the following properties:

<div class="sublist">

- `authChannel: AuthChannel`

Returns the auth channel that was applied to the current call. If no [custom session](#route-auth-channels) is set
for the current route, it just returns the default session channel as configured in your `AuthConfig` class.

- `userProvider: UserProvider?`

Returns the user provider for the current call.

- `isAuthenticated: Boolean`

Returns `true` if the current call is authenticated. If not, returns `false`.

- `isFromGuest: Boolean`

Checks if the current call is from a guest; opposite of `isAuthenticated`.

- `user: Authenticatable`

Returns the current user if the call is authenticated. If not, aborts the call by throwing a `NotFoundHttpException`.
If you don't want abort the call, only call this after checking `isAuthenticated` property.

</div>

<a name="custom-auth-channel"></a>
### [Custom Auth Channel](#custom-auth-channel)

To create a custom auth channel, you just need to implement `AuthChannel` interface and
override a couple of things. The most important one being the `check()` method.

Let's say, hypothetically, we want to write an auth channel that checks **X-ADMIN-API-KEY**
header of the incoming request and if it matches your API key then you consider this
session to be authenticated. The user is then authorized as an admin.

<span class="line-numbers" data-start="4" data-file="AdminApiAuthChannel.kt">

```kotlin

class AdminApiAuthChannel(private val call: HttpCall) : AuthChannel {
    override var user: Authenticatable? = null
    override fun check(): Boolean {
        val authHeader = call.header("X-ADMIN-API-KEY") ?: return false
        val isAuthenticated = 
        if(authHeader == "abracadabra"){
            // fetch the user somehow
            user = Users.findByRole("admin").firstOrNull()
        }
        return user != null
    }
}

```

</span>

<span class="line-numbers" data-start="2" data-file="configs/AuthConfig.kt">

```kotlin

class AuthConfig(env: Environment) : BaseConfig(env) {
    init {
        addChannel("session") { call -> AdminApiAuthChannel(call) }
    }
}

```

</span>

Now `AdminApiAuthChannel` will be used for authenticating all auth only routes.

<a name="route-auth-channels"></a>
### [Route Auth Channels](#route-auth-channels)

You may want to have multiple auth channels for your app depending on different routes.

For an example, while `SessionAuthChannel` may be appropriate for web routes, it may
not be appropriate for API routes. API routes will probably use some other ways of
validating a user—such as checking a personal token in the header rather than
checking the cookies—and you might want a dedicated auth channel for this.

Alpas allows you to easily apply an auth channel for a specific route, or a route group
by passing an auth channel name to `mustBeAuthenticated()` method.

<span class="line-numbers" data-start="6" data-file="routes.kt">

```kotlin

fun Router.addRoutes() {
    // authenticate the user using an auth channel named "api"
    group("/api/docs") {
        get(DocsController::class)
    }.mustBeAuthenticated("api")

    // authenticate the user using an auth channel named "session"
    group("/web/admin") {
        get(ProfileController::class)
    }.mustBeAuthenticated("session")
}

```

</span>

<span class="line-numbers" data-start="2" data-file="configs/AuthConfig.kt">

```kotlin

class AuthConfig(env: Environment) : BaseConfig(env) {
    init {
        addChannel("session") { call -> SessionAuthChannel(call, Users) }
        addChannel("api") { call -> ApiAuthChannel(call) }
    }
}

```

</span>

>/tip/ <span> `alpas route:list` command shows more information about all your routes in a nicely
>formatted table. It also shows auth channels applied to each authorized only routes.</span>

<a name="authentication-middleware"></a>
### [Authentication Middleware](#authentication-middleware)

You need to assign `AuthOnlyMiddleware` to each of your routes that you want to be accessible only to
[authorized users](/docs/routing#guarded-routes). If you want some routes to be accessible only by
guests and not by authorized users then you need to apply `GuestOnlyMiddleware` instead.

`AuthConfig`'s `ifUnauthorizedRedirectToPath` property decides where to redirect unauthorized
users trying to access authorized only routes. This is set to `/login` by default.

Similarly, `AuthConfig`'s `ifAuthorizedRedirectToPath` property decides where to redirect users who
are already authorized but trying to access guests only routes. This is set to `/` by default.

You are more than welcome to change these properties to fit your needs.
