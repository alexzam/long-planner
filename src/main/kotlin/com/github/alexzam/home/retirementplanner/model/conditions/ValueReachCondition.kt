package com.github.alexzam.home.retirementplanner.model.conditions

import com.github.alexzam.home.retirementplanner.model.TimePoint
import org.springframework.data.annotation.TypeAlias
import java.math.BigDecimal

@TypeAlias("C-VR")
class ValueReachCondition(val variable: String, val value: BigDecimal) : Condition {
    override fun check(state: TimePoint): Boolean = state[variable] >= value
}