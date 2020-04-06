package dev.alpas.alpasdev.controllers

import dev.alpas.http.HttpCall
import dev.alpas.routing.Controller
import dev.alpas.alpasdev.entities.Blogs
import dev.alpas.ozone.latest
import dev.alpas.alpasdev.actions.pagination
import dev.alpas.alpasdev.guards.BlogGuard
import dev.alpas.orAbort
import dev.alpas.ozone.create
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update
import me.liuwj.ktorm.entity.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class BlogController : Controller() {
    fun blog(call: HttpCall) {
        val blogs = Blogs.latest().sortedByDescending { it.createdAt }.drop(0).take(10).toList()
        val pagination = pagination(1)

        call.render("blog", mapOf("blogs" to blogs, "pagination" to pagination))
    }

    fun pages(call: HttpCall) {
        val currentPage = call.stringParam("num")!!.toInt()
        val pagination = pagination(currentPage)
        val blogs = Blogs.latest().sortedByDescending { it.createdAt }.drop(10 * (currentPage - 1)).take(10).toList()

        call.render("blog", mapOf("blogs" to blogs, "pagination" to pagination))
    }

    fun submit(call: HttpCall) {

        call.validateUsing(BlogGuard::class)

        Blogs.create() {
            it.title to call.stringParam("title")
            it.url to call.stringParam("url")
            it.body to call.stringParam("body")
            it.createdAt to Instant.now()

            flash("success", "Successfully added some sweet honey 🍯")

            call.redirect().back()
        }
    }

    fun edit(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        var blog = Blogs.latest { it.id eq id }.toList()

        call.render("editbuzz", mapOf("blog" to blog[0]))
    }

    fun update(call: HttpCall) {

        call.validateUsing(BlogGuard::class)

        val id = call.longParam("id").orAbort()

        Blogs.update {
            it.title to call.stringParam("title")
            it.url to call.stringParam("url")
            it.body to call.stringParam("body")

            where{ it.id eq id }

            flash("success", "Successfully Saved")

            call.redirect().toRouteNamed("blog")
        }
    }

    fun delete(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        Blogs.delete { it.id eq id }

        call.redirect().toRouteNamed("blog")

        flash("success", "Successfully removed the stinger 🐝")
    }
}