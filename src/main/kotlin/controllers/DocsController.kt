package dev.alpas.alpasdev.controllers

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import dev.alpas.ResourceLoader
import dev.alpas.http.HttpCall
import dev.alpas.lodestar.orAbort
import dev.alpas.make
import dev.alpas.routing.Controller
import java.io.File
import java.nio.file.Paths

class DocsController : Controller() {
    fun show(call: HttpCall) {
        val page = call.paramAsString("page") ?: "installation"
        val doc = Documentation(call.make())
        val content = doc.get(page)
        val toc = doc.toc()
        val title = page.replace("-", " ")
        call.render("docs", mapOf("content" to content, "title" to title, "toc" to toc))
    }

    fun index(call: HttpCall) {
        call.redirect().toRouteNamed("docs.show", mapOf("page" to "installation"))
    }
}

class Documentation(private val resourceLoader: ResourceLoader) {
    private fun docsPath(page: String) = Paths.get("docs", page)

    fun get(page: String): String {
        val path = resourceLoader.load(docsPath("$page.md").toString())?.toURI().orAbort("Page $page not found!")
        val markdown = File(path).readText()
        return Markdown.render(markdown)
    }

    fun toc(): String {
        val path = resourceLoader.load(docsPath("toc.md").toString())?.toURI().orAbort()
        val markdown = File(path).readText()
        return Markdown.render(markdown)
    }
}

class Markdown {
    private val options by lazy {
        MutableDataSet().apply {
            set(Parser.EXTENSIONS, listOf(AutolinkExtension.create()))
            set(HtmlRenderer.HARD_BREAK, "<br />\n")
        }
    }
    private val parser: Parser by lazy { Parser.builder(options).build() }
    private val renderer: HtmlRenderer by lazy { HtmlRenderer.builder(options).build() }

    companion object {
        fun render(markdown: String?): String {
            return Markdown().convert(markdown)
        }
    }

    private fun convert(markdown: String?): String {
        val document = parser.parse(markdown.orEmpty())
        return renderer.render(document)
    }
}
