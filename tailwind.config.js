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
                gray: {
                    '100': '#FBFBFE',
                    '200': '#f3f3f6',
                    '300': '#ececef',
                    '400': '#c6c6c9',
                    '500': '#a1a1a3',
                    '600': '#7b7b7c',
                    '700': '#555556',
                    '800': '#303030',
                    '900': '#212121'
                }
            }
        }
    },
    variants: {
        backgroundColor: ['responsive', 'hover', 'focus', 'odd', 'even'],
    }
}
