package com.github.alexzam.home.retirementplanner.model

import org.springframework.data.annotation.TypeAlias
import java.math.BigDecimal

@TypeAlias("R-CM")
data class ChangeMultRule(val variable: String,
                     val mult: BigDecimal) : Rule {

    override fun apply(oldState: TimePoint, state: TimePoint) {
        state[variable] *= mult
    }
}
