let mix = require('laravel-mix')
const tailwindcss = require('tailwindcss')
const publicPath = 'src/main/resources/web'
const resources = 'src/main/resources'

mix
    .setPublicPath(publicPath)
    .js([`${resources}/js/app.js`, `${resources}/js/sidebar.js`], 'js')
    .less(`${resources}/css/docs.less`, 'css/docs.css')
    .less(`${resources}/css/app.less`, 'css/app.css')
    .options({
        postCss: [
            tailwindcss('./tailwind.config.js'),
        ]
    })
    .version()
    .disableNotifications()

if (!mix.inProduction()) {
    mix.browserSync({
        open: false,
        files: [
            'build/**/*',
        ],
    })
}
