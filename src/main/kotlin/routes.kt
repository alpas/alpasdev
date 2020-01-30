package dev.alpas.alpasdev

import dev.alpas.alpasdev.controllers.DocsController
import dev.alpas.alpasdev.controllers.HomeController
import dev.alpas.alpasdev.controllers.NewsletterController
import dev.alpas.alpasdev.controllers.ThanksController
import dev.alpas.routing.Router

fun Router.addRoutes() = apply {
    webRoutes()
}

private fun Router.webRoutes() {
    get("/", HomeController::class).name("welcome")
    get("/docs/<page>", DocsController::class, "show").name("docs.show")
    get("/docs", DocsController::class, "index").name("docs.index")
    get("/newsletter", NewsletterController::class, "index").name("newsletter")
    get("/thankyou", ThanksController::class, "index").name("thankyou")
}
