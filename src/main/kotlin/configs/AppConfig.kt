package dev.alpas.alpasdev.configs

import dev.alpas.AppConfig
import dev.alpas.Environment
import java.nio.file.Paths

@Suppress("unused")
class AppConfig(env: Environment) : AppConfig(env) {
    override val staticDirs by lazy {
        if (env.isDev) {
            // for this you need to create a symlink from storage/src/main/resources/web to src/main/resources/web
            // mkdir -p $PWD/storage/src/main/resources && ln -s $PWD/src/main/resources/web $PWD/storage/src/main/resources/web
            arrayOf(Paths.get(env.storagePath, "src", "main", "resources", "web").toAbsolutePath().toString()) +
                    super.staticDirs
        } else {
            super.staticDirs
        }
    }
}

