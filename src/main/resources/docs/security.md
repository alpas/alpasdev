- [Form Method Spoofing](#form-method-spoofing)
- [Session Security](#session-security)
    - [Cookies Encryption](#cookies-encryption)
    - [Session Config](#session-config)

Your app, by the virtue of being a public facing web application, needs to be secure by default. As a web developer
you should be aware of the most, if not all, the security measures that you need to take to minimize the risk
of it getting your web apps compromised. While this is ideal, the reality is that it is a daunting
task to know and implement all the security measures, even for a seasoned web developer.

Alpas comes with many security measures to make sure you spend as less time as possible worrying
about the security and focus on delivering an app that your users love and trust.

>/alert/<span>Alpas does its best to implement security measures out-of-the-box to make your web
>app as much secure as possible but eventually the responsibility lies on you to make sure
>you have secured all the borders as far as the security of your app is concerned.</span>

Let's talk about some ways you can keep your app more secure and few things that you just need to be aware of.

<a name="form-method-spoofing"></a>
### [Form Method Spoofing](#form-method-spoofing)

HTTP forms only support **GET** or **POST** but not **PUT**, **PATCH**, or **DELETE**. To use these methods
in your form so that the correct route gets matched, you need to spoof it by passing a hidden field named
`_method` with your form.

<span class="line-numbers" data-start="20">

```html

<form action="/docs" method="post">
    <input type="hidden" name="_method" value="delete"/>
    <button type="submit">Delete</button>
</form>

```

</span>

Method spoofing is enabled by default. But you can disable it by setting `allowMethodSpoofing`
property to `false` in your `AppConfig` class.

<a name="session-security"></a>
### [Session Security](#session-security)

[Session](/docs/sessions) is a very critical component of any web application in terms of both the functionality
it brings to the table and the security risk that gets tagged along with it. Session is the magic sauce behind
making the stateless nature of web work as if it is not stateless.

<a name="cookies-encryption"></a>
#### [Cookies Encryption](#cookies-encryption)

When a browser makes a request, your web app makes a session and attaches an id to the response it sends back. This
id is saved by the browser as a cookie and—as long as they are not expired or deleted—sends it back to the
server in every subsequent requests. The web app uses this id to lookup the client's info and identifies
the client as a guest or a "known" user.

As you can imagine, a little mishandling of these sessions and cookies could leak your sessions and make your
web app prone to [session hijacking](https://en.wikipedia.org/wiki/Session_hijacking) among other threats.

Alpas encrypts all your outgoing cookies by default and decrypts as soon as it receives them to make it totally
transparent to you. The encryption is done using `APP_KEY` key defined in your `.env` file. You want to keep
this key very secret by not sharing with anyone and definitely not pushing it to your version control
system. All your members in your team can have their own unique app keys and the app will work
just as fine.

>/tip/ <span>You can quickly create a new app key by running `alpas make:key` command.</span>

In case you want to skip encrypting some outgoing cookies, you could return a list
of their names from `SessionConfig`'s `encryptExcept` property.

<a name="session-config"></a>
#### [Session Config](#session-config)

You can extend `dev.alpas.SessionConfig` and tweak it as per your requirements. The default
values are set conservatively preferring security over generosity. Consider the
following suggestions when making the changes:

<div class="sublist">

- `httpOnly`

This is set to `true` by default, which makes it so that **all** your cookies are inaccessible
to JavaScript's `Document.cookie` API. If you set it to `false`, your cookies will
be accessible via JavaScript using `document.cookie`.

For a server side web app there are not many cases where you need your cookies to be accessible
via JavaScript. So always set this to `true`, unless you have a very good reason not to.

If you absolutely need a cookie to be accessible from JavaScript, remember that you could set
`httpOnly` flag to `false` for an individual cookie while appending it to a response.
Prefer this over setting the global `httpOnly` to false for better security.

- `lifetime`

The maximum age to be set for all cookies by default. This is set by default to **2 hours**,
which means after two hours the cookie will expire and will no longer be valid.

A negative duration means that this cookie is not stored persistently and will be deleted when
the browser exits. Setting it to 0 means requesting the browser to forget/delete this cookie.

Keep this value to a reasonably minimum time by balancing between not compromising the security and
not making it too annoying for users by requiring their credentials too often, for an example.

</div>
