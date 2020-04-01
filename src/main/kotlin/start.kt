package dev.alpas.alpasdev

import dev.alpas.Alpas
import dev.alpas.Environment
import dev.alpas.routing.BaseRouteLoader
import dev.alpas.routing.Router

fun main(args: Array<String>): Unit = Alpas(args) {
    routes(RouteLoader(this.env))
}.ignite()

class RouteLoader(private val env: Environment) : BaseRouteLoader() {
    override fun add(router: Router) {
        router.addRoutes(env)
    }
}
