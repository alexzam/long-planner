package com.github.alexzam.longplanner.model.rules

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.conditions.Condition
import kotlinx.serialization.Serializable

@Serializable
abstract class Rule(var enabled: Boolean = true) {
    abstract val id: Long
    abstract val condition: Condition?

    fun doApply(oldState: TimePoint?, state: TimePoint, rules: List<Rule>): List<String> {
        if (!enabled) return listOf()

        if (condition?.check(state) == false) return listOf()

        return apply(oldState, state, rules)
    }

    abstract fun apply(oldState: TimePoint?, state: TimePoint, rules: List<Rule>): List<String>
}

//
//class MultiPercentRule(percents: Map<BigDecimal, BigDecimal>) : Rule {
//    private val thresholds = percents.keys.sorted()
//    private val rules = percents.mapValues { entry -> PercentRule(entry.value) }
//
//    override val type: String
//        get() = "multipercent"
//
//    override fun apply(oldState: AccountState): AccountState {
//        val value = oldState.value
//        val threshold = thresholds.findLast { it < value }
//        return rules[threshold]?.apply(oldState) ?: oldState
//    }
//}