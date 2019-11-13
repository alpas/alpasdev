package dev.alpas.alpasdev.providers

import dev.alpas.Application
import dev.alpas.alpasdev.addRoutes
import dev.alpas.make
import dev.alpas.routing.RouteServiceProvider
import dev.alpas.routing.Router

class RouteServiceProvider : RouteServiceProvider() {
    override fun register(app: Application) {
        super.register(app)
        app.make<Router>().addRoutes()
    }
}
