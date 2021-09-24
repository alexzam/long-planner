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
data class TimePack(
    @Field
    val number: Int,
    @Field
    val start: LocalDate,
    @Field
    val end: LocalDate
) : TimePointListItem()

@Serializable
@Entity
data class TimePointShort(
    @Field
    val date: LocalDate,
    @Field
    val valueNum: Int,
    @Field
    val eventNum: Int
) : TimePointListItem()