package dev.alpas.alpasdev.actions

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import dev.alpas.Environment
import dev.alpas.ResourceLoader
import dev.alpas.orAbort
import redis.clients.jedis.JedisPool
import java.nio.file.Paths

class Documentation(
    private val env: Environment,
    private val resourceLoader: ResourceLoader,
    private val jedisPool: JedisPool
) {

    private fun docsPath(page: String) = Paths.get("docs", page)

    private val markdown by lazy { Markdown() }

    fun get(page: String): String {

        return if (env.isDev) {
            convert(page)
        } else {
            jedisPool.resource.use { jedis ->
                return jedis.hget("docs", page) ?: convert(page).also { jedis.hset("docs", page, it) }
            }
        }
    }

    private fun convert(page: String) = markdown.convert(readSource(page))

    fun toc(): String {
        return get("toc")
    }

    private fun readSource(page: String): String {
        return resourceLoader.load(docsPath("$page.md").toString())
            ?.readText()
            .orAbort("Page $page not found!")
    }
}

class Markdown {
    private val options by lazy {
        MutableDataSet().apply {
            set(Parser.EXTENSIONS, listOf(AutolinkExtension.create(), TablesExtension.create()))
            set(HtmlRenderer.HARD_BREAK, "<br />\n")
            set(HtmlRenderer.RENDER_HEADER_ID, true)
            set(TablesExtension.CLASS_NAME, "pure-table pure-table-striped")
        }
    }
    private val parser: Parser by lazy { Parser.builder(options).build() }
    private val renderer: HtmlRenderer by lazy { HtmlRenderer.builder(options).build() }

    internal fun convert(markdown: String?): String {
        val document = parser.parse(markdown.orEmpty())
        return renderer.render(document)
    }
}
