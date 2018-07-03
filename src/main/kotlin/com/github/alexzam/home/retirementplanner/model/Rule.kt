package com.github.alexzam.home.retirementplanner.model

abstract class Rule {
    abstract fun apply(oldState: TimePoint, state: TimePoint)
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