package com.github.alexzam.home.retirementplanner.model.rules

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.conditions.Condition
import org.springframework.data.annotation.TypeAlias

@TypeAlias("R-S")
data class SwitchRule(val rule: Long,
                      val enable: Boolean,
                      override val id: Long,
                      override val condition: Condition?) : Rule() {
    override fun apply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String> {
        val foundRule = rules.find { it.id == rule } ?: return listOf()

        val ret = if (foundRule.enabled != enable) listOf("Rule $rule switched ${if (enable) "ON" else "OFF"}")
        else listOf()

        foundRule.enabled = enable
        return ret
    }
}