package dev.alpas.alpasdev.controllers

import dev.alpas.http.HttpCall
import dev.alpas.routing.Controller

class ThanksController : Controller() {
    fun index(call: HttpCall) {
        call.render("thankyou")
    }
}
