package com.github.alexzam.home.retirementplanner.model

import com.github.alexzam.home.retirementplanner.model.rules.Rule
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.repository.CrudRepository
import java.math.BigDecimal
import java.time.LocalDate

@TypeAlias("P")
data class Plan(@Id val id: ObjectId,
                val name: String,
                val start: LocalDate,
                val end: LocalDate,
                val vars: List<Var>,
                val rules: List<Rule>)

interface PlansRepository : CrudRepository<Plan, ObjectId>

@TypeAlias("V")
data class Var(val name: String,
               val keep: Boolean,
               val initialValue: BigDecimal = BigDecimal.ZERO)
