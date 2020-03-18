package dev.alpas.alpasdev.entities

import dev.alpas.ozone.*
import java.time.Instant

interface Blog : OzoneEntity<Blog> {
    var id: Long
    var title: String?
    var url: String?
    var body: String?
    var createdAt: Instant?

    companion object : OzoneEntity.Of<Blog>()
}

object Blogs : OzoneTable<Blog>("blogs") {
    val id by bigIncrements()
    val title by string("title").size(200).nullable().bindTo { it.title }
    val url by string("url").size(150).nullable().bindTo { it.url }
    val body by string("body").size(150).nullable().bindTo { it.body }
    val createdAt by createdAt()
}