package dev.alpas.alpasdev.controllers

import dev.alpas.http.HttpCall
import dev.alpas.routing.Controller
import dev.alpas.alpasdev.entities.Blogs
import dev.alpas.ozone.latest
import dev.alpas.alpasdev.actions.pagination
import dev.alpas.orAbort
import dev.alpas.ozone.create
import dev.alpas.validation.required
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update
import me.liuwj.ktorm.entity.*

class BlogController : Controller() {
    fun blog(call: HttpCall) {
        val blogs = Blogs.latest().sortedByDescending { it.createdAt }.drop(0).take(10).toList()
        val pagination = pagination(1)
        println(blogs)
        call.render("blog", mapOf("blogs" to blogs, "pagination" to pagination))
    }

    fun pages(call: HttpCall) {
        val currentPage = call.stringParam("num")!!.toInt()
        val pagination = pagination(currentPage)
        val blogs = Blogs.latest().sortedByDescending { it.createdAt }.drop(10 * (currentPage - 1)).take(10).toList()

        call.render("blog", mapOf("blogs" to blogs, "pagination" to pagination))
    }

    fun add(call: HttpCall) {
        call.render("addbuzz")
    }

    fun submit(call: HttpCall) {
        Blogs.create() {
            it.title to call.stringParam("title")
            it.url to call.stringParam("url")
            it.body to call.stringParam("body")

            call.redirect().toRouteNamed("blog")
        }
    }

    fun edit(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        val blog = Blogs.latest { it.id eq id }.toList()
        call.render("editbuzz", mapOf("blog" to blog[0]))
    }

    fun update(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        println("hello")

/*        call.applyRules("content") {
            required()
        }.validate()*/

        Blogs.update {
            it.title to call.stringParam("title")
            it.url to call.stringParam("url")
            it.body to call.stringParam("body")
            //it.createdAt to call.stringParam("createdAt")

            where{ it.id eq id }

            call.redirect().toRouteNamed("blog")
        }

        flash("success", "Successfully Saved")
    }

    fun delete(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        Blogs.delete { it.id eq id }
        call.redirect().toRouteNamed("blog")
        flash("success", "Successfully removed buzz")
    }

}