package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.Var
import com.github.alexzam.home.retirementplanner.model.World
import org.bson.types.ObjectId
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

class PlanningServiceTest {
    @Test
    fun vars() {
        val varA = Var(1, "a", BigDecimal.ONE, "#prev.a + 1")
        val varB = Var(2, "b", expression = "a*2")
        val varC = Var(3, "c", expression = "b+1")
        val varD = Var(4, "d", BigDecimal.ONE, expression = "#prev.d * 1.002", digitsToKeep = 5)
        val varE = Var(5, "e", BigDecimal.ONE, expression = "#prev.e+d", digitsToKeep = 5)

        val world = World(
            ObjectId(), "w1",
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 12, 31),
            Period.ofMonths(1),
            listOf(varE, varD, varC, varB, varA),
            listOf()
        )

        val points = PlanningService(CalcService()).calculateWorld(world)
        println("Date\tA\tB\tC\tD\tE")
        points.forEach {
            println("${it.date}\t${it["a"]}\t${it["b"]}\t${it["c"]}\t${it["d"]}\t${it["e"]}")
        }
    }
}