package com.github.alexzam.longplanner.model.rules

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.conditions.Condition
import java.math.BigDecimal

data class TransferRule(override val id: Long,
                        val from: String,
                        val to: String,
                        val amount: BigDecimal = BigDecimal.ONE,
                        override val condition: Condition?) : Rule() {
    override fun apply(oldState: TimePoint?, state: TimePoint, rules: List<Rule>): List<String> {
        val value = state[from] * amount

        state[from] -= value
        state[to] += value
        return listOf()
    }
}