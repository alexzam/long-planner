package com.github.alexzam.home.retirementplanner.model.rules

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.conditions.Condition
import org.springframework.data.annotation.TypeAlias
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.math.BigDecimal

@TypeAlias("R-C")
data class ChangeRule(val expression: String,
                      val variable: String,
                      override val id: Long,
                      override val condition: Condition?) : Rule() {
    companion object {
        val parser = SpelExpressionParser()
    }

    override fun apply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String> {
        val value = parser.parseExpression(expression).getValue(state) as BigDecimal
        state[variable] = value
        return listOf()
    }
}