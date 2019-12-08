package dev.alpas.alpasdev.configs

import dev.alpas.Environment
import dev.alpas.view.ViewConfig

@Suppress("unused")
class ViewConfig(env: Environment) : ViewConfig(env) {
    override val templateExtension = "peb"
}
