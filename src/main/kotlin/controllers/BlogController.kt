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
import me.liuwj.ktorm.schema.date
import java.time.Instant
import java.time.LocalDate
import java.util.Date
import java.util.Calendar

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

        // val date = call.stringParam("createdAt")
        // val createdAt = Calendar.getInstance(date)
        // val createdAt: Instant? = LocalDate.parse(date, DateTimeFormatter.ISO_DATE).toInstant()
        call.applyRules("title") {
            required()
        }.validate()
        call.applyRules("url") {
            required()
        }.validate()
        call.applyRules("body") {
            required()
        }.validate()

        Blogs.create() {
            it.title to call.stringParam("title")
            it.url to call.stringParam("url")
           // it.createdAt to createdAt as Instant?
            it.body to call.stringParam("body")

            call.redirect().back()
            flash("success", "Successfully added some sweet honey 🍯")
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
        flash("success", "Successfully removed the stinger 🐝")
    }

}