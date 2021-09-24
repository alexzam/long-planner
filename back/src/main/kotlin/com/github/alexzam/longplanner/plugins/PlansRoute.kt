package com.github.alexzam.longplanner.plugins

import com.github.alexzam.longplanner.PlanningService
import com.github.alexzam.longplanner.StorageService
import com.github.alexzam.longplanner.model.Plan
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

fun Route.plansRoute(storageService: StorageService, planningService: PlanningService) {
    route("plans") {

        get {
            call.respond(storageService.plans.getAllPlansShort())
        }

        post {
            call.respond(HttpStatusCode.Created, storageService.plans.createPlan())
        }

        route("{planId}") {
            fun PipelineContext<Unit, ApplicationCall>.planId() = call.parameters["planId"]!!.toLong()

            get {
                call.respond(storageService.plans.getPlan(planId()) ?: throw NotFoundException("Plan not found"))
            }

            put {
                val plan = call.receive<Plan>()
                call.respond(storageService.plans.updatePlanParams(plan))
            }

            route("vars") {
                post {
                    call.respond(planningService.addVar(planId()))
                }

                put("{varId}") {
                    fun PipelineContext<Unit, ApplicationCall>.varId() = call.parameters["varId"]!!.toInt()

                    call.respond(storageService.plans.updateVar(planId(), varId(), call.receive()))
                }
            }

            route("timepoints") {
                get {

                }
            }
        }
    }
}