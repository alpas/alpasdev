package dev.alpas.alpasdev.database.factories

import dev.alpas.alpasdev.entities.Blog
import dev.alpas.alpasdev.entities.Blogs
import dev.alpas.ozone.EntityFactory
import dev.alpas.ozone.faker
import java.util.concurrent.TimeUnit

internal object BlogFactory : EntityFactory<Blog, Blogs>() {
    override val table = Blogs

    override fun entity(): Blog {
        return Blog {
            title = faker.lorem().sentence()
            body = faker.lorem().paragraph(faker.random().nextInt(2, 5))
            url = faker.internet().url()
            createdAt = faker.date().past(1, TimeUnit.HOURS).toInstant()
        }
    }
}
