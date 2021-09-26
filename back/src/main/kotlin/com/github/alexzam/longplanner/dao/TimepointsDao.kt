@file:UseSerializers(LocalDateSerializer::class)

package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.math.BigDecimal
import java.time.LocalDate

class TimepointsDao(db: CoroutineDatabase, private val counterDao: CounterDao) {
    private val timePoints = db.getCollection<TimePoint>()

    suspend fun makeNew(planId: Long, date: LocalDate): TimePoint =
        TimePoint(counterDao.getCounterValue("timepoint"), planId, date)

    @Serializable
    data class Result(
        val isPreset: Boolean,
        val isCalc: Boolean,
        val minDate: LocalDate,
        val maxDate: LocalDate,
        val num: Int
    )

    suspend fun getStats(planId: Long): List<Result> {
        val results = timePoints.aggregate<Result>(
            listOf(
                match(
                    TimePoint::planId eq planId
                ),
                project(
                    TimePoint::date from 1,
                    TimepointsDao.Result::isPreset from (MongoOperator.ne from listOf(
                        TimePoint::presetValues,
                        mutableMapOf<Int, BigDecimal>()
                    )),
                    TimepointsDao.Result::isCalc from (MongoOperator.ne from listOf(
                        TimePoint::values,
                        mutableMapOf<Int, BigDecimal>()
                    ))
                ),
                group(
                    fields(Result::isPreset from Result::isPreset, Result::isCalc from Result::isCalc),
                    Result::minDate min TimePoint::date,
                    Result::maxDate max TimePoint::date,
                    Result::num sum 1
                ),
                project(
                    Result::isPreset from TimePoint::id / Result::isPreset,
                    Result::isCalc from TimePoint::id / Result::isCalc,
                    Result::minDate from 1,
                    Result::maxDate from 1,
                    Result::num from 1
                )
            )
        ).toList()

        return results
    }
}