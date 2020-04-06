Cross Site Request Forgery (CSRF), is a common attack that works by executing command on behalf of currently 
authenticated user but by an unauthenticated user. Alpas comes with out-of-the box support for protecting
you against the CSRF attacks. If an incoming request is one of `POST`, `PUT`, `PATCH`, or `DELETE` methods, then
Alpas makes sure that these requests are made by the currently authenticated user. If not, it
raises a `TokenMismatchException`.

### Enabling CSRF Protection

CSRF protection is enabled by default for `POST`, `PUT`, `PATCH`, and `DELETE` requests. The only thing
you have to do on your part is to include a `csrf` token with any kind of above mentioned requests. This is
preferably done by including a hidden input field within your HTTP form.

Alpas includes a `csrf` tag that you can use in your Pebble Templates to quickly populate a hidden input field
with a token that is valid for the current request. If you need just the raw token and not the full input
field, you can use `_csrf` variable instead.

```twig

<form action="/docs" method="post">
    {% csrf %}
    <input type="hidden" name="_method" value="delete"/>
    <button type="submit">Delete</button>
</form>

<h3>The current token is: {{ _csrf }}</h3>

```

Once a token is included in a request, Alpas validates it in the `VerifyCsrfToken` middleware and throws an 
exception if it doesn't match with the token that is saved in the current session.

>/alert/<span>The `VerifyCsrfToken` middleware must be applied to your route(s) to validate the incoming CSRF token.
>Otherwise, the validation will be skipped.</span>

>/tip/<span>Since `VerifyCsrfToken` middleware is included with the `web`
>[middleware group](/docs/middleware#named-middleware-group), you can instead just apply
>`web` middleware group to your routes to have `VerifyCsrfToken` middleware added to your request pipeline.</span>


### CSRF Validation with JavaScript

CSRF validation could get tricky if you are making requests from your JavaScript code. To ease the pain
of figuring out everything, Alpas includes an encrypted token `XSRF-TOKEN` in the cookie.

When making a request that needs to be validated against CSRF attacks, you can include the same token in the header
and pass it back as either `X-CSRF-TOKEN` or `X-XSRF-TOKEN`. The only difference between these two tokens is that
the value of `X-CSRF-TOKEN` is considered to be unencrypted while the value of `X-XSRF-TOKEN` is considered
to be encrypted and hence it will be decrypted before validating the accuracy of the token.

In the following example, we pass `X-CSRF-TOKEN` in the header of an Ajax call inside a Pebble Template. 

```javascript

 $.ajax({
     url:"{{ route('showmore.load') }}",
     method:"POST",
     headers: {
      'X-CSRF-TOKEN': '{{ _csrf }}'
      },
     data:{id:id},
     success:function(data)
     {
        $('#button').remove();
        $('#list').append(data);
     }
  });

```

It may look like a lot of work to make CSRF protection work when making an AJAX call from JavaScript, but
in practice, a library like Axios should automatically notice that an encrypted `XSRF-TOKEN` cookie is
included and should read it and send back as it is in the request the header. It can read the value,
of course, because of the [same-origin policy](https://en.wikipedia.org/wiki/Same-origin_policy).
