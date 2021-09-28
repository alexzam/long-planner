package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.TimePoint
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TimepointsDaoTest : DbTest() {
    @Test
    fun timepointsStats() {
        val collection = db.getCollection<TimePoint>()

        runBlocking {
            collection.insertMany(
                listOf(
                    TimePoint(1, 1, LocalDate.of(2000, 1, 1)),
                    TimePoint(2, 1, LocalDate.of(2000, 2, 1)),
                    TimePoint(3, 1, LocalDate.of(2000, 3, 1), values = mutableMapOf(1 to BigDecimal.ZERO)),
                    TimePoint(4, 1, LocalDate.of(2000, 4, 1), presetValues = mutableMapOf(1 to BigDecimal.ZERO)),
                    TimePoint(
                        5,
                        1,
                        LocalDate.of(2000, 5, 1),
                        presetValues = mutableMapOf(1 to BigDecimal.ZERO),
                        values = mutableMapOf(1 to BigDecimal.ZERO)
                    ),
                    TimePoint(
                        6,
                        1,
                        LocalDate.of(2000, 6, 1),
                        presetValues = mutableMapOf(1 to BigDecimal.ZERO),
                        values = mutableMapOf(1 to BigDecimal.ZERO)
                    ),
                    TimePoint(7, 1, LocalDate.of(2000, 7, 1), presetValues = mutableMapOf(1 to BigDecimal.ZERO)),
                    TimePoint(8, 1, LocalDate.of(2000, 8, 1), values = mutableMapOf(1 to BigDecimal.ZERO)),
                    TimePoint(9, 1, LocalDate.of(2000, 9, 1))
                )
            )

            val dao = TimepointsDao(db, CounterDao(db))

            val stats = dao.getStats(1)

            stats.forEach { println(it) }

            val none = stats.find { !it.isPreset && !it.isCalc }
            assertNotNull(none)
            assertEquals(3, none.num)
            assertEquals(Month.JANUARY, none.minDate.month)
            assertEquals(Month.SEPTEMBER, none.maxDate.month)

            val both = stats.find { it.isPreset && it.isCalc }
            assertNotNull(both)
            assertEquals(2, both.num)
            assertEquals(Month.MAY, both.minDate.month)
            assertEquals(Month.JUNE, both.maxDate.month)
        }
    }
}