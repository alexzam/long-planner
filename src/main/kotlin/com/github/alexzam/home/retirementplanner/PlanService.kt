package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.Plan
import com.github.alexzam.home.retirementplanner.model.TimePoint
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

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

            oldPoint = currentPoint.copy()
            oldPoint.values = oldPoint.values
                    .mapValues { entry -> entry.value.setScale(2, RoundingMode.HALF_EVEN) }
                    .mapValues { entry -> if (entry.key.keep) entry.value else entry.key.initialValue }
                    .toMutableMap()

            points.add(oldPoint)
            currentPoint.date = currentPoint.date.plusMonths(1)
        }

        return points
    }
}
