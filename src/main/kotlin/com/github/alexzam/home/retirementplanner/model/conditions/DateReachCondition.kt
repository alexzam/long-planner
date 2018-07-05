package com.github.alexzam.home.retirementplanner.model.conditions

import com.github.alexzam.home.retirementplanner.model.TimePoint
import org.springframework.data.annotation.TypeAlias
import java.time.LocalDate

@TypeAlias("C-DR")
class DateReachCondition(val date: LocalDate) : Condition {
    override fun check(state: TimePoint): Boolean = !state.date.isBefore(date)
}