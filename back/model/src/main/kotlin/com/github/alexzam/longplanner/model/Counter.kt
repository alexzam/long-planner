package com.github.alexzam.longplanner.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Counter(
    @SerialName("_id")
    val id: String,
    val value: Long
)