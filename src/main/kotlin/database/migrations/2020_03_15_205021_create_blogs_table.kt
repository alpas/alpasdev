package dev.alpas.alpasdev.database.migrations

import dev.alpas.alpasdev.entities.Blogs
import dev.alpas.ozone.migration.Migration

class CreateBlogsTable : Migration() {
    override var name = "2020_03_15_205021_create_blogs_table"

    override fun up() {
        createTable(Blogs)
    }

    override fun down() {
        dropTable(Blogs)
    }
}