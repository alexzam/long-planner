package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.AccountState
import com.github.alexzam.home.retirementplanner.model.Plan
import com.github.alexzam.home.retirementplanner.model.TimePoint
import org.springframework.stereotype.Service

@Service
class PlanService {
    fun calculatePlan(plan: Plan): List<TimePoint> {
        var date = plan.start.withDayOfMonth(1)
        var currentStates = plan.accounts
                .associate { acc -> acc to AccountState(acc.initialValue) }

        val points = mutableListOf(TimePoint(date, currentStates))

        while (date.isBefore(plan.end)) {
            currentStates = currentStates.mapValues { entry ->
                entry.key.rules
                        .fold(entry.value) { state, rule -> rule.apply(state) }
            }
            points.add(TimePoint(date, currentStates.toMap()))
            date = date.plusMonths(1)
        }

        return points
    }
}
