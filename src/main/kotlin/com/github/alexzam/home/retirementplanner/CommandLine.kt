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

    val etf = Var("Индекс", true, BigDecimal.valueOf(300_000L))
    val sal = Var("sal", true, BigDecimal.valueOf(140000 + 105000 * 0.87))

    val ruleEtf = ChangeMultRule("Индекс", BigDecimal.valueOf(1 + .06 / 12), 1, null)
    val ruleAdd = ChangeRule("get('Индекс') + get('sal') * 0.08", "Индекс", 2, null)
    val plan = Plan(ObjectId(), "Test", LocalDate.now(), LocalDate.of(2020, 5, 1), listOf(etf, sal),
            listOf(ruleEtf, ruleAdd))

    val points = planService.calculatePlan(plan)
    points.forEach {
        println("${it.date}\t${it["Индекс"]}")
    }
}