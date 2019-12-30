- [Available Drivers](#available-drivers)
- [Getting Started](#getting-started)
- [Composing and Sending Emails](#composing-and-sending-emails)
- [Using View Templates](#using-view-templates)
- [Debugging Emails](#debugging-emails)
- [Custom Mail Driver](#custom-mail-driver)

Alpas uses [Simple Java Mail](https://github.com/bbottema/simple-java-mail) library and sprinkles it
with some of its own APIs to make it easy for you to send emails from within your app. You can
start with an SMTP driver or a local file driver or write one of your own drivers.

<a name="available-drivers"></a>
### [Available Drivers](#available-drivers)

Currently, Alpas comes bundled with 2 mail drivers:

<div class="sublist">

* SMTP Driver (*smtp*)
* Local Mail Driver (*local*)

</div>

<a name="getting-started"></a>
### [Getting Started](#getting-started)

If you open `configs/MailConfig.kt`, you'll notice that Alpas has lazily loaded two drivers for you—`smpt` for SMTP
Driver and `local` for the Local Mail Driver.You can get an instance of one of these drivers during the runtime
by calling `driver()` method on `MailConfig` class. You can pass the name of a driver or use the default driver.
You can decide to always use a specific driver by setting `MAIL_DRIVER` variable to one of the driver names.

<a name="composing-and-sending-emails"></a>
### [Composing and Sending Emails](#composing-and-sending-emails)

To compose an email, create an instance of `MailMessage` class and set the properties such as `to`,`subject`, 
`message` etc. Once the mail is composed, send it via one of the mail drivers' `send()` method.

<span class="line-numbers" data-start="5">

```kotlin

fun send(call: HttpCall) {
   //...

   val mail = MailMessage().apply {
        to = "hello@example.com"
        subject = "Hello There!"
        message = "Just want to say hi!"
   }
   call.config<MailConfig>().driver().send(mail)

   //...
}

```

</span>

<a name="using-view-templates"></a>
### [Using View Templates](#using-view-templates)

While composing an email, instead of using plain text, you can also call `view()` method and pass
the name of the view template, and an optional argument map to render the email's content.

<span class="line-numbers" data-start="5">

```kotlin

fun send(call: HttpCall) {
   val mail = MailMessage().apply {
        to = "hello@example.com"
        subject = "Hello There!"
        view("mails.welcome", mapOf("name" to "Jane"))
   }.also {
        // don't forget to call the render method before sending the mail
        it.render(call.make())
   }
   call.config<MailConfig>().driver().send(mail)
}

```

</span>


<a name="debugging-emails"></a>
### [Debugging Emails](#debugging-emails)

During development, it is very convenient to save email messages locally for debugging. Alpas supports
saving all your email messages to `storage/mails` folder by using `LocalMailDriver`. To use this
driver, make sure the value of `MAIL_DRIVER` is set to `local` in your `.env` file.

If you really wish to send emails across the wire for real testing during development, you can use a
service like [Mailtrap](https://mailtrap.io/), which collects all your emails in a dummy demo inbox.

<a name="custom-mail-driver"></a>
### [Custom Mail Driver](#custom-mail-driver)

It is easy to add a custom driver. All you have to do is create a class that implements 
`dev.alpas.mailing.drivers.MailDriver` interface and override `send(mail: MailMessage)`
method. Eventually, register this new driver in `MailConfig` class under a name.

Let's see and example of how to write your own [SparkPost](https://sparkpost.com) driver by
wrapping their official [Java client API](https://github.com/SparkPost/java-sparkpost).

<div class="ordered-list">

1. Create the driver:

<span class="line-numbers" data-start="2" data-file="SparkPostMailDriver.kt">

```kotlin

import dev.alpas.mailing.MailMessage
import dev.alpas.mailing.drivers.MailDriver

class SparkPostDriver(val apiKey: String, defaultFromAddress: String) : MailDriver {
    override fun send(mail: MailMessage) {
        val client = Client(apiKey)
        client.sendMessage(defaultFromAddress, mail.to, mail.subject, mail.text, mail.text)
    }
}

```

</span>

2. Register the driver:

<span class="line-numbers" data-start="3" data-file="configs/MailConfig.kt">

```kotlin

class MailConfig(env: Environment) : BaseConfig(env) {
    init {
        // ...
        // ...

        addDriver("sparkpost", lazy { 
            SparkPostDriver(env("SPARKPOST_API_KEY")!!, env("MAIL_FROM_ADDRESS")) 
        })
    }
}

```

</span>

3. Use the driver:

<span class="line-numbers" data-start="5">

```kotlin

fun send(call: HttpCall) {
   //...

   val mail = MailMessage().apply {
        to = "hello@example.com"
        subject = "Hello There!"
        message = "Just want to say hi!"
   }
   call.config<MailConfig>().driver("sparkpost").send(mail)

   //...
}

```

</span>

</div>
