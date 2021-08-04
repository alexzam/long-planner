package com.github.alexzam.longplanner.model

import com.github.alexzam.longplanner.model.rules.Rule
import com.gitlab.alexzam.entityvc.model.Entity
import com.gitlab.alexzam.entityvc.model.Field
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    @Contextual
    val start: LocalDate,
    @Contextual
    val end: LocalDate,
    @Contextual
    val increment: Period,
    val vars: List<Var>,
    val rules: List<Rule>
)

@Serializable
data class Var(
    val id: Long,
    val name: String,
    @Contextual
    val initialValue: BigDecimal = BigDecimal.ZERO,
    val expression: String = "0",
    val digitsToKeep: Int = 2
)
