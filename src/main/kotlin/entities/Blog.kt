package dev.alpas.alpasdev.entities

import dev.alpas.ozone.OzoneEntity
import dev.alpas.ozone.OzoneTable
import dev.alpas.ozone.bigIncrements
import dev.alpas.ozone.string
import me.liuwj.ktorm.schema.text
import me.liuwj.ktorm.schema.timestamp
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
    val body by text("body").nullable().bindTo { it.body }
    val createdAt by timestamp("created_at").nullable().bindTo { it.createdAt }
}
