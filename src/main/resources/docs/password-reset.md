- [The Way it Works](#the-way-it-works)
- [Database Setup](#database-setup)
- [Debugging Reset Emails](#debugging-reset-emails)
- [Customization](#customization)
    - [Not Sending Emails](#not-sending-emails)
    - [Tweaking Email's Look-&-Feel](#tweaking-emails-look-feel)
    - [Redirecting User After Sending Link](#redirecting-user-after-sending-link)
    - [Disabling Password Reset](#disabling-password-reset)

Users forgetting their password and wanting to reset them is a very common thing that happens with an authentication
enabled web app. Alpas provides all the views, controllers, and everything else needed for supporting this feature
without very minimal efforts on your side.

<a name="the-way-it-works"></a>
### [The Way it Works](#the-way-it-works)

Password reset feature of Alpas works by creating a secure token in the database whenever user requests a password
reset email. Any previous tokens will first be removed. After a token is saved in the database, an email is sent to the
user containing a link. When the user clicks this link, they are redirected to `auth/passwords/reset` page, which
asks the user to enter their email address and a password. If the token is not expired and all the user's inputs are
valid, user's record will be updated with a hashed version of the new password, the token is delted, user is logged in,
and then redirected to `/` route.

> /tip/ <span> The password reset token is set to expire in 2 hours by default, which you can extend by overriding 
> `AuthConfig#passwordResetTokenExpiration` property. We recommend keeping it to a low expiration duration for
> security resons. </span>

<a name="database-setup"></a>
### [Database Setup](#database-setup)

As evident by [The way it works](/docs/password-reset/#the-way-it-works) section, we need to secure a password
reset token in the database and we need a table for it. When you scaffolded your project, a migration for creating
both `users` table and `password_reset_tokens` table is created for you. If you have run the migration already,
this table should already exist in the table. If not, you need to migrate your tables using `db:migrate` command.

```bash
alpas db:migrate
```
<a name="debugging-reset-emails"></a>
### [Debugging Reset Emails](#debugging-reset-emails)

The sending of the reset email to the user is done via `SmtpDriver` by default. During development, it is more 
convenient to save email messages locally. Alpas supports saving all your email address to `storage/mails` folder
by using `LocalMailDriver`. To use this driver, set the value of `MAIL_DRIVER` to `local` in your `.env` file.

<a name="customization"></a>
### [Customization](#customization)

<a name="not-sending-emails"></a>
#### [Not Sending Emails](#not-sending-emails) 

By default, after a token is created, a `ResetPassword` notification is dispatched. This notification is responsible
for composing a mail and sending it to the user. If you rather deliver the reset notification by a different means,
say, SMS, then you can override `sendPasswordResetNotification()` method in `ForgotPasswordController` class.

<a name="tweaking-emails-look-feel"></a>
#### [Tweaking Email's Look-&-Feel](#tweaking-emails-look-feel)

`resources/templates/auth/emails/reset.peb` template is what gets rendered and then gets sent as an HTML email to the
user. Feel free to tweak this template according to your needs.

<a name="redirecting-user-after-sending-link"></a>
#### [Redirecting User After Sending Link](#redirecting-user-after-sending-link)

After sending an email with a link, the user is redirected back to same password reset request page with a flash
notification. If you rather want to take them to somewhere else, override 
`fun afterResetLinkSentRedirectTo(call: HttpCall): String?` method and return the proper route.

<a name="disabling-password-reset"></a>
#### [Disabling Password Reset](#disabling-password-reset)

If you do not want to allow your users to reset their password for whatever reasons, you can disable it completely 
by passing `supportPasswordReset = false` flag while calling `authRoutes()` method.

```kotlin

// routes.kt

fun Router.addRoutes() = apply {
    webRoutes()
    // no password reset related routes will be added by setting supportPasswordReset to false
    authRoutes(supportPasswordReset = false)
}

```

> /alert/ <span> Setting `supportPasswordReset` flag to `false` only takes care of the routes, it's your 
> responsibility to remove any references to one of the reset routes from your templates and controllers.</span>
