package dev.alpas.alpasdev.providers

import dev.alpas.alpasdev.addRoutes
import dev.alpas.routing.RouteServiceProvider
import dev.alpas.routing.Router
import dev.alpas.Environment

class RouteServiceProvider(private val env: Environment) : RouteServiceProvider() {
    override fun loadRoutes(router: Router) {
        router.addRoutes(env)
    }
}
