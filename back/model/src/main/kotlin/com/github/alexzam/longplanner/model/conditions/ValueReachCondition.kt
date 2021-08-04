package com.github.alexzam.longplanner.model.conditions

import com.github.alexzam.longplanner.model.TimePoint
import java.math.BigDecimal

class ValueReachCondition(val variable: String, val value: BigDecimal) : Condition {
    override fun check(state: TimePoint): Boolean = state[variable] >= value
}