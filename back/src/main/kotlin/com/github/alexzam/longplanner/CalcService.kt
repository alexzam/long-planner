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

    fun calculateVar(
        variable: Var, prevTimePoint: TimePoint?, currentTimePoint: TimePoint,
        prevMonthPoint: TimePoint? = null
    ): BigDecimal {
        val evaluationContext = SimpleEvaluationContext.Builder(TimepointPropertyAccessor)
            .withRootObject(currentTimePoint)
            .build()
            .apply {
                setVariable("prev", prevTimePoint)
            }

        prevMonthPoint?.also {
            evaluationContext.setVariable("prevmon", it)
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

    fun getDependencies(variable: Var): Set<Int> {
        val point = TimePoint(0, 0, LocalDate.MIN)
        val loggingPropertyAccessor = LoggingPropertyAccessor()

        val evaluationContext = SimpleEvaluationContext.Builder(loggingPropertyAccessor)
            .withRootObject(point)
            .build()
            .apply {
                val copy = point.copy(date = LocalDate.MAX)
                setVariable("prev", copy)
                setVariable("prevmon", copy)
            }

        parser.parseExpression(variable.expression)
            .getValue(evaluationContext)

        return loggingPropertyAccessor.accesses
    }
}

object TimepointPropertyAccessor : PropertyAccessor {
    override fun getSpecificTargetClasses(): Array<Class<*>> =
        arrayOf(TimePoint::class.java)

    override fun canRead(context: EvaluationContext, target: Any?, name: String): Boolean =
        target != null
                && target is TimePoint
                && name.startsWith("id")


    override fun read(context: EvaluationContext, target: Any?, name: String): TypedValue {
        if (target == null) return TypedValue.NULL
        val point = target as TimePoint

        val varId = name.drop(2).toIntOrNull() ?: return TypedValue.NULL

        return TypedValue(point[varId])
    }

    override fun canWrite(context: EvaluationContext, target: Any?, name: String): Boolean = false

    override fun write(context: EvaluationContext, target: Any?, name: String, newValue: Any?) = Unit
}

class LoggingPropertyAccessor : PropertyAccessor {
    val accesses = mutableSetOf<Int>()

    override fun getSpecificTargetClasses(): Array<Class<*>> =
        arrayOf(TimePoint::class.java)

    override fun canRead(context: EvaluationContext, target: Any?, name: String): Boolean =
        name.startsWith("id")

    override fun read(context: EvaluationContext, target: Any?, name: String): TypedValue {
        if ((target as TimePoint).date == LocalDate.MIN) accesses += name.drop(2).toInt()
        return TypedValue(BigDecimal.ONE)
    }

    override fun canWrite(context: EvaluationContext, target: Any?, name: String): Boolean = false

    override fun write(context: EvaluationContext, target: Any?, name: String, newValue: Any?) = Unit
}