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
import com.gitlab.alexzam.entityvc.model.Field
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
    @Field
    val id: Long,
    @Field
    val name: String,
    @Field
    val start: LocalDate,
    @Field
    val end: LocalDate?,
    val increment: Period = Period.ofMonths(1),
    @Field
    val vars: List<Var>,
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
    @Field
    val id: Int,
    @Field
    val name: String,
    @Field
    val initialValue: BigDecimal = BigDecimal.ZERO,
    @Field
    val expression: String = "0",
    @Field
    val digitsToKeep: Int = 2
)
