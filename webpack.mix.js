let mix = require('laravel-mix')
const tailwindcss = require('tailwindcss')
const publicPath = 'src/main/resources/web'
const assetsPath = 'src/main/assets'

mix
    .setPublicPath(publicPath)
    .js(`${assetsPath}/js/app.js`, 'js')
    .less(`${assetsPath}/css/app.less`, 'css/app.css')
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
