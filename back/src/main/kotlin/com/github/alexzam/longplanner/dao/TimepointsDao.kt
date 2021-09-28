package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.TimepointStatItem
import org.intellij.lang.annotations.Language
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.math.BigDecimal
import java.time.LocalDate

class TimepointsDao(db: CoroutineDatabase, private val counterDao: CounterDao) {
    private val timePoints = db.getCollection<TimePoint>()

    suspend fun makeNew(planId: Long, date: LocalDate): TimePoint =
        TimePoint(counterDao.getCounterValue("timepoint"), planId, date)

    suspend fun getStats(planId: Long): List<TimepointStatItem> {
        val results = timePoints.aggregate<TimepointStatItem>(
            listOf(
                match(
                    TimePoint::planId eq planId
                ),
                project(
                    TimePoint::date from 1,
                    TimepointStatItem::isPreset from (MongoOperator.ne from listOf(
                        TimePoint::presetValues,
                        mutableMapOf<Int, BigDecimal>()
                    )),
                    TimepointStatItem::isCalc from (MongoOperator.ne from listOf(
                        TimePoint::values,
                        mutableMapOf<Int, BigDecimal>()
                    ))
                ),
                group(
                    fields(
                        TimepointStatItem::isPreset from TimepointStatItem::isPreset,
                        TimepointStatItem::isCalc from TimepointStatItem::isCalc
                    ),
                    TimepointStatItem::minDate min TimePoint::date,
                    TimepointStatItem::maxDate max TimePoint::date,
                    TimepointStatItem::num sum 1
                ),
                project(
                    TimepointStatItem::isPreset from TimePoint::id / TimepointStatItem::isPreset,
                    TimepointStatItem::isCalc from TimePoint::id / TimepointStatItem::isCalc,
                    TimepointStatItem::minDate from 1,
                    TimepointStatItem::maxDate from 1,
                    TimepointStatItem::num from 1
                )
            )
        ).toList()

        return results
    }

    suspend fun addOrGet(planId: Long, date: LocalDate): TimePoint {
        @Language("JSON")
        val existing = timePoints.findOne("{\"planId\": $planId, \"date\": \"$date\"}")
        return existing ?: makeNew(planId, date).also { timePoints.insertOne(it) }
    }
}