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
                    call.respond(storageService.getAllPlansShort())
                }

                post {
                    call.respond(HttpStatusCode.Created, storageService.createPlan())
                }

                route("{planId}") {
                    get {
                        val planId = call.parameters["planId"]!!.toLong()
                        call.respond(storageService.getPlan(planId) ?: throw NotFoundException("Plan not found"))
                    }

                    post("_updateName") {
                        val planId = call.parameters["planId"]!!.toLong()
                        val newName = call.request.queryParameters["name"]!!

                        storageService.updateName(planId, newName)
                        call.respondText("OK")
                    }
                }
            }
        }
    }
}
