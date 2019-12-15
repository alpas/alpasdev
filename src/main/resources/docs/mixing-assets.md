- [Configuration](#configuration) 
- [Cache Busting with Asset Fingerprinting](#cache-busting-with-asset-fingerprinting)
- [Setting Environment Variables](#setting-environment-variables)

If you have got any experience with frontend and esp. with bundling your assets such as vanilla JS, stylesheets,
images, frontend libraries such as VueJS etc., you know that mixing them is not a trivial task and needs a lot of
wiring and juggling. Webpack is currently the de facto standard for bundling assets. However, it is complex and has 
a huge learning curve. 

Luckily, there exists a JS library, called [Laravel Mix](https://laravel-mix.com/), that wraps the complex Webpack 
APIs in a much simpler fluent APIs. To make it easier for you to get going with your web app, Alpas comes set up with 
asset handling through Mix.

> /info/ <span> Alpas uses Mix because it's simple to configure, it's fast, and it just works! However,
> Alpas is not really tied to Mix. Feel free to use any bundler of your choice such as 
> [Grunt](https://gruntjs.com/), [Gulp](https://gulpjs.com/), [Rollup](https://rollupjs.org/guide/en/) etc.

<a name="configuration"></a>
### [Configuration](#configuration)

When a new project is scaffolded, Alpas creates a `webpack.mix.js` file for you in the root of your project. This file
is where you'd configure your assets bundling pipeline. Alpas also creates a `package.json` file for you that lists 
some very basic npm packages to get you going. Feel free to add and remove dependencies as per your project
requirement.

To install the packages listed in the `package.json` file make sure you have the latest version of 
[nodejs](https://nodejs.org/en/) and [npm](https://www.npmjs.com/) installed. If so, simply run `npm install` from your
terminal.

> /tip/ <span>Instead of `npm` we highly recommend using [yarn](https://yarnpkg.com), which is an ultra fast 
> alternative to `npm` for managing nodejs packages.</span>

If you open the `webpack.mix.js` file you will notice that `publicPath` is set to `src/main/resources/web`. This 
location is where your final bundled assets will be put. On the other hand, `resources` is set to `src/main/resources`
folder, which is where your source files will be loaded from. Feel free to change these values as you wish.

After finish configuring your `webpack.mix.js` file, you can run one of the following commands to bundle your assets.
All these commands come from the `package.json` file.

<div class="sublist">

- `npm run dev`

Bundles all the assets without minifying the output. Use this in development mode. Since the symbols are not mingled,
it is easier to debug your scripts and your styles.

- `npm run prod`

Bundles all the assets and minifies the output. Use this before deploying or creating a fat jar for deployment. Since
the generated files are minified, the files are usually much smaller than the original source files but aren't easy
to debug.

- `npm run watch`

Watches the asset files and automatically recompiles them whenever a change is detected. This comes in very handy
during development and esp. if you are tweaking your assets.

> /alert/ <span> If you are serving your app, the recompilation of assets are not automatically reflected in your
> browser. You have to recompile and reserve your app again.</span>

> /tip/ <span> Recompiling and re-serving your app after each small asset tweaking gets tiring and annoying very soon.
> If you are running your app from within IntelliJ IDEA then a much better approach is to just reloading your 
> changed classes from `Run > Reload Changed Classes` menu. Once your assets are done compiling give it a couple of 
> seconds before reloading the classes. </span>

</div>

<br/>

<a name="cache-busting-with-asset-fingerprinting"></a>
### [Cache Busting with Asset Fingerprinting](#cache-busting-with-asset-fingerprinting)

Browsers usually cache assets for performance reasons. This is all good until you change the assets. Browsers will not
know about the new changes and might continue to serve the old cached assets. To force browsers to load a fresh copy
of your assets, you could append a version number to each of your asset URLs for every new version, which is like
fingerprinting your assets. This could look something like `app.css?ver=1.0`.

Doing this manually is bother cumbersome and error prone. Luckily, Mix will append a random hash to the
filenames of all your compiled asset when you use `version()` method.

<span class="line-numbers" data-start="1">

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

<br/>

Applying versioning almost means, to simplify, renaming your assets to some random strings. Which means you need a
consistent way to refer to these assets from your templates so that you don't have to manually change the referenced
filenames every time. For this you need to use Alpas's `mix()` function. So instead of referring assets like this:

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

Mix will automatically read any environment variables that start with a `MIX_` prefix in your `.env` file and make it
available on `process.env` object.

Say you have a `MIX_APP_PORT=8060` variable defined in your .env file, this will be later available as 
`process.env.MIX_APP_PORT=8060`.

