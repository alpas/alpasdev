package dev.alpas.alpasdev.database.seeds

import dev.alpas.Application
import dev.alpas.alpasdev.database.factories.BlogFactory
import dev.alpas.ozone.Seeder
import dev.alpas.ozone.from

internal class DatabaseSeeder : Seeder() {
    override fun run(app: Application) {
        from(BlogFactory, 50)
    }
}
