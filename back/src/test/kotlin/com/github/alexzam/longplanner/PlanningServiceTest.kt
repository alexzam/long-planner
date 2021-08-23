package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.Var
import org.junit.Assert
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

        val plan = Plan(
            1, "w1",
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 12, 31),
            Period.ofMonths(1),
            listOf(varE, varD, varC, varB, varA),
            listOf()
        )

        val points = PlanningService(CalcService(), storageService).calculateWorld(plan)
        println("Date\tA\tB\tC\tD\tE")
        points.forEach { it.printAsRow() }
    }

    @Test
    fun presets() {
        val presets = listOf(
            TimePoint(LocalDate.of(2020, 1, 15), mutableMapOf("a" to BigDecimal.valueOf(20)), mutableListOf()),
            TimePoint(LocalDate.of(2020, 2, 1), mutableMapOf("a" to BigDecimal.valueOf(30)), mutableListOf()),
            TimePoint(LocalDate.of(2020, 2, 15), mutableMapOf("b" to BigDecimal.valueOf(51)), mutableListOf()),
            TimePoint(LocalDate.of(2020, 3, 1), mutableMapOf("a" to BigDecimal.valueOf(40)), mutableListOf()),
        )

        val vars = listOf(
            Var(1, "a", expression = "#prev.a"),
            Var(2, "b", expression = "#prev.b + a")
        )

        val end = LocalDate.of(2020, 4, 1)
        val plan = Plan(
            2,
            "w1",
            LocalDate.of(2020, 1, 1),
            end,
            Period.ofMonths(1),
            vars,
            listOf()
        )

        val points = PlanningService(CalcService(), storageService).calculateWorld(plan, presets)

        println("Date\tA\tB")
        points.forEach { it.printAsRow() }

        Assert.assertArrayEquals(
            intArrayOf(40, 131),
            listOf(points.last()["a"], points.last()["b"]).map { it.toInt() }.toIntArray()
        )
    }

    @Test
    fun somethingReal() {
        val vars = listOf(
            Var(2, "r02", expression = "#prev.r02"),
            Var(3, "r03", expression = "#prev.r03"),
            Var(4, "r04", expression = "r03 - r02"),
            Var(4, "r05", expression = "#prev.r05"),
            Var(4, "r06", expression = "0"),
            Var(4, "r07", expression = "#prev.r07"),
            Var(4, "r08", expression = "#prev.r08"),
            Var(4, "r09", expression = "r02 - r07"),
            Var(4, "r10", expression = "r06 + #prev.r10"),
            Var(4, "r11", expression = "r02 + r04 + r05 - r07 - r08"),
            Var(4, "r12", expression = "r11 - r10"),
            Var(4, "r13", expression = "#prev.r13", digitsToKeep = 4),
            Var(4, "r14", expression = "r13 + 1", digitsToKeep = 4),
            Var(4, "r15", expression = "#prev.r15 * r14", digitsToKeep = 4),
            Var(4, "r16", initialValue = BigDecimal.ONE, expression = "#prev.r16 + 1"),
            Var(4, "r17", expression = "#prev.r17 + r06 / r15", digitsToKeep = 0),
            Var(4, "r18", expression = "r11  / r15", digitsToKeep = 0),
            Var(4, "r19", expression = "r18 - r17", digitsToKeep = 0)
        )

        val presets = listOf(
            TimePoint(
                LocalDate.of(2018, 7, 1), mutableMapOf(
                    "r05" to BigDecimal.valueOf(20_079),
                    "r06" to BigDecimal.valueOf(20_000),
                    "r15" to BigDecimal.ONE,
                )
            ),
            TimePoint(
                LocalDate.of(2018, 8, 1), mutableMapOf(
                    "r02" to BigDecimal.valueOf(106_000),
                    "r04" to BigDecimal.valueOf(96_216),
                    "r05" to BigDecimal.valueOf(20_254),
                    "r06" to BigDecimal.valueOf(223_403),
                    "r13" to BigDecimal.valueOf(0.0001),
                )
            ),
        )

        val plan = Plan(
            3,
            "rw",
            LocalDate.of(2018, 7, 1),
            LocalDate.of(2018, 12, 1),
            Period.ofMonths(1),
            vars,
            listOf()
        )

        val result = PlanningService(CalcService(), storageService).calculateWorld(plan, presets)

        print("Date:\t")
        println(vars.map { it.name }.sorted().joinToString("\t"))
        result.forEach { it.printAsRow() }
    }
}