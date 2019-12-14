package dev.alpas.alpasdev.providers

import dev.alpas.view.ConditionalTagsRegistrar
import dev.alpas.view.ViewServiceProvider

class ViewServiceProvider() : ViewServiceProvider() {
    override fun registerConditionalTags(registrar: ConditionalTagsRegistrar) {
        registrar.register("prod") { it.env.isProduction }
    }
}
