package dev.alpas.alpasdev.actions

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import dev.alpas.Environment
import dev.alpas.ResourceLoader
import dev.alpas.ozone.orAbort
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
        fun make(): String {
            return markdown.convert(readSource(page))
        }

        if (env.isDev) {
            return make()
        }

        return jedisPool.resource.use { jedis ->
            return jedis.hget("docs", page) ?: make().also { jedis.hset("docs", page, it) }
        }
    }

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
            set(Parser.EXTENSIONS, listOf(AutolinkExtension.create()))
            set(HtmlRenderer.HARD_BREAK, "<br />\n")
            set(HtmlRenderer.RENDER_HEADER_ID, true)
        }
    }
    private val parser: Parser by lazy { Parser.builder(options).build() }
    private val renderer: HtmlRenderer by lazy { HtmlRenderer.builder(options).build() }

    internal fun convert(markdown: String?): String {
        val document = parser.parse(markdown.orEmpty())
        return renderer.render(document)
    }
}
