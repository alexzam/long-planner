package com.github.alexzam.home.retirementplanner.model.rules

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.conditions.Condition
import org.springframework.data.annotation.TypeAlias

@TypeAlias("R-E")
data class EventRule(override val id: Long, val event: String, override val condition: Condition?) : Rule() {
    override fun apply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String> {
        return listOf(event)
    }
}