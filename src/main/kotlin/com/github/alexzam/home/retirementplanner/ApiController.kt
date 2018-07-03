package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.Account
import com.github.alexzam.home.retirementplanner.model.PercentRule
import com.github.alexzam.home.retirementplanner.model.Plan
import com.github.alexzam.home.retirementplanner.model.PlansRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigDecimal
import java.time.LocalDate
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
    fun temp() : String {
        val perc4Rule = PercentRule(BigDecimal.valueOf(0.04))
        val acc = Account("Депозит", BigDecimal.valueOf(1_384_000L), listOf(perc4Rule))
        val plan = Plan(ObjectId(), "Test", LocalDate.now(), LocalDate.of(2020, 5, 1), listOf(acc))

        val points = planService.calculatePlan(plan)
        val ret = StringBuilder()
        points.forEach {
            ret.append(it.date)
                    .append("\t")
                    .append(it.state[acc]?.value)
                    .append("\n")
        }

        return ret.toString()
    }


}