package dev.alpas.alpasdev.configs

import dev.alpas.Environment
import dev.alpas.ozone.DatabaseConfig

@Suppress("unused")
class DatabaseConfig(env: Environment) : DatabaseConfig(env) {
    init {
//        addConnection(
//            "mysql",
//            lazy { MySqlConnection(env, dev.alpas.ozone.ConnectionConfig(sqlDialect = MySqlDialect())) })
//        addConnection(
//            "sqlite",
//            lazy { SqliteConnection(env, dev.alpas.ozone.ConnectionConfig(sqlDialect = SQLiteDialect())) })
    }
}

