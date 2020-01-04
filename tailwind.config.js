module.exports = {
    plugins: [
        require('@tailwindcss/custom-forms'),
    ],
    theme: {
        extend: {
            colors: {
                blue: {
                    '100': '#cad6f5',
                    '200': '#aabdef',
                    '300': '#8aa5e9',
                    '400': '#4a74dd',
                    '500': '#2A5BD7',
                    '600': '#244db7',
                    '700': '#1d4097',
                    '800': '#173276',
                    '900': '#112456'
                },
            },
            screens: {
                'xxl': '1921px',
            },
            lineHeight: {
                chill: 1.8
            }
        }
    },
    variants: {
        backgroundColor: ['responsive', 'hover', 'focus', 'odd', 'even'],
    }
}
