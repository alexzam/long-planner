package com.github.alexzam.longplanner.model.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Period

class PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Period", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Period =
        Period.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Period) =
        encoder.encodeString(value.toString())
}