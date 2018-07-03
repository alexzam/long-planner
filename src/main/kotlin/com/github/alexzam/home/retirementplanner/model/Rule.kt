package com.github.alexzam.home.retirementplanner.model

abstract class Rule(var enabled: Boolean = true) {
    abstract val id: Long

    fun doApply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String> {
        if (!enabled) return listOf()
        return apply(oldState, state, rules)
    }

    abstract fun apply(oldState: TimePoint, state: TimePoint, rules: MutableList<Rule>): List<String>
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