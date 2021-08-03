package com.example.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun Application.configureRouting() {
    install(AutoHeadResponse)


    routing {
        defaultResource("index.html", "static")

        static("/js") {
            resources("static/js")
        }
        static("/css") {
            resources("static/css")
        }
    }
}
