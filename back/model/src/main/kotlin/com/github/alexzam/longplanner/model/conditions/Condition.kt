package com.github.alexzam.longplanner.model.conditions

import com.github.alexzam.longplanner.model.TimePoint

interface Condition {
    fun check(state: TimePoint): Boolean
}