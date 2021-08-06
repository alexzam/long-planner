package com.example.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json

val frontendJson = Json {
    encodeDefaults = true
    isLenient = true
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(frontendJson)
    }
}
