package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.Var
import io.ktor.features.*
import java.time.LocalDate

class PlanningService(
    val calcService: CalcService,
    val storageService: StorageService
) {
    fun calculateWorld(plan: Plan, presetPoints: List<TimePoint> = listOf()): List<TimePoint> {
        val varSequence = findSequence(plan.vars)
        val varsById = plan.vars.associateBy { it.id }

        val calculatedPoints = mutableListOf<TimePoint>()
        val dates = presetPoints.asSequence()
            .map { it.date }
            .plus(generateSequence(plan.start) { date ->
                date.plus(plan.increment).takeIf { it <= plan.end }
            })
            .sorted()
            .distinct()

        val presetPointsByDate = presetPoints.associateBy { it.date }

        var currentPoint: TimePoint
        var oldPoint = TimePoint(LocalDate.MIN, mutableMapOf(), mutableListOf())

        dates.forEach { date ->
            currentPoint = TimePoint(date, mutableMapOf(), mutableListOf())

            calculateTimePoint(currentPoint, oldPoint, presetPointsByDate[currentPoint.date], varSequence, plan)
            oldPoint = currentPoint
                .apply { applyRounding(varsById) }
                .also { calculatedPoints.add(it) }
        }

        return calculatedPoints
    }

    suspend fun addVar(planId: Long): Var {
        val plan = storageService.getPlan(planId) ?: throw NotFoundException("Plan $planId not found")
        val maxVarId = plan.vars.maxOfOrNull { it.id } ?: 0
        val varr = Var(maxVarId + 1, "no name")
        storageService.addVarToPlan(planId, varr)
        return varr
    }

    private fun calculateTimePoint(
        currentPoint: TimePoint,
        oldPoint: TimePoint,
        presetPoint: TimePoint?,
        varSequence: List<Var>,
        plan: Plan
    ) {
        varSequence.forEach { variable ->
            currentPoint[variable] =
                presetPoint?.values?.get(variable.id)
                    ?: calcService.calculateVar(variable, oldPoint, currentPoint)
        }

        plan.rules.forEach { it.doApply(oldPoint, currentPoint, plan.rules) }
    }

    private fun findSequence(vars: List<Var>): List<Var> {
        val varByIds = vars.associateBy { it.id }
        val dependencies = vars.map { it.id to calcService.getDependencies(it) }.toMap()
        val transitiveDependencies = vars.map { it.id to calculateTransitiveDeps(it, dependencies) }.toMap()

        val toAdd = vars.map { it.id }.toMutableSet()
        val added = mutableSetOf<Int>()
        val ret = mutableListOf<Var>()

        while (toAdd.isNotEmpty()) {
            val adding = toAdd.filter {
                val deps = transitiveDependencies[it]!!
                (deps - added).isEmpty()
            }

            ret.addAll(adding.map { varByIds[it]!! })
            added.addAll(adding)
            toAdd.removeAll(adding)
        }

        return ret
    }

    private fun calculateTransitiveDeps(variable: Var, dependencies: Map<Int, Set<Int>>): Set<Int> {
        val visited = mutableSetOf<Int>()
        val toCheck = dependencies[variable.id]?.let { ArrayDeque(it) }
            ?: throw IllegalArgumentException("No dependencies for ${variable.name}")

        while (toCheck.isNotEmpty()) {
            val current = toCheck.removeFirst()
            if (current !in visited) {
                visited += current

                val deps = dependencies[current] ?: throw IllegalArgumentException("No dependencies for $current")
                if (variable.id in deps)
                    throw IllegalArgumentException("Circular dependency found for ${variable.name}")

                toCheck.addAll(deps)
            }
        }

        return visited
    }
}