package com.github.alexzam.longplanner.plugins

import com.github.alexzam.longplanner.PlanningService
import com.github.alexzam.longplanner.StorageService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
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
        static("fonts") {
            resources("static/fonts")
        }

        route("api") {
            plansRoute(storageService, planningService)
            timepointsRoute(storageService)
        }
    }
}

fun longParamGetter(name: String): PipelineContext<Unit, ApplicationCall>.() -> Long =
    { call.parameters[name]!!.toLong() }