package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.Var
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

class PlanningServiceTest {
    private val storageService: StorageService = mock(StorageService::class.java)

    @Test
    suspend fun vars() {
        val varA = Var(1, "a", BigDecimal.ONE, "#prev.id1 + 1")
        val varB = Var(2, "b", expression = "id1*2")
        val varC = Var(3, "c", expression = "id2+1")
        val varD = Var(4, "d", BigDecimal.ONE, expression = "#prev.id4 * 1.002", digitsToKeep = 5)
        val varE = Var(5, "e", BigDecimal.ONE, expression = "#prev.id5+id4", digitsToKeep = 5)

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
    suspend fun presets() {
        val presets = listOf(
            TimePoint(0, 1, LocalDate.of(2020, 1, 15), mutableMapOf(1 to BigDecimal.valueOf(20))),
            TimePoint(1, 1, LocalDate.of(2020, 2, 1), mutableMapOf(1 to BigDecimal.valueOf(30))),
            TimePoint(1, 1, LocalDate.of(2020, 2, 15), mutableMapOf(2 to BigDecimal.valueOf(51))),
            TimePoint(1, 1, LocalDate.of(2020, 3, 1), mutableMapOf(1 to BigDecimal.valueOf(40))),
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
            listOf(points.last()[1], points.last()[2]).map { it.toInt() }.toIntArray()
        )
    }

    @Test
    suspend fun somethingReal() {
        val vars = listOf(
            Var(2, "r02", expression = "#prev.r02"),
            Var(3, "r03", expression = "#prev.r03"),
            Var(4, "r04", expression = "r03 - r02"),
            Var(5, "r05", expression = "#prev.r05"),
            Var(6, "r06", expression = "0"),
            Var(7, "r07", expression = "#prev.r07"),
            Var(8, "r08", expression = "#prev.r08"),
            Var(9, "r09", expression = "r02 - r07"),
            Var(10, "r10", expression = "r06 + #prev.r10"),
            Var(11, "r11", expression = "r02 + r04 + r05 - r07 - r08"),
            Var(12, "r12", expression = "r11 - r10"),
            Var(13, "r13", expression = "#prev.r13", digitsToKeep = 4),
            Var(14, "r14", expression = "r13 + 1", digitsToKeep = 4),
            Var(15, "r15", expression = "#prev.r15 * r14", digitsToKeep = 4),
            Var(16, "r16", initialValue = BigDecimal.ONE, expression = "#prev.r16 + 1"),
            Var(17, "r17", expression = "#prev.r17 + r06 / r15", digitsToKeep = 0),
            Var(18, "r18", expression = "r11  / r15", digitsToKeep = 0),
            Var(19, "r19", expression = "r18 - r17", digitsToKeep = 0)
        )

        val presets = listOf(
            TimePoint(
                1, 1,
                LocalDate.of(2018, 7, 1), mutableMapOf(
                    5 to BigDecimal.valueOf(20_079),
                    6 to BigDecimal.valueOf(20_000),
                    15 to BigDecimal.ONE,
                )
            ),
            TimePoint(
                1, 1,
                LocalDate.of(2018, 8, 1), mutableMapOf(
                    2 to BigDecimal.valueOf(106_000),
                    4 to BigDecimal.valueOf(96_216),
                    5 to BigDecimal.valueOf(20_254),
                    6 to BigDecimal.valueOf(223_403),
                    13 to BigDecimal.valueOf(0.0001),
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