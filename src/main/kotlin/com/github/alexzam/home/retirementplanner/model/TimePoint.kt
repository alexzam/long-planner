package com.github.alexzam.home.retirementplanner.model

import java.math.BigDecimal
import java.time.LocalDate

data class TimePoint(
    var date: LocalDate,
    var values: MutableMap<String, BigDecimal>,
    val events: MutableList<String> = mutableListOf()
) {

    operator fun set(variable: String, value: BigDecimal) {
        if (variable !in values.keys) return
        values[variable] = value
    }

    operator fun set(variable: Var, value: BigDecimal) {
        values[variable.name] = value
    }

    operator fun get(variable: String): BigDecimal {
        return values[variable] ?: BigDecimal.ZERO
    }

    fun copy(): TimePoint {
        val valuesCopy = mutableMapOf<String, BigDecimal>()
        valuesCopy.putAll(values)
        return TimePoint(date, valuesCopy)
    }
}