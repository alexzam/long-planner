package com.github.alexzam.longplanner.plugins

import com.github.alexzam.longplanner.PlanningService
import com.github.alexzam.longplanner.StorageService
import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.toShort
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import java.time.LocalDate

fun Route.plansRoute(storageService: StorageService, planningService: PlanningService) {
    route("plans") {

        get {
            call.respond(storageService.plans.getAllPlansShort())
        }

        post {
            call.respond(HttpStatusCode.Created, storageService.plans.createPlan())
        }

        route("{planId}") {
            val planId = longParamGetter("planId")

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
                    val stats = storageService.timepoints.getStats(planId())
                    call.respond(stats)
                }

                post {
                    val date = LocalDate.parse(call.parameters["date"]!!)
                    call.respond(storageService.timepoints.addOrGet(planId(), date).toShort())
                }
            }
        }
    }
}