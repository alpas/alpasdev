package dev.alpas.alpasdev

import dev.alpas.Alpas
import dev.alpas.routing.BaseRouteLoader
import dev.alpas.routing.Router

fun main(args: Array<String>) = Alpas(args).routes(RouteLoader()).ignite()

class RouteLoader : BaseRouteLoader() {
    override fun add(router: Router) {
        router.addRoutes()
    }
}
