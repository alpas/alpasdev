package dev.alpas.alpasdev

import dev.alpas.Environment
import dev.alpas.alpasdev.controllers.*
import dev.alpas.auth.authRoutes
import dev.alpas.routing.RouteGroup
import dev.alpas.routing.Router

fun Router.addRoutes(env: Environment) = apply {
    group {
        webRoutesGroup()
    }.middlewareGroup("web")

    authRoutes(allowRegistration = env("ALLOW_REGISTRATION", false), requireEmailVerification = false, allowPasswordReset = env("ALLOW_PASSWORD_RESET", false), addHomeRoute = false)
}


private fun RouteGroup.webRoutesGroup() {

    get("/home") {
        redirect().toRouteNamed("home")
    }

    get("/", HomeController::class).name("home")
    get("docs/<page>", DocsController::show).name("docs.show")
    get<DocsController>("docs").name("docs.index")
    get<NewsletterController>("newsletter").name("newsletter")
    get<ThanksController>("thankyou").name("thankyou")
    get("/buzz", BlogController::blog).name("blog")
    get("/buzz/<num>", BlogController::pages).name("pages")

    group("/") {
        addBuzzAdminRoutes()
    }.name("buzz").mustBeAuthenticated()
}

private fun RouteGroup.addBuzzAdminRoutes() {
    delete("/buzz", BlogController::class).name("delete")
    get("/buzz/edit/<id>", BlogController::edit).name("edit")
    patch("buzz/edit/<id>", BlogController::update).name("update")
    post("/buzz", BlogController::submit).name("submit")
}