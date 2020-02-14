let mix = require('laravel-mix')
const tailwindcss = require('tailwindcss')
const purgecss = require('@fullhuman/postcss-purgecss')
const publicPath = 'src/main/resources/web'
const resources = 'src/main/resources'
const docsPath = `${resources}/docs`
const templatesPath = `${resources}/templates`

mix
    .setPublicPath(publicPath)
    .js([`${resources}/js/app.js`], 'js')
    .js([`${resources}/js/docs.js`, `${resources}/js/sidebar.js`], 'js')
    .less(`${resources}/css/docs.less`, 'css/docs.css')
    .less(`${resources}/css/app.less`, 'css/app.css')
    .options({
        postCss: [
            tailwindcss('./tailwind.config.js'),
            ...mix.inProduction() ? [
                purgecss({
                    content: [`${templatesPath}/**/*.peb`, `${resources}/js/*.js`],
                    defaultExtractor: content => content.match(/[\w-/:.]+(?<!:)/g) || [],
                    whitelist: ['clipboard'],
                }),
            ] : [],
        ],
    })
    .version()
    .disableNotifications()

if (!mix.inProduction()) {
    mix.browserSync({
        open: false,
        notify: false,
        proxy: `localhost:${process.env.MIX_APP_PORT}`,
        files: [
            'out/**/*',
            publicPath,
            docsPath
        ],
    })
}
