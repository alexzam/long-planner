@file:UseSerializers(LocalDateSerializer::class)

package com.github.alexzam.longplanner.model

import com.github.alexzam.longplanner.model.serialization.LocalDateSerializer
import com.gitlab.alexzam.entityvc.model.Entity
import com.gitlab.alexzam.entityvc.model.Field
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate

@Serializable
@Entity
data class ShortPlan(
    @Field
    @SerialName("_id")
    val id: Long,
    @Field
    val name: String
)

@Serializable
sealed class TimePointListItem

@Serializable
@Entity
data class TimePointShort(
    @Field
    val id: Long,
    @Field
    val date: LocalDate,
    @Field
    val valueNum: Int,
    @Field
    val eventNum: Int
) : TimePointListItem()

fun TimePoint.toShort(): TimePointShort =
    TimePointShort(id, date, presetValues.size + values.size, events.size)

@Serializable
@Entity
data class TimepointStatItem(
    @Field
    val isPreset: Boolean,
    @Field
    val isCalc: Boolean,
    @Field
    val minDate: LocalDate,
    @Field
    val maxDate: LocalDate,
    @Field
    val num: Int
)