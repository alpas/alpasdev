- [Form Method Spoofing](#form-method-spoofing)
- [Session Security](#session-security)
    - [Cookies Encryption](#cookies-encryption)
    - [Session Config](#session-config)

Your app, by the virtue of being a public facing web application, needs to be secure. As a web developer you should 
be aware of the most, if not all, the security measures that you need to take to minimize the risk of it getting 
compromised as much as possible. While this is true, the reality is that it is a daunting task to know all the
security measures that you need to adopt, even for a seasoned web developer.

Alpas comes with many security measures to make sure that you spend as minimum time as possible to worrying about the
security and focus on delivering an app that your users love and trust.

> /alert/<span>Alpas does its best to give you enough tools to make your web app as much secure as possible but 
> eventually the responsibility is on you to make sure that you have locked all the borders as far as security 
> is concerned.</span>

Let's talk about some of the ways you could keep your app more secure and some of the things that you just need to
be aware of.

<a name="form-method-spoofing"></a>
### [Form Method Spoofing](#form-method-spoofing)

HTTP forms only support **GET** or **POST** but not **PUT**, **PATCH**, or **DELETE**. To use these methods 
in your form so that the correct route gets matched, you need to spoof that method by passing a hidden field 
named `_method` with your form.

<span class="line-numbers" data-start="20">

```html

<form action="/docs" method="post">
    <input type="hidden" name="_method" value="delete"/>
    <button type="submit">Delete</button>
</form>

```

</span>

Method spoofing is enabled by default but for only if the original HTTP method is `POST`. You can disable method 
spoofing setting `allowMethodSpoofing` property in your own `AppConfig` class.

<a name="session-security"></a>
### [Session Security](#session-security)

Session is a very critical component of any web application in both terms of the functionality it brings to the table
and the security risk that gets tagged along with it. Session is the magic sauce behind making the stateless nature of 
web work as if it not stateless.

<a name="cookies-encryption"></a>
#### [Cookies Encryption](#cookies-encryption)

When a browser makes a request, your web app makes a session and attaches an id to the response it sends back. This
id is saved by the browser as a cookie and sends back to the server in the subsequent requests. The web app uses
this id to lookup the client's info and identifies the client as whether a guest or a "known" user. As you can imagine
a little mishandling of these sessions and cookies could leak your sessions or make your web app prone to session
hijacking among other threats.

Alpas encrypts all your outgoing cookies by default and decrypts as soon as it receives them to make it transparent
to you. The encryption is done using a key defined in your `.env` file as `APP_KEY`. You want to keep this key
secret by not sharing with anyone and not pushing it to version control system. All your members in your team could
have their own unique app keys and the app would work just as fine. You can quickly create a new app key by
running `alpas make:key` command. 

Although we don't recommend it, you could skip encrypting of a specific cookie by returning a list of cookies' names
from `SessionConfig`'s `encryptExcept` property.

<a name="session-config"></a>
#### [Session Config](#session-config)

You could extend `dev.alpas.SessionConfig` and tweak it as per your requirements. The default values are set 
conservatively preferring security over generosity. Consider the following suggestions when making the changes:

<div class="sublist">

- `httpOnly`

This is set to `true` by default, which makes it so that *all* your cookies are inaccessible to JavaScript's 
`Document.cookie` API. If you set it to `false`, your cookies will be accessible via JavaScript using 
`document.cookie`.

For a server side web app there are not many cases where you need your cookies to be accessible 
via JavaScript so set this to `true`, unless you have a very good reason not to.

If you absolutely need a cookie to be accessible from JavaScript, remember that you could set `httpOnly` flag to 
`false` for a specific cookie while appending to a response. Prefer this over setting the global `httpOnly` to false
for better security.

- `lifetime`

The maximum age to be set for all cookies by default. This is set by default to 2 hours, which means after two hours,
the cookie will expire and will no longer be valid. A negative duration means that this cookie is not stored
persistently and will be deleted when the browser exits. Setting it to 0 means commanding the browser to forget/delete
this cookie.

Keep this value to a reasonably minimum time by balancing between not compromising the security and not making it 
annoying to users by requiring their credentials too often, for an example.
</div>

