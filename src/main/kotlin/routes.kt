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
    get<HomeController>().name("welcome")
    get("docs/<page>", DocsController::show).name("docs.show")
    get<DocsController>("docs").name("docs.index")
    get<NewsletterController>("newsletter").name("newsletter")
    get<ThanksController>("thankyou").name("thankyou")
}
