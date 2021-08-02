package com.github.alexzam.home.retirementplanner.model.conditions

import com.github.alexzam.home.retirementplanner.model.TimePoint

interface Condition {
    fun check(state: TimePoint): Boolean
}