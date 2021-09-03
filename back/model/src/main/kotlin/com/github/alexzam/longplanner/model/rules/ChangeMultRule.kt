package com.github.alexzam.longplanner.model.rules

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.conditions.Condition
import java.math.BigDecimal

data class ChangeMultRule(
    val variable: Int,
    val mult: BigDecimal,
    override val id: Long,
    override val condition: Condition?
) : Rule() {

    override fun apply(oldState: TimePoint?, state: TimePoint, rules: List<Rule>): List<String> {
        state[variable] *= mult
        return listOf()
    }
}
