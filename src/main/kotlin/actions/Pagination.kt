package dev.alpas.alpasdev.actions

import me.liuwj.ktorm.dsl.count
import kotlin.math.ceil
import dev.alpas.alpasdev.entities.Blogs

class Pagination(val previousUrl: String?, val nextUrl: String?) {
    fun hasPrevious(): Boolean {
        return previousUrl != null
    }

    fun hasNext(): Boolean {
        return nextUrl != null
    }
}

fun pagination(currentPage: Int): Pagination {
    val numPages = ceil(Blogs.count().toDouble() / 10)
    var previousUrl = if (currentPage <= 1) null else (currentPage - 1).toString()
    var nextUrl = if (currentPage.toDouble() == numPages) null else (currentPage + 1).toString()

    return Pagination(previousUrl, nextUrl)
}
