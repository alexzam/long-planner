package com.github.alexzam.home.retirementplanner.model

import java.math.BigDecimal
import java.time.LocalDate

data class TimePoint(var date: LocalDate,
                     val values: MutableMap<Var, BigDecimal>,
                     val inflation: BigDecimal,
                     val events: MutableList<String> = mutableListOf()) {

    operator fun set(variable: String, value: BigDecimal) {
        val varr = values.keys.find { it.name == variable } ?: return
        values[varr] = value
    }

    operator fun get(variable: String): BigDecimal {
        val varr = values.keys.find { it.name == variable } ?: return BigDecimal.ZERO
        return values[varr]!!
    }
}