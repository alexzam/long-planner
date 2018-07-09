package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.Plan
import com.github.alexzam.home.retirementplanner.model.Var
import com.github.alexzam.home.retirementplanner.model.rules.ChangeMultRule
import com.github.alexzam.home.retirementplanner.model.rules.ChangeRule
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.LocalDate

fun main(args: Array<String>) {
    val planService = PlanService()

    val var1 = Var("Индекс", true, BigDecimal.valueOf(100_000L))
    val ruleEtf = ChangeMultRule("Индекс", BigDecimal.valueOf(1 + .06 / 12), 1, null)
    val ruleAdd = ChangeRule("get('Индекс') + 20000", "Индекс", 2, null)
    val plan = Plan(ObjectId(), "Test", LocalDate.now(), LocalDate.of(2020, 5, 1), listOf(var1),
            listOf(ruleEtf, ruleAdd))

    val points = planService.calculatePlan(plan)
    points.forEach {
        println("${it.date}\t${it["Индекс"]}")
    }
}