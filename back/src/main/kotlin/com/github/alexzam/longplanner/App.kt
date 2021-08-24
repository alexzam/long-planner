package com.github.alexzam.longplanner

import com.example.plugins.configureHTTP
import com.example.plugins.configureSerialization
import com.github.alexzam.longplanner.plugins.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class App

fun main() {
    // "Config"
    val port = System.getenv("PORT")?.toInt() ?: 8080

    // "DI"
    val calcService = CalcService()
    val storageService = StorageService()
    val planningService = PlanningService(calcService, storageService)

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        configureRouting(storageService, planningService)
        configureHTTP()
        configureSerialization()
    }.start(wait = true)
}