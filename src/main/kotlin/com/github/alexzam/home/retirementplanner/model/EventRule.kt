package com.github.alexzam.home.retirementplanner.model

import org.springframework.data.annotation.TypeAlias

@TypeAlias("R-E")
data class EventRule(override val id: Long, val event: String) : Rule() {
    override fun apply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String> {
        return listOf(event)
    }
}