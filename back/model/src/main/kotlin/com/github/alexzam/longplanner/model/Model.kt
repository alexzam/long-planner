@file:UseSerializers(
    BigDecimalSerializer::class,
    LocalDateSerializer::class,
    PeriodSerializer::class
)

package com.github.alexzam.longplanner.model

import com.github.alexzam.longplanner.model.rules.Rule
import com.github.alexzam.longplanner.model.serialization.BigDecimalSerializer
import com.github.alexzam.longplanner.model.serialization.LocalDateSerializer
import com.github.alexzam.longplanner.model.serialization.PeriodSerializer
import com.gitlab.alexzam.entityvc.model.Entity
import com.gitlab.alexzam.entityvc.model.NoField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

@Serializable
@Entity
data class Plan(
    @SerialName("_id")
    val id: Long,
    val name: String,
    val start: LocalDate,
    val end: LocalDate?,
    @NoField
    val increment: Period = Period.ofMonths(1),
    val vars: List<Var>,
    @NoField
    val rules: List<Rule> = listOf()
) {
    companion object {
        fun makeNew(id: Long): Plan = Plan(
            id,
            "no name",
            LocalDate.now().withMonth(1).withDayOfMonth(1),
            LocalDate.now().withMonth(12).withDayOfMonth(31),
            Period.ofMonths(1),
            listOf(),
            listOf()
        )
    }

    fun toShort() = ShortPlan(id, name)
}

@Serializable
@Entity
data class Var(
    val id: Int,
    val name: String,
    val initialValue: BigDecimal = BigDecimal.ZERO,
    val expression: String = "0",
    val digitsToKeep: Int = 2
)
