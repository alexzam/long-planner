package com.github.alexzam.home.retirementplanner.model

import com.github.alexzam.home.retirementplanner.model.rules.Rule
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

@Serializable
data class World(
    @SerialName("_id")
    val id: Long,
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
