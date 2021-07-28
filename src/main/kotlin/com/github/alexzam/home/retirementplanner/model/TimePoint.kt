package com.github.alexzam.home.retirementplanner.model

import java.math.BigDecimal
import java.math.RoundingMode
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

    fun applyRounding(varsByName: Map<String, Var>) {
        values = values
            .mapValues {
                val digitsToKeep = varsByName[it.key]!!.digitsToKeep
                if (it.value.scale() > digitsToKeep)
                    it.value.setScale(digitsToKeep, RoundingMode.HALF_DOWN).stripTrailingZeros()
                else it.value
            }
            .toMutableMap()
    }
}