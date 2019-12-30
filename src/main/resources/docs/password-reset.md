- [Database Setup](#database-setup)
- [Debugging Reset Emails](#debugging-reset-emails)
- [Customization](#customization)
    - [Reset Notification](#reset-notification)
    - [Tweaking Email's Look-&-Feel](#tweaking-emails-look-feel)
    - [Redirecting User After Sending Link](#redirecting-user-after-sending-link)
    - [Disabling Password Reset](#disabling-password-reset)


Allowing users to reset their forgotten passwords is a feature provided by pretty much every app that allows
accounts registration with a password. Alpas provides all the views, controllers, and everything else
an app needs for supporting password reset feature with almost no efforts from your side.

Alpas creates a secure time-boxed token and saves it in the database whenever user requests a password reset email. 
Any previous tokens will first be removed. After the token is saved, an email is sent to the reset email address
containing a link.

When the user clicks this link, they are redirected to `auth/passwords/reset` page, which asks the user to enter
their email address and a password. If the token has not yet expired and all the user's inputs are valid,
user's record is updated with a hashed version of the new password, the token is deleted, user is
logged in and then redirected to `/` route.

>/tip/ <span> The password reset token expires in 2 hours by default, which you can change by overriding
>`AuthConfig#passwordResetTokenExpiration` property. We recommend keeping it to a small expiration
>duration for security reasons. </span>

<a name="database-setup"></a>
### [Database Setup](#database-setup)

We need to store the password reset token in the database for which we need a table. When you scaffold a project,
a migration for creating both `users` table and `password_reset_tokens` table is created for you under 
`database/migrations` folder. If you have run the migration already, this table should already exist
in the table. If not, you need to migrate your tables using `db:migrate` command.

```bash

$ alpas db:migrate

```

<a name="debugging-reset-emails"></a>
### [Debugging Reset Emails](#debugging-reset-emails)

During development, it is very convenient to save email messages locally. Alpas supports saving all your
email messages to `storage/mails` folder by using `LocalMailDriver`. To use this driver, make sure
the value of `MAIL_DRIVER` is set to `local` in your `.env` file.

<a name="customization"></a>
### [Customization](#customization)

<a name="reset-notification"></a>
#### [Reset Notification](#reset-notification) 

By default, after a token is created, a `ResetPassword` notification is dispatched. This notification is
responsible for composing an email and sending it to the user. If you'd rather deliver the notification
by a different means, say, SMS, you can override `sendPasswordResetNotification()` method in
`ForgotPasswordController` class.

<a name="tweaking-emails-look-feel"></a>
#### [Tweaking Email's Look-&-Feel](#tweaking-emails-look-feel)

`resources/templates/auth/emails/reset.peb` template is what gets rendered and sent as an HTML
email to the user. Feel free to tweak this template according to your needs.

<a name="redirecting-user-after-sending-link"></a>
#### [Redirecting User After Sending Link](#redirecting-user-after-sending-link)

After sending an email with a link, the user is redirected back to same reset request page with a flash notification.
If you want to redirect them somewhere else, override `fun afterResetLinkSentRedirectTo(call: HttpCall): String?`
method and return the route you want.

<a name="disabling-password-reset"></a>
#### [Disabling Password Reset](#disabling-password-reset)

If you do not want to allow your users to reset their password, you can disable it completely
by passing `allowPasswordReset = false` flag while calling `authRoutes()` method.

<span class="line-numbers" data-start="3" data-file="routes.kt">

```kotlin

fun Router.addRoutes() = apply {
    webRoutes()
    // No password reset related routes will be added 
    authRoutes(allowPasswordReset = false)
}

```

</span>

>/alert/ <span> Setting `allowPasswordReset` flag to `false` only makes sure the reset routes are not added but doesn't
>remove links from templates and controllers. Make sure to go through all your templates and controllers and remove
>any links to password reset route. There should be one such link in `resources/templates/auth/login.peb`.
