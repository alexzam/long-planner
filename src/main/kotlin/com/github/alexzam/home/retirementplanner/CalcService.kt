package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.TimePoint
import com.github.alexzam.home.retirementplanner.model.Var
import org.springframework.expression.EvaluationContext
import org.springframework.expression.PropertyAccessor
import org.springframework.expression.TypedValue
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.SimpleEvaluationContext
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class CalcService {
    private val parser = SpelExpressionParser()

    fun calculateVar(variable: Var, prevTimePoint: TimePoint?, currentTimePoint: TimePoint): BigDecimal {
        val evaluationContext = SimpleEvaluationContext.Builder(TimepointPropertyAccessor)
            .withRootObject(currentTimePoint)
            .build()
            .apply {
                setVariable("prev", prevTimePoint)
            }

        val ret = parser.parseExpression(variable.expression)
            .getValue(evaluationContext)

        val bdRet = when(ret){
            is BigDecimal -> ret
            is Int -> BigDecimal.valueOf(ret.toLong())
            is Long -> BigDecimal.valueOf(ret)
            null -> null
            else -> throw IllegalArgumentException("Unknown return type ${ret::class.qualifiedName}")
        }

        return bdRet ?: BigDecimal.ZERO
    }

    fun getDependencies(variable: Var) : Set<String> {
        val point = TimePoint(LocalDate.MIN, mutableMapOf(), BigDecimal.ZERO, mutableListOf())
        val loggingPropertyAccessor = LoggingPropertyAccessor()

        val evaluationContext = SimpleEvaluationContext.Builder(loggingPropertyAccessor)
            .withRootObject(point)
            .build()
            .apply {
                setVariable("prev", point)
            }

        parser.parseExpression(variable.expression)
            .getValue(evaluationContext)

        return loggingPropertyAccessor.accesses
    }
}

object TimepointPropertyAccessor : PropertyAccessor {
    override fun getSpecificTargetClasses(): Array<Class<*>> =
        arrayOf(TimePoint::class.java)

    override fun canRead(context: EvaluationContext, target: Any?, name: String): Boolean = true

    override fun read(context: EvaluationContext, target: Any?, name: String): TypedValue {
        if(target == null) return TypedValue.NULL
        val point = target as TimePoint

        return TypedValue(point[name])
    }

    override fun canWrite(context: EvaluationContext, target: Any?, name: String): Boolean = false

    override fun write(context: EvaluationContext, target: Any?, name: String, newValue: Any?) = Unit
}

class LoggingPropertyAccessor : PropertyAccessor {
    val accesses = mutableSetOf<String>()

    override fun getSpecificTargetClasses(): Array<Class<*>> =
        arrayOf(TimePoint::class.java)

    override fun canRead(context: EvaluationContext, target: Any?, name: String): Boolean = true

    override fun read(context: EvaluationContext, target: Any?, name: String): TypedValue {
        accesses += name
        return TypedValue.NULL
    }

    override fun canWrite(context: EvaluationContext, target: Any?, name: String): Boolean = false

    override fun write(context: EvaluationContext, target: Any?, name: String, newValue: Any?) = Unit
}