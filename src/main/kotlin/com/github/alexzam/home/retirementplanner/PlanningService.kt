package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.Var
import com.github.alexzam.home.retirementplanner.model.World
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class PlanningService(
    val calcService: CalcService
) {
    fun calculateWorld(world: World): List<TimePoint> {
        val varSequence = findSequence(world.vars)

        val start = world.start
        val currentPoint = TimePoint(start, mutableMapOf(), mutableListOf())
        var oldPoint: TimePoint? = null
        val points = mutableListOf<TimePoint>()

        while (currentPoint.date <= world.end) {
            varSequence.forEach { variable ->
                currentPoint[variable] = calcService.calculateVar(variable, oldPoint, currentPoint)
            }

            world.rules.forEach { it.doApply(oldPoint, currentPoint, world.rules) }

            oldPoint = currentPoint.copy()
            oldPoint.values = oldPoint.values
                .mapValues {
                    if (it.value.scale() > it.key.digitsToKeep)
                        it.value.setScale(it.key.digitsToKeep, RoundingMode.HALF_DOWN).stripTrailingZeros()
                    else it.value
                }
                .toMutableMap()

            points.add(oldPoint)
            currentPoint.date = currentPoint.date.plus(world.increment)
        }

        return points
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