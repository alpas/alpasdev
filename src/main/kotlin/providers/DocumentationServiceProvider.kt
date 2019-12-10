package dev.alpas.alpasdev.providers

import dev.alpas.Application
import dev.alpas.ServiceProvider
import dev.alpas.alpasdev.actions.Documentation
import dev.alpas.make

class DocumentationServiceProvider : ServiceProvider {
    override fun boot(app: Application) {
        app.bind(Documentation(app.make(), app.make(), app.make()))
    }
}
