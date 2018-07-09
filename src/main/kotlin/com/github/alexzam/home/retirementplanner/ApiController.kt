package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.PlansRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.websocket.server.PathParam

@Controller
@RequestMapping("/api")

class ApiController @Autowired constructor(private val plansRepository: PlansRepository,
                                           private val planService: PlanService) {

    @RequestMapping("/plans")
    @ResponseBody
    fun getPlans(): String {
        return plansRepository.findAll()
                .map { it.name }
                .joinToString(separator = "\\n")
    }

    @RequestMapping("/plans/{id}")
    @ResponseBody
    fun getPlan(@PathParam("id") id: String) {
        val plan = plansRepository.findById(ObjectId(id)).orElseThrow { Exception("Not found") }

        val points = planService.calculatePlan(plan)
    }

    @RequestMapping("/temp", produces = ["text/plain"])
    @ResponseBody
    fun temp(): String {
        return "Noop"
    }


}