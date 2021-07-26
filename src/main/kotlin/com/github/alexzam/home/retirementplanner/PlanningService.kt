package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.World
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PlanningService(
    val calcService: CalcService
) {
    fun calculateWorld(world: World): List<TimePoint> {
        val start = world.start
        val initValues = world.vars.associate { it to it.initialValue }.toMutableMap()

        val currentPoint = TimePoint(start, initValues, BigDecimal.ONE, mutableListOf())
        var oldPoint = currentPoint.copy()
        val points = mutableListOf(oldPoint)
        val rules = world.rules.toMutableList()
        currentPoint.date = currentPoint.date.plusMonths(1)

        while (currentPoint.date < world.end) {
            rules.forEach { it.doApply(oldPoint, currentPoint, rules) }

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