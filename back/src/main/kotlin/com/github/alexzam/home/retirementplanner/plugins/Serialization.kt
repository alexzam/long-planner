package com.example.plugins

import io.ktor.application.*
import io.ktor.features.*
import kotlinx.serialization.json.Json

val frontendJson = Json {
    encodeDefaults = true
    isLenient = true
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        frontendJson
    }
}
