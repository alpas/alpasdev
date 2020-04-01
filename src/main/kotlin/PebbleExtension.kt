package dev.alpas.alpasdev

import dev.alpas.Application
import dev.alpas.view.ConditionalTags
import dev.alpas.view.extensions.PebbleExtension
import dev.alpas.alpasdev.entities.User
import dev.alpas.config
import dev.alpas.alpasdev.configs.AppConfig

@Suppress("unused")
class PebbleExtension : PebbleExtension {
    override fun register(app: Application, conditionalTags: ConditionalTags) {
        conditionalTags.add("prod") { call.env.isProduction }
        conditionalTags.add("admin") {
            call.isAuthenticated && call.caller<User>().isAdmin(call.config<AppConfig>().adminEmails)
        }
    }
}
