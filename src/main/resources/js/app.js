window.jQuery = window.$ = require('jquery')

jQuery(function ($) {
    $(document).keydown(function (e) {
        const _focused = $(document.activeElement);
        const _inputting = _focused.get(0).tagName.toLowerCase() === "textarea" || _focused.get(0).tagName.toLowerCase() === "input";

        // / (forward slash) key = search
        if (!_inputting && e.keyCode === 191) {
            e.preventDefault();
            $("#algolia").focus();
        }
    });
})
