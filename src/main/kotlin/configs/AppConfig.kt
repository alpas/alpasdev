package dev.alpas.alpasdev.configs

import dev.alpas.Config
import dev.alpas.AppConfig as BaseConfig
import dev.alpas.Environment
import dev.alpas.ozone.ConnectionConfig
import dev.alpas.ozone.DatabaseConnection
import dev.alpas.ozone.MySqlConnection
import me.liuwj.ktorm.database.Database

@Suppress("unused")
class AppConfig(env: Environment) : BaseConfig(env) {

   val adminEmails = env("ADMIN_EMAILS", "").split(",")
}


