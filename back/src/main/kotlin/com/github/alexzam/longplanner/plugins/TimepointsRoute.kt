package com.github.alexzam.longplanner.plugins

import com.github.alexzam.longplanner.StorageService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.timepointsRoute(storageService: StorageService) {
    route("timepoints") {
        get("{timepointId}") {
            val timepointId = longParamGetter("timepointId")

            call.respond(storageService.timepoints.getById(timepointId()))
        }
    }
}