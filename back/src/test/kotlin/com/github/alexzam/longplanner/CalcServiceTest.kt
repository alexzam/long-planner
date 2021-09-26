package com.github.alexzam.longplanner;

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.Var
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class CalcServiceTest {
    @Test
    fun calcVar() {
        val service = CalcService()

        val var1 = Var(1, "a", expression = "13")
        val var2 = Var(2, "b", expression = "a * 2")
        val var3 = Var(3, "c", expression = "#prev.a + b + 1")

        val timePoint = TimePoint(
            0, 1,
            LocalDate.of(2021, 7, 1)
        )

        val val1_1 = service.calculateVar(var1, null, timePoint)
        assertEquals(BigDecimal(13), val1_1)

        timePoint[var1] = val1_1
        val val2_1 = service.calculateVar(var2, null, timePoint)
        assertEquals(BigDecimal(26), val2_1)

        timePoint[var2] = val2_1

        val timePoint2 = timePoint.copy()
        val val1_2 = service.calculateVar(var1,  timePoint, timePoint2)
        assertEquals(BigDecimal(13), val1_2)

        timePoint2[var1] = val1_2
        val val2_2 = service.calculateVar(var2, timePoint, timePoint2)
        assertEquals(BigDecimal(26), val2_2)

        timePoint2[var2] = val2_2
        val val3_2 = service.calculateVar(var3, timePoint, timePoint2)
        assertEquals(BigDecimal(40), val3_2)
    }
}
