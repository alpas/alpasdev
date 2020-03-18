package dev.alpas.alpasdev

import dev.alpas.alpasdev.controllers.*
import dev.alpas.auth.authRoutes
import dev.alpas.routing.Router

fun Router.addRoutes() = apply {
    webRoutes()
    authRoutes(requireEmailVerification = false)
}

private fun Router.webRoutes() {
    get<HomeController>().name("welcome")
    get("docs/<page>", DocsController::show).name("docs.show")
    get<DocsController>("docs").name("docs.index")
    get<NewsletterController>("newsletter").name("newsletter")
    get<ThanksController>("thankyou").name("thankyou")
    get("/buzz", BlogController::blog).name("blog")
    get("/buzz/<num>", BlogController::pages).name("pages")
    delete("/buzz", BlogController::class).name("delete")
    get("/buzz/edit/<id>", BlogController::edit).name("edit")
    patch("buzz/edit/<id>", BlogController::update).name("update")
    get("/buzz/freshhoney", BlogController::add).name("add")
    post("/buzz", BlogController::submit).name("submit")

}
