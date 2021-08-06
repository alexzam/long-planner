package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.Var
import org.springframework.expression.EvaluationContext
import org.springframework.expression.PropertyAccessor
import org.springframework.expression.TypedValue
import org.springframework.expression.spel.SpelEvaluationException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.SimpleEvaluationContext
import java.math.BigDecimal
import java.time.LocalDate

class CalcService {
    private val parser = SpelExpressionParser()

    fun calculateVar(variable: Var, prevTimePoint: TimePoint?, currentTimePoint: TimePoint): BigDecimal {
        val evaluationContext = SimpleEvaluationContext.Builder(TimepointPropertyAccessor)
            .withRootObject(currentTimePoint)
            .build()
            .apply {
                setVariable("prev", prevTimePoint)
            }

        val ret = try {
            parser.parseExpression(variable.expression)
                .getValue(evaluationContext)
        } catch (e: SpelEvaluationException) {
            variable.initialValue
        } catch (e: ArithmeticException) {
            variable.initialValue
        }

        val bdRet = when (ret) {
            is BigDecimal -> ret
            is Int -> BigDecimal.valueOf(ret.toLong())
            is Long -> BigDecimal.valueOf(ret)
            null -> null
            else -> throw IllegalArgumentException("Unknown return type ${ret::class.qualifiedName}")
        }

        return bdRet ?: variable.initialValue
    }

    fun getDependencies(variable: Var): Set<String> {
        val point = TimePoint(LocalDate.MIN, mutableMapOf(), mutableListOf())
        val loggingPropertyAccessor = LoggingPropertyAccessor()

        val evaluationContext = SimpleEvaluationContext.Builder(loggingPropertyAccessor)
            .withRootObject(point)
            .build()
            .apply {
                setVariable("prev", point.copy(date = LocalDate.MAX))
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
        if (target == null) return TypedValue.NULL
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
        if ((target as TimePoint).date == LocalDate.MIN) accesses += name
        return TypedValue(BigDecimal.ONE)
    }

    override fun canWrite(context: EvaluationContext, target: Any?, name: String): Boolean = false

    override fun write(context: EvaluationContext, target: Any?, name: String, newValue: Any?) = Unit
}