package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.Plan
import com.github.alexzam.home.retirementplanner.model.TimePoint
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlanService {
    fun calculatePlan(plan: Plan): List<TimePoint> {
        val start = plan.start.withDayOfMonth(1)
        val initValues = plan.vars.associate { it to it.initialValue }.toMutableMap()

        val currentPoint = TimePoint(start, initValues, BigDecimal.ONE, mutableListOf())
        var oldPoint = currentPoint.copy()
        val points = mutableListOf(oldPoint)
        val rules = plan.rules.toMutableList()
        currentPoint.date = currentPoint.date.plusMonths(1)

        while (currentPoint.date.isBefore(plan.end)) {
            rules.forEach { rule -> rule.doApply(oldPoint, currentPoint, rules) }

            oldPoint = currentPoint.copy(events = mutableListOf())
            oldPoint.values
                    .filterKeys { !it.keep }
                    .mapValues { entry -> entry.key.initialValue }

            points.add(oldPoint)
            currentPoint.date = currentPoint.date.plusMonths(1)
        }

        return points
    }
}
