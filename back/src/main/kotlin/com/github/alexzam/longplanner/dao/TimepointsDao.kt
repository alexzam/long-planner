package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.TimePoint
import com.github.alexzam.longplanner.model.TimePointPage
import com.github.alexzam.longplanner.model.TimepointStatItem
import com.github.alexzam.longplanner.model.toShort
import com.mongodb.client.model.Filters
import io.ktor.features.*
import org.intellij.lang.annotations.Language
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.math.BigDecimal
import java.time.LocalDate

class TimepointsDao(db: CoroutineDatabase, private val counterDao: CounterDao) {
    private val timePoints = db.getCollection<TimePoint>()

    suspend fun makeNew(planId: Long, date: LocalDate): TimePoint =
        TimePoint(generateId(), planId, date)

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

    suspend fun getPresetPoints(planId: Long): List<TimePoint> =
        timePoints.find(TimePoint::planId eq planId, TimePoint::presetValues ne mapOf())
            .toList()

    suspend fun replacePoints(planId: Long, points: List<TimePoint>) {
        timePoints.deleteMany(TimePoint::planId eq planId)
        timePoints.insertMany(points)
    }

    suspend fun getLastDate(planId: Long): LocalDate? =
        timePoints.find(TimePoint::planId eq planId)
            .sort(descending(TimePoint::date))
            .limit(1)
            .first()
            ?.date

    suspend fun getPage(
        planId: Long,
        from: LocalDate,
        to: LocalDate,
        isCalc: Boolean?,
        isPreset: Boolean?,
        size: Int
    ): TimePointPage {
        val filters = listOfNotNull(
            TimePoint::planId eq planId,
            Filters.gte("date", from.toString()),
            Filters.lte("date", to.toString()),
            isCalc?.let { if (it) TimePoint::values ne mutableMapOf() else TimePoint::values eq mutableMapOf() },
            isPreset?.let { if (it) TimePoint::presetValues ne mutableMapOf() else TimePoint::presetValues eq mutableMapOf() }
        )
            .toTypedArray()
        val points = timePoints.find(*filters)
            .sort(ascending(TimePoint::date))
            .limit(size + 1)
            .toList()

        val nextDate = if (points.size > size) points.last().date else null
        val items = if (points.size > size) points.dropLast(1) else points
        return TimePointPage(items.map { it.toShort() }, nextDate)
    }

    private suspend fun generateId() = counterDao.getCounterValue("timepoint")
}