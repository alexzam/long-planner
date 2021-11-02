package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.dao.CounterDao
import com.github.alexzam.longplanner.dao.TimepointsDao
import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.Var
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class PlanningServiceTest {
    private object counterDao : CounterDao {
        val seq = generateSequence(1L) { it + 1 }.iterator()
        override suspend fun getCounterValue(id: String): Long = seq.next()
    }

    val timepointsDao: TimepointsDao = TimepointsDao(mockk(relaxed = true), counterDao)

    private val storageService: StorageService = mockk() {
        every { timepoints } returns timepointsDao
    }

    @Test
    fun vars() {
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

        val points = runBlocking { PlanningService(CalcService(), storageService).calculateWorld(plan) }
        println("Date\tA\tB\tC\tD\tE")
        points.forEach { it.printAsRow() }
    }

    @Test
    fun presets() {
        val presets = listOf(
            TimePoint(0, 1, LocalDate.of(2020, 1, 15), mutableMapOf(1 to BigDecimal.valueOf(20))),
            TimePoint(1, 1, LocalDate.of(2020, 2, 1), mutableMapOf(1 to BigDecimal.valueOf(30))),
            TimePoint(1, 1, LocalDate.of(2020, 2, 15), mutableMapOf(2 to BigDecimal.valueOf(51))),
            TimePoint(1, 1, LocalDate.of(2020, 3, 1), mutableMapOf(1 to BigDecimal.valueOf(40))),
        )

        val vars = listOf(
            Var(1, "a", expression = "#prev.id1"),
            Var(2, "b", expression = "#prev.id2 + id1")
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

        val points = runBlocking { PlanningService(CalcService(), storageService).calculateWorld(plan, presets) }

        println("Date\tA\tB")
        points.forEach { it.printAsRow() }

        Assert.assertArrayEquals(
            intArrayOf(40, 131),
            listOf(points.last()[1], points.last()[2]).map { it.toInt() }.toIntArray()
        )
    }

    @Test
    fun somethingReal() {
        val vars = listOf(
            Var(2, "id02", expression = "#prev.id02"),
            Var(3, "id03", expression = "#prev.id03"),
            Var(4, "id04", expression = "id03 - id02"),
            Var(5, "id05", expression = "#prev.id05"),
            Var(6, "id06", expression = "0"),
            Var(7, "id07", expression = "#prev.id07"),
            Var(8, "id08", expression = "#prev.id08"),
            Var(9, "id09", expression = "id02 - id07"),
            Var(10, "id10", expression = "id06 + #prev.id10"),
            Var(11, "id11", expression = "id02 + id04 + id05 - id07 - id08"),
            Var(12, "id12", expression = "id11 - id10"),
            Var(13, "id13", expression = "#prev.id13", digitsToKeep = 4),
            Var(14, "id14", expression = "id13 + 1", digitsToKeep = 4),
            Var(15, "id15", expression = "#prev.id15 * id14", digitsToKeep = 4),
            Var(16, "id16", initialValue = BigDecimal.ONE, expression = "#prev.id16 + 1"),
            Var(17, "id17", expression = "#prev.id17 + id06 / id15", digitsToKeep = 0),
            Var(18, "id18", expression = "id11  / id15", digitsToKeep = 0),
            Var(19, "id19", expression = "id18 - id17", digitsToKeep = 0)
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

        val result = runBlocking { PlanningService(CalcService(), storageService).calculateWorld(plan, presets) }

        print("Date:\t")
        println(vars.map { it.name }.sorted().joinToString("\t"))
        result.forEach { it.printAsRow() }
    }

    @Test
    fun prevMonthPoint() {
        val vars = listOf(
            Var(1, "moncounter", expression = "#prevmon.id1 + 1", initialValue = BigDecimal.ONE),
            Var(2, "counter", expression = "#prev.id2 + 1", initialValue = BigDecimal.ONE)
        )

        val plan = Plan(
            4,
            "prevmon",
            LocalDate.of(2019, 1, 1),
            LocalDate.of(2019, 12, 31),
            Period.ofDays(7),
            vars,
            listOf()
        )

        val result = runBlocking { PlanningService(CalcService(), storageService).calculateWorld(plan, listOf()) }

        result.last().printAsRow()
        assertEquals(12, result.last().values[1]?.toInt())
        assertThat("counter should be greater than 12", 12 < (result.last().values[2]?.toInt() ?: 0))
    }
}
