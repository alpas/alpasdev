package dev.alpas.alpasdev.controllers

import dev.alpas.alpasdev.actions.Documentation
import dev.alpas.extensions.toTitleCase
import dev.alpas.http.HttpCall
import dev.alpas.make
import dev.alpas.routing.Controller

class DocsController : Controller() {
    fun show(call: HttpCall) {
        val page = call.stringParam("page") ?: "installation"
        val pagination = pagination(page)
        val doc = call.make<Documentation>()
        val content = doc.get(page)
        val title = page.replace("-", " ").toTitleCase()
        call.render("docs", mapOf("content" to content, "title" to title, "page" to page, "pagination" to pagination))
    }

    fun index(call: HttpCall) {
        call.redirect().toRouteNamed("docs.show", mapOf("page" to "installation"))
    }
}

private fun pagination(currentPage: String): Pagination {
    val index = pages.indexOf(currentPage)
    val previousUrl = if (index < 1) null else pages[index - 1]
    val nextUrl = if (index >= pages.size - 1) null else pages[index + 1]
    return Pagination(previousUrl, nextUrl)
}

data class Pagination(val previousUrl: String?, val nextUrl: String?) {
    fun hasPrevious(): Boolean {
        return previousUrl != null
    }

    fun hasNext(): Boolean {
        return nextUrl != null
    }
}


private val pages = listOf(
    "installation",
    "quick-start-guide-todo-list",
    "configuration",
    "project-structure",
    "request-response",
    "ioc-container",
    "service-providers",
    "routing",
    "middleware",
    "controllers",
    "http-request",
    "http-response",
    "sessions",
    "validation",
    "error-handling",
    "logging",
    "pebble-templates",
    "mixing-assets",
    "security",
    "authentication",
    "csrf-protection",
    "authentication-scaffolding",
    "password-reset",
    "email-verification",
    "database-getting-started",
    "ozone",
    "entity-relationship",
    "migrations",
    "seeding",
    "entity-factory",
    "redis",
    "alpas-console",
    "mail",
    "notifications",
    "queues",
    "deployment",
    "deploying-to-heroku",
    "troubleshooting"
)

