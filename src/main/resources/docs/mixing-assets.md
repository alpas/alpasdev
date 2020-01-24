- [Configuration](#configuration) 
- [Watching Changes and Auto Reloading](#auto-reloading)
    - [Frontend Asset Changes](#auto-reload-frontend)
    - [Code Changes](#auto-reload-code)
- [Cache Busting with Asset Fingerprinting](#cache-busting-with-asset-fingerprinting)
- [Setting Environment Variables](#setting-environment-variables)

If you have any experience with frontend development and especially with bundling your assets — vanilla JS, stylesheets,
images, frontend libraries such as VueJS, etc. — you know that mixing them is not a trivial task and needs a lot of
wiring and juggling. Webpack is currently the de-facto standard for bundling assets. However, it is complex
and has a huge learning curve. 

Thankfully, there exists a JS library, called [Laravel Mix](https://laravel-mix.com/), that wraps the
complex Webpack APIs in a much simpler fluent APIs. To make it easier for you to get going with
your web app, Alpas comes set up with asset handling through Mix.

>/info/ <span> Alpas uses Mix because it is simple to configure, it is fast, and it just works! However, Alpas
>is not really tied to Mix. Feel free to use any bundler of your choice such as [Grunt](https://gruntjs.com/),
>[Gulp](https://gulpjs.com/), [Rollup](https://rollupjs.org/guide/en/), etc.

<a name="configuration"></a>
### [Configuration](#configuration)

When a new project is scaffolded, Alpas creates a `webpack.mix.js` file for you in the root of your project.
This file is where you would configure your assets bundling pipeline. Alpas also creates a `package.json`
file for you that lists some very basic npm packages to get you going. Feel free to add and remove
dependencies as per your project requirements.

To install the packages listed in the `package.json` file, make sure you have the latest version of 
[nodejs](https://nodejs.org/en/) and [npm](https://www.npmjs.com/) installed and then simply run
`npm install` from your terminal.

>/tip/ <span>Instead of `npm` we highly recommend using [yarn](https://yarnpkg.com), which is an
>ultra fast alternative to `npm` for managing nodejs packages.</span>

When you open the `webpack.mix.js` file you will notice that `publicPath` is set to `src/main/resources/web`.
This location is where your final bundled assets will be put. On the other hand, `resources` is set to
`src/main/resources` folder, which is where your source files will be loaded from. Feel free to
change these values as you wish.

After you finish configuring your `webpack.mix.js` file, you can run one of the following commands to bundle
your assets. All these commands come from the `package.json` file.

<div class="sublist">

- `npm run dev`

Bundles all the assets without minifying the output. Use this in [dev mode](/docs/configuration#dev).
Since the symbols are not mingled, it is much easier to debug your scripts and your styles.

- `npm run prod`

Bundles all the assets and minifies the output. Use this before deploying or creating a fat jar for
deployment. Since the generated files are minified, the files are usually much smaller than the
original source files but aren't easy to debug.

- `npm run watch`

Watches the asset files and automatically re-compiles them whenever a change is detected.
This comes in very handy during development, especially when you are tweaking your assets.

>/alert/ <span> If you are serving your app, the recompilation of assets are not automatically
>reflected in your browser. You have to recompile and reserve your app again.</span>
</div>

<a name="auto-reloading"></a>
### [Watching Changes and Auto Reloading](#auto-reloading)

<a name="auto-reload-frontend"></a>
#### [Frontend Asset Changes](#auto-reload-frontend)

When you are working with tweaking the design of your app or making the interactive aspect of the app just right, you
may want your assets to recompile and the browser to reload automatically as soon as you make save yours changes.
Alpas comes everything wired to facilitate this rapid prototyping so that you can make interactions faster
without having to recompile everything and refreshing your browser for every small changes.

This works by linking `storage/src/main/web` folder to the actual `src/main/resources/web` folder.
This should be done automatically when you initialized the project. But in case it isn't,
you can use the `link:web` Alpas command to do this for you.

```bash
$ alpas link:web
```

Once the link has been created, you need to follow two steps:

<div class="ordered-list">

1. Run and serve the app, preferably from IntelliJ, if not, using the `alpas serve` command.
2. Open the terminal and from the root of the project run the `yarn watch` command.

</div>

<br/>

Your "auto-syncing" app should now be available at a new port, which is by default port
**3000** i.e. the app is accessible at http://localhost:3000

To try it out, make some changes in one of your frontend assets like *app.less*, *app.js*, *welcome.vue*,
etc. and you will notice that after few seconds the browser will auto-refresh with the new changes.

<a name="auto-reload-code"></a>
#### [Code Changes](#auto-reload-code)

Unfortunately, Kotlin being a compiled language, your code changes won't be loaded immediately and automatically.
You may have to recompile and re-serve your app for the changes to load, especially for big changes.

However, most of the times, if you are running your app from IntelliJ IDEA, you can just reload your changed classes
using `Run > Reload Changed Classes` menu. If you have `yarn watch` running in the background, then it can detect
the code reload changes and automatically refresh the browser for you as well. Our recommendation is to set a
shortcut for `Run > Reload Changed Classes` and trigger it using the shortcut. We have set this to `CMD+R`.

<a name="cache-busting-with-asset-fingerprinting"></a>
### [Cache Busting with Asset Fingerprinting](#cache-busting-with-asset-fingerprinting)

Browsers usually cache assets for performance reasons. This is all good until you change the assets. Browsers might
not know about the new changes and continue to serve the old cached assets. To force browsers to load a
fresh copy of your assets, you can append a version number to each of your asset URLs for every new version,
which is like fingerprinting your assets. This looks something like `app.css?ver=1.0`.

Doing this manually is cumbersome and error prone. Luckily, Mix will append a random hash to the
filenames of all your compiled asset when you use `version()` method.

<span class="line-numbers" data-start="1" data-file="webpack.mix.js">

```javascript

let mix = require('laravel-mix')
const publicPath = 'src/main/resources/web'
const resources = 'src/main/resources'

mix.setPublicPath(publicPath)
   .js([`${resources}/js/app.js`, `${resources}/js/main.js`], 'js/app.js')
   .less(`${resources}/css/app.less`, 'css/app.css')

// apply versioning only in production
if(mix.inProduction()) {
   mix.version()
}

```

</span>

Applying versioning means, to simplify, renaming your assets to some random strings. Which means
you need a consistent way to refer to these assets from your templates so that you don't have
to manually change the referenced filenames every time. For this you need to use Alpas's
`mix()` function. So instead of referring assets like this:

```html

<script src="/js/app.js"></script>
<link rel="stylesheet" href="/css/app.css">

```

you need refer them like so:

```html

<script src="{{ mix('js/app.js') }}"></script>
<link rel="stylesheet" href="{{ mix('css/app.css') }}">

```

<a name="setting-environment-variables"></a>
### [Setting Environment Variables](#setting-environment-variables)

Mix will automatically read any environment variables that start with a `MIX_` 
prefix in your `.env` file and make it available on `process.env` object.

Say you have a `MIX_APP_PORT=8060` variable defined in your .env file, this will be
later available as `process.env.MIX_APP_PORT` in your JS files.
