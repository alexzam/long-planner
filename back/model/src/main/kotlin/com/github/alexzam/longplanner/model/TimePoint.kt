@file:UseSerializers(
    BigDecimalSerializer::class,
    LocalDateSerializer::class
)

package com.github.alexzam.longplanner.model

import com.github.alexzam.longplanner.model.serialization.BigDecimalSerializer
import com.github.alexzam.longplanner.model.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Serializable
data class TimePoint(
    val date: LocalDate,
    val values: MutableMap<Int, BigDecimal> = mutableMapOf(),
    val events: MutableList<String> = mutableListOf()
) {

    operator fun set(varId: Int, value: BigDecimal) {
        values[varId] = value
    }

    operator fun set(variable: Var, value: BigDecimal) {
        values[variable.id] = value
    }

    operator fun get(varId: Int): BigDecimal {
        return values[varId] ?: BigDecimal.ZERO
    }

    fun copy(): TimePoint {
        val valuesCopy = mutableMapOf<Int, BigDecimal>()
        valuesCopy.putAll(values)
        return TimePoint(date, valuesCopy)
    }

    fun applyRounding(varsById: Map<Int, Var>) {
        values.replaceAll { id, value ->
            val digitsToKeep = varsById[id]!!.digitsToKeep
            if (value.scale() > digitsToKeep)
                value.setScale(digitsToKeep, RoundingMode.HALF_DOWN).stripTrailingZeros()
            else value
        }
    }

    fun printAsRow() {
        print("$date\t")
        println(values.keys.sorted().map { values[it] }.joinToString("\t"))
    }
}