package com.github.alexzam.home.retirementplanner.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.math.BigDecimal

@TypeAlias("R-C")
data class ChangeRule(val expression: String, val variable: String) : Rule {
    companion object {
        val parser = SpelExpressionParser()
    }

    override fun apply(oldState: TimePoint, state: TimePoint) {
        val value = parser.parseExpression(expression).getValue(state) as BigDecimal
        state[variable] = value
    }
}