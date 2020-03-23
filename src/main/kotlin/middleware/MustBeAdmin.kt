package dev.alpas.alpasdev.middleware

import dev.alpas.Handler
import dev.alpas.Middleware
import dev.alpas.exceptions.NotFoundHttpException
import dev.alpas.http.HttpCall
import dev.alpas.routing.Route
import dev.alpas.routing.RouteGroup
import dev.alpas.alpasdev.entities.User

class MustBeAdmin : Middleware<HttpCall>() {
    override fun invoke(call: HttpCall, forward: Handler<HttpCall>) {
        if (call.isAuthenticated && call.caller<User>().isAdmin()) {
            forward(call)
        } else {
            throw NotFoundHttpException()
        }
    }
}

fun RouteGroup.mustBeAdmin() {
    middleware(MustBeAdmin::class)
}

fun Route.mustBeAdmin() {
    middleware(MustBeAdmin::class)
}
