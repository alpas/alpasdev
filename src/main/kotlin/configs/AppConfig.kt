package dev.alpas.alpasdev.configs

import dev.alpas.AppConfig
import dev.alpas.Environment
import java.nio.file.Paths

class AppConfig(env: Environment) : AppConfig(env) {
    override val staticDirs by lazy {
        arrayOf(
            Paths.get(env.storagePath, "src", "main", "resources", "web").toAbsolutePath().toString()
        ) + super.staticDirs
    }
}
