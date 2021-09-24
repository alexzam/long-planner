package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.TimePoint
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.time.LocalDate

class TimepointsDao(db: CoroutineDatabase, private val counterDao: CounterDao) {
    private val timePoints = db.getCollection<TimePoint>()

    suspend fun makeNew(planId: Long, date: LocalDate): TimePoint =
        TimePoint(counterDao.getCounterValue("timepoint"), planId, date)

    private data class Result(
        val isPreset: Boolean,
        val isCalc: Boolean,
        val minDate: LocalDate,
        val maxDate: LocalDate,
        val num: Int
    )

    suspend fun getStats(planId: Long) {
        val results = timePoints.aggregate<Result>(
            listOf(
                project(
                    TimePoint::date to 1,
                    Result::isPreset to (TimePoint::presetValues ne null),
                    Result::isCalc to (TimePoint::values ne null)
                ),
                group(
                    fields(Result::isPreset from Result::isPreset, Result::isCalc from Result::isCalc),
                    Result::minDate min TimePoint::date,
                    Result::maxDate max TimePoint::date,
                    Result::num sum 1
                )
            )
        ).toList()
    }
}