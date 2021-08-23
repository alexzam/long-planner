package com.example.plugins

import com.github.alexzam.longplanner.PlanningService
import com.github.alexzam.longplanner.StorageService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

fun Application.configureRouting(storageService: StorageService, planningService: PlanningService) {
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
                    fun PipelineContext<Unit, ApplicationCall>.planId() = call.parameters["planId"]!!.toLong()

                    get {
                        call.respond(storageService.getPlan(planId()) ?: throw NotFoundException("Plan not found"))
                    }

                    post("_updateName") {
                        val newName = call.request.queryParameters["name"]!!

                        storageService.updateName(planId(), newName)
                        call.respondText("OK")
                    }

                    route("vars") {
                        post {
                            call.respond(planningService.addVar(planId()))
                        }
                    }
                }
            }
        }
    }
}
