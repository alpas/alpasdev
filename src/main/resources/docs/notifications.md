- [Getting Started](#getting-started)
- [Notifiable](#notifiable)
- [Dispatching Notifications](#dispatching-notifications)
- [Notification Channels](#notification-channels)
- [Notifications Queueing](#notifications-queueing)
- [Mailable Notification](#mailable-notification)
- [Custom Notification Channels](#custom-notification-channels)

Notification allows you to let a user know about an event via channels such as emails, SMS, Slack message
etc. While you can easily [send an email](/docs/mail) using one of the mail drivers, notifications
gives you more flexibility esp. when it comes to delivering a message via different channels.

Let's say you are writing a social media web app that allows users to post a comment. When someone likes
a user's post, you may want to send the author a `PostLiked` email notification. You may also want to
add a notification in the database so that when they log-in to the app, they see like a red badge
notification just like the way Facebook shows it next to a bell icon.

Another example of a notification is when a user requests for a password reset. You can "notify"
the actual email author by sending an email notification. In fact this is what Alpas does when
[sending reset emails](/docs/password-reset).

<a name="getting-started"></a>
### [Getting Started](#getting-started)

A notification is a class implementing `dev.alpas.notifications.Notification<T: Notifiable>` interface.
In practice, however, unless you are writing a custom channel, you don't implement this interface
directly but use one of the out-of-the-box channels—`MailableNotification`,
`SlackableNotification` etc. and override appropriate methods. 

The quickest way to create a notification is by using `make:notification` Alpas command. It will create a notification
that class implements [MailableNotification](#mailable-notification) type under `notifications` folder.

```bash

$ alpas make:notification PostLiked

```

<span class="line-numbers" data-start="1" data-file="notifications/PostLiked.kt">

```kotlin

class PostLiked : MailableNotification<User> {
    override fun channels(notifiable: Authenticatable): List<KClass<out NotificationChannel>> {
        // return a list of channels appropriate for the given notifiable object
        return listOf(MailChannel::class)
    }

    override fun toMail(notifiable: Authenticatable): MailMessage {
        TODO("Return a mail message")
    }
}

```

</span>

<a name="notifiable"></a>
### [Notifiable](#notifiable)

A notifiable object is an object that implements `dev.alpas.notifications.Notifiable` interface to whom a notification
is being sent. Since most of the times you would be sending a notification to an "authenticatable user", the
`Authenticatable` interface already comes with `Notifiable` interface implemented. However, any other
objects can implement this interface to make them compatible with Alpas's notification system.

<a name="dispatching-notifications"></a>
### [Dispatching Notifications](#dispatching-notifications)

To dispatch a notification, you get an instance of `dev.alpas.notifications.NotificationDispatcher`
from a [DI container](/docs/ioc-container) and call `dispatch()` method passing the actual
notification object and the notifiable object.

<span class="line-numbers" data-start="10">

```kotlin

    // ...

    val notification = PostLikedNotification(sender)
    container.make<NotificationDispatcher>().dispatch(notification, receiver)

    // ...

```

</span>

For convenience, your controller classes already have a base `send()` method that you can use to send a
notification easily without having to call the `NotificationDispatcher`'s `dispatch()` method.

<span class="line-numbers" data-start="7">

```kotlin

fun like(call: HttpCall){
    //...

    send(PostLikedNotification(call.user), receiver)

    //...
}

```

</span>

<a name="notification-channels"></a>
### [Notification Channels](#notification-channels)

Every notification must override `channels()` method and must return the class names of all the
channels that the notification will be delivered on. This method receives an instance of a
notifiable object receiving the notification. You can use this notifiable object to
decide what channels to use for delivering the notification.

<span class="line-numbers" data-start="8" data-file="notifications/PostLiked.kt">

```kotlin

//...

override fun channels(notifiable: Authenticatable): List<KClass<out NotificationChannel>> {
    return listOf(MailChannel::class, SlackChannel::class)
}

//...

```

</span>

<a name="notifications-queueing"></a>
### [Notifications Queueing](#notifications-queueing)

More often than not, sending notifications takes some time as they might have to make to an external API call to
deliver the notification. Instead of waiting for the notification to be delivered, you can put the notification
delivery in a queue and send it later.

Not just for the performance reason, you could also decide to add notifications to a queue to be processed by
a different, probably more efficient system that could well have been written in a different programming
language. Queueing a notification can be easily done by overriding `shouldQueue()` method and
returning `true`. It returns `false` by default.

`shouldQueue()` method also receives an instance of the `Notifiable` object to help you make
a better decision to whether to queue the notification or not.

<span class="line-numbers" data-start="5" data-file="notifications/PostLiked.kt">

```kotlin

//...

fun shouldQueue(notifiable: T): Boolean {
    return true
}

//...

```

</span>

>/info/ <span> It's upto a `NotificationChannel` to decide whether to respect the return value of `shouldQueue()`
>or not and how to queue it. It may not make sense for some `NotificationChannel` to queue anything.

<a name="mailable-notification"></a>
### [Mailable Notification](#mailable-notification)

A mailable notification uses `MailChannel` to send a notification in an email form. Your notification must
implement `MailableNotification` interface, overrride `toMail()` method that returns an instance of a
`MailMessage`, and lastly return `MailChannel::class` as one of the channels from `channels()` method.

<span class="line-numbers" data-start="1" data-file="notifications/PostLiked.kt">

```kotlin

class PostLiked : MailableNotification<User> {
    override fun channels(user: User): List<KClass<out NotificationChannel>> {
        return listOf(MailChannel::class)
    }

    override fun toMail(user: User): MailMessage {
       return MailMessage().apply {
            to = user.email
            subject = "Hello There!"
            message = "Someone just liked your post ❤"
       }
    }
}

```

</span>

<a name="custom-notification-channels"></a>
### [Custom Notification Channels](#custom-notification-channels)

If you want to write your own channels to deliver a notification, you can easily do so by
extending a `NotificationChannel` class and overriding the `send()` method.

Let's say we want to write a custom channel called `TextChannel` that delivers a notification to a
user via SMS text. Let's see a complete example of how we go can about writing such a custom
channel ourselves in few easy steps.

<div class="ordered-list">

1. Create a channel and related classes:

<span class="line-numbers" data-start="1" data-file="TextChannel.kt">

```kotlin

class TextChannel : NotificationChannel {
    override fun <T : Notifiable> send(notification: Notification<T>, notifiable: T) {
        val textNotification = notification as TextableNotification
        val actualMessage = textNotification.toText(notifiable)

        // Send the actual message to the notifiable object
    }
}

interface TextableNotification<T : Notifiable> : Notification<T> {
    fun toText(notifiable: T): TextMessage
}

data class TextMessage(val number: Int, val message: String)

```

</span>

2. Once we have our own channel, using it is very simple:

<span class="line-numbers" data-start="2" data-file="notifications/PostLiked.kt">

```kotlin

class PostLiked : TextableNotification<User> {
    override fun channels(user: User): List<KClass<out NotificationChannel>> {
        return listOf(TextChannel::class)
    }

    override fun toText(user: User): TextMessage {
       return TextMessage(12345678, "Someone just liked your post ❤")
    }
}

```

</span>

</div>
