package com.github.alexzam.longplanner.plugins

import com.github.alexzam.longplanner.StorageService
import com.github.alexzam.longplanner.model.TimepointWithPrev
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.timepointsRoute(storageService: StorageService) {
    route("timepoints") {
        route("{timepointId}") {
            val timepointId = longParamGetter("timepointId")
            get {
                val withPrev = call.parameters["timepointId"].toBoolean()
                val timePoint = storageService.timepoints.getById(timepointId())

                if (withPrev)
                    call.respond(timePoint)
                else
                    call.respond(
                        TimepointWithPrev(
                            cur = timePoint,
                            prev = storageService.timepoints.getPrevOf(timePoint.planId, timePoint.date)
                        )
                    )
            }

            post("values/{varId}") {
                val varId = longParamGetter("varId")
                val value = call.parameters["value"]?.toBigDecimal() ?: throw BadRequestException("No value parameter")

                storageService.timepoints.savePresetValue(timepointId(), varId(), value)
                call.respond("OK")
            }
        }
    }
}