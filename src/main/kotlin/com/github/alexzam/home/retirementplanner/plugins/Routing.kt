package com.example.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    install(AutoHeadResponse)


    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        static("/css") {
            resources("static/js")
        }
        static("/js") {
            resources("static/css")
        }
    }
}
