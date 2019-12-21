package dev.alpas.alpasdev.controllers

import dev.alpas.alpasdev.actions.Documentation
import dev.alpas.extensions.toTitleCase
import dev.alpas.http.HttpCall
import dev.alpas.make
import dev.alpas.routing.Controller

class DocsController : Controller() {
    fun show(call: HttpCall) {
        val page = call.paramAsString("page") ?: "installation"
        val doc = call.make<Documentation>()
        val content = doc.get(page)
        val toc = doc.toc()
        val title = page.replace("-", " ").toTitleCase()
        call.render("docs", mapOf("content" to content, "title" to title, "toc" to toc))
    }

    fun index(call: HttpCall) {
        call.redirect().toRouteNamed("docs.show", mapOf("page" to "installation"))
    }
}
