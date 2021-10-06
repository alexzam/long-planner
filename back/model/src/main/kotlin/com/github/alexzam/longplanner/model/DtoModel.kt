@file:UseSerializers(LocalDateSerializer::class)

package com.github.alexzam.longplanner.model

import com.github.alexzam.longplanner.model.serialization.LocalDateSerializer
import com.gitlab.alexzam.entityvc.model.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate

@Serializable
@Entity
data class ShortPlan(
    @SerialName("_id")
    val id: Long,
    val name: String
)

@Serializable
sealed class TimePointListItem

@Serializable
@Entity
data class TimePointShort(
    val id: Long,
    val date: LocalDate,
    val valueNum: Int,
    val eventNum: Int
) : TimePointListItem()

fun TimePoint.toShort(): TimePointShort =
    TimePointShort(id, date, presetValues.size + values.size, events.size)

@Serializable
@Entity
data class TimepointStatItem(
    val isPreset: Boolean,
    val isCalc: Boolean,
    val minDate: LocalDate,
    val maxDate: LocalDate,
    val num: Int
)

@Serializable
@Entity
data class TimepointWithPrev(
    val cur: TimePoint,
    val prev: TimePoint?
)