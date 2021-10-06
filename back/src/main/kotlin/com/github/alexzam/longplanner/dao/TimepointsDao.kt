package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.TimepointStatItem
import io.ktor.features.*
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

    suspend fun getById(timepointId: Long): TimePoint =
        timePoints.findOneById(timepointId) ?: throw NotFoundException("Timepoint $timepointId not found")

    suspend fun getPrevOf(planId: Long, date: LocalDate): TimePoint? =
        timePoints.find(
            TimePoint::planId eq planId,
            TimePoint::date lt date
        )
            .sort(descending(TimePoint::date))
            .limit(1)
            .first()

    suspend fun savePresetValue(timepointId: Long, varId: Long, value: BigDecimal) {
        timePoints.updateOneById(timepointId, "{'\$set':{'presetValues.$varId': '$value'}}")
    }
}