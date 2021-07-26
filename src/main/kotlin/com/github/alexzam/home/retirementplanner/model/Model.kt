package com.github.alexzam.home.retirementplanner.model

import com.github.alexzam.home.retirementplanner.model.rules.Rule
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.repository.CrudRepository
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.Period

@TypeAlias("P")
data class World(
    @Id val id: ObjectId,
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
    val increment: Period,
    val vars: List<Var>,
    val rules: List<Rule>
)

interface WorldRepository : CrudRepository<World, ObjectId>

@TypeAlias("V")
data class Var(
    val id: Long,
    val name: String,
    val keep: Boolean,
    val initialValue: BigDecimal = BigDecimal.ZERO,
    val expression: String = "0"
)
