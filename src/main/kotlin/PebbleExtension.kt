package dev.alpas.alpasdev

import dev.alpas.Application
import dev.alpas.view.ConditionalTags
import dev.alpas.view.extensions.PebbleExtension

@Suppress("unused")
class PebbleExtension : PebbleExtension {
    override fun register(app: Application, conditionalTags: ConditionalTags) {
        conditionalTags.add("prod") { call.env.isProduction }
    }
}
