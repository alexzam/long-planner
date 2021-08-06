package com.example.plugins

import com.github.alexzam.longplanner.StorageService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting(storageService: StorageService) {
    install(AutoHeadResponse)

    routing {
        defaultResource("index.html", "static")

        static("js") {
            resources("static/js")
        }
        static("css") {
            resources("static/css")
        }

        route("api") {
            route("plans") {

                get {
                    call.respondText("ok get")

                }
                post {
                    call.respond(HttpStatusCode.Created, storageService.createPlan())
                }
            }
        }
    }
}
