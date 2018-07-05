package com.github.alexzam.home.retirementplanner.model.rules

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.conditions.Condition
import org.springframework.data.annotation.TypeAlias
import java.math.BigDecimal

@TypeAlias("R-CM")
data class ChangeMultRule(val variable: String,
                          val mult: BigDecimal,
                          override val id: Long,
                          override val condition: Condition?) : Rule() {

    override fun apply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String> {
        state[variable] *= mult
        return listOf()
    }
}
