package dev.alpas.alpasdev.configs

import dev.alpas.Environment
import dev.alpas.alpasdev.entities.Users
import dev.alpas.auth.AuthChannel
import dev.alpas.auth.Authenticatable
import dev.alpas.auth.SessionAuthChannel
import dev.alpas.auth.AuthConfig as BaseConfig

@Suppress("unused")
class AuthConfig(env: Environment) : BaseConfig(env) {
    init {
        addChannel("session") { call -> SessionAuthChannel(call, Users) }
    }
}
