package dev.alpas.alpasdev.providers

import dev.alpas.alpasdev.addRoutes
import dev.alpas.routing.RouteServiceProvider
import dev.alpas.routing.Router

class RouteServiceProvider : RouteServiceProvider() {
    override fun loadRoutes(router: Router) {
        router.addRoutes()
    }
}
