package com.github.alexzam.home.retirementplanner.model

import com.github.alexzam.home.retirementplanner.model.rules.Rule
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

data class World(
    val id: ObjectId,
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
    val increment: Period,
    val vars: List<Var>,
    val rules: List<Rule>
)

data class Var(
    val id: Long,
    val name: String,
    val initialValue: BigDecimal = BigDecimal.ZERO,
    val expression: String = "0",
    val digitsToKeep: Int = 2
)
