package com.github.alexzam.home.retirementplanner.model.conditions

import com.github.alexzam.home.retirementplanner.model.TimePoint
import java.math.BigDecimal

class ValueReachCondition(val variable: String, val value: BigDecimal) : Condition {
    override fun check(state: TimePoint): Boolean = state[variable] >= value
}