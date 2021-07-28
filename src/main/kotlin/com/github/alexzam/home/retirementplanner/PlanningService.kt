package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.Var
import com.github.alexzam.home.retirementplanner.model.World
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PlanningService(
    val calcService: CalcService
) {
    fun calculateWorld(world: World, presetPoints: List<TimePoint> = listOf()): List<TimePoint> {
        val varSequence = findSequence(world.vars)
        val varsByName = world.vars.associateBy { it.name }

        val calculatedPoints = mutableListOf<TimePoint>()
        val dates = presetPoints.asSequence()
            .map { it.date }
            .plus(generateSequence(world.start) { date ->
                date.plus(world.increment).takeIf { it <= world.end }
            })
            .sorted()
            .distinct()

        val presetPointsByDate = presetPoints.associateBy { it.date }

        var currentPoint: TimePoint
        var oldPoint = TimePoint(LocalDate.MIN, mutableMapOf(), mutableListOf())

        dates.forEach { date ->
            currentPoint = TimePoint(date, mutableMapOf(), mutableListOf())

            calculateTimePoint(currentPoint, oldPoint, presetPointsByDate[currentPoint.date], varSequence, world)
            oldPoint = currentPoint
                .apply { applyRounding(varsByName) }
                .also { calculatedPoints.add(it) }
        }

        return calculatedPoints
    }

    private fun calculateTimePoint(
        currentPoint: TimePoint,
        oldPoint: TimePoint,
        presetPoint: TimePoint?,
        varSequence: List<Var>,
        world: World
    ) {
        varSequence.forEach { variable ->
            currentPoint[variable] =
                presetPoint?.values?.get(variable.name)
                    ?: calcService.calculateVar(variable, oldPoint, currentPoint)
        }

        world.rules.forEach { it.doApply(oldPoint, currentPoint, world.rules) }
    }

    private fun findSequence(vars: List<Var>): List<Var> {
        val varByNames = vars.associateBy { it.name }
        val dependencies = vars.map { it.name to calcService.getDependencies(it) }.toMap()
        val transitiveDependencies = vars.map { it.name to calculateTransitiveDeps(it, dependencies) }.toMap()

        val toAdd = vars.map { it.name }.toMutableSet()
        val added = mutableSetOf<String>()
        val ret = mutableListOf<Var>()

        while (toAdd.isNotEmpty()) {
            val adding = toAdd.filter {
                val deps = transitiveDependencies[it]!!
                (deps - added).isEmpty()
            }

            ret.addAll(adding.map { varByNames[it]!! })
            added.addAll(adding)
            toAdd.removeAll(adding)
        }

        return ret
    }

    private fun calculateTransitiveDeps(variable: Var, dependencies: Map<String, Set<String>>): Set<String> {
        val visited = mutableSetOf<String>()
        val toCheck = dependencies[variable.name]?.let { ArrayDeque(it) }
            ?: throw IllegalArgumentException("No dependencies for ${variable.name}")

        while (toCheck.isNotEmpty()) {
            val current = toCheck.removeFirst()
            if (current !in visited) {
                visited += current

                val deps = dependencies[current] ?: throw IllegalArgumentException("No dependencies for $current")
                if (variable.name in deps)
                    throw IllegalArgumentException("Circular dependency found for ${variable.name}")

                toCheck.addAll(deps)
            }
        }

        return visited
    }
}