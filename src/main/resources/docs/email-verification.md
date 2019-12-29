- [Database Setup](#database-setup)
- [Entity Setup](#entity-setup)
- [Route Setup](#route-setup)
- [Debugging Verification Emails](#debugging-verification-emails)
- [Customization](#customization)
    - [Verify Notification](#verify-notification) 
    - [Tweaking Email's Look-&-Feel](#tweaking-emails-look-feel)
    - [Disabling Email Verification](#disabling-verification)

If your web app accepts an email address during registration, you may want to verify the email address to check the
legitimacy of it. Alpas supports sending and verifying email out-of-the box and saves you from re-implementing this
convenient but time-consuming feature for each application. It requires almost no efforts on your side other than
making sure that you have the proper mail driver to actually deliver the verification request emails.

During registration, Alpas checks whether `User` entity's `mustVerifyEmail` flag is set to `true` or not. If it is,
then it creates a signed time-boxed link and sends an email to the user.

When user clicks the link, it first asks user to login and then checks the validity of the link. If it is
correct, unmodified, and unexpired, Alpas proceeds to mark the user as verified by setting
`email_verified_at` column of `users` table to current timestamp. After verification is
successfully completed, the user is then redirected to `/home` route.

If the logged-in user is yet to verify their email address, they are shown `resources/templates/auth/verify` template.

<a name="database-setup"></a>
### [Database Setup](#database-setup)

Your `users` table only need a nullable timestamp column to save the current timestamp when the user's
email is verified. By default, Alpas already includes this column to `users` table migration during
scaffolding. Once you run the database migrations, this column will be added to the table.

<a name="entity-setup"></a>
### [Entity Setup](#entity-setup)

`User` entity implements `Authenticatable` interface, which has the two attributes required for email
verification to work—`mustVerifyEmail` property and `isEmailVerified()` method. By default, these
are already set to enable verification during registration, so you don't have to do anything.

<a name="route-setup"></a>
### [Route Setup](#route-setup)

To enable verification, `requireEmailVerification` parameter must be set to `true` when calling
`authRoutes()`. For you convenience, this is set to `true` by default.

**You must apply** `VerifiedEmailOnlyMiddleware` to each of your routes that you
[want to protect](/docs/routing#guarded-routes) and only want verified users to
have access. Alternatively, you can also call `mustBeVerified()` method on
a route, or a route group to have Alpas apply the middleware for you.

<span class="line-numbers" data-start="5" data-file="routes.kt">

```kotlin

fun Router.addRoutes() = apply {
    authRoutes()

    // anyone can access this route
    get("/", WelcomeController::class)
    // only guests can access this route
    get("/docs", DocsController::class).mustBeGuest()
    // only logged in users can access this route
    get("/profile", UserController::class).mustBeAuthenticated()
    // only authenticated and verified users can access this route
    get("/admin", AdminController::class).mustBeAuthenticated().mustBeVerified()
}

```

</span>

<a name="debugging-verification-emails"></a>
### [Debugging Verification Emails](#debugging-verification-emails)

During development, it is very convenient to save email messages locally. Alpas supports saving all your
email messages to `storage/mails` folder by using `LocalMailDriver`. To use this driver, make sure
the value of `MAIL_DRIVER` is set to `local` in your `.env` file.

<a name="customization"></a>
### [Customization](#customization)

<a name="verify-notification"></a>
#### [Verify Notification](#verify-notification) 

By default, after registration, if email verification is required by your app, a `VerifyEmail`
[notification is dispatched](/docs/notifications). This notification is responsible for
composing an email and sending it to the user.

If you'd rather deliver the verification notification by a different means, say, SMS, then you can
override `sendVerificationNotice()` method in `controllers/auth/RegisterController` class
**as well as** in `controllers/auth/EmailVerificationController` class.

<a name="tweaking-emails-look-feel"></a>
#### [Tweaking Email's Look-&-Feel](#tweaking-emails-look-feel)

`resources/templates/auth/emails/verify.peb` template is what gets rendered and sent as an HTML
email to the user. Feel free to tweak this template according to your needs.

<a name="disabling-verification"></a>
#### [Disabling Email Verification](#disabling-verification)

If you do not need your users to verify their email addresses, you can disable it completely by passing 
`requireEmailVerification = false` flag while calling `authRoutes()` method.

<span class="line-numbers" data-start="3" data-file="routes.kt">

```kotlin

fun Router.addRoutes() = apply {
    webRoutes()
    // No email verification related routes will be added 
    authRoutes(requireEmailVerification = false)
}

```

Also, you must override and set `mustVerifyEmail` to `false` in your `User` interface in `entities/User.kt` file.

</span>
