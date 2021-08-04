package com.github.alexzam.longplanner.model.conditions

import com.github.alexzam.longplanner.model.TimePoint
import java.time.LocalDate

class DateReachCondition(val date: LocalDate) : Condition {
    override fun check(state: TimePoint): Boolean = !state.date.isBefore(date)
}