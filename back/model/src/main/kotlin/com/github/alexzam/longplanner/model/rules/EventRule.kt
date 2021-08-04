package com.github.alexzam.longplanner.model.rules

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.conditions.Condition

data class EventRule(override val id: Long, val event: String, override val condition: Condition?) : Rule() {
    override fun apply(oldState: TimePoint?, state: TimePoint, rules: List<Rule>): List<String> {
        return listOf(event)
    }
}