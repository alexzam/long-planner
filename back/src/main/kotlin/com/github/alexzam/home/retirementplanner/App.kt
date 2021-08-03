package com.github.alexzam.home.retirementplanner

import com.example.plugins.configureHTTP
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class App

fun main() {
    // "Config"
    val port = System.getenv("PORT")?.toInt() ?: 8080

    // "DI"
    val calcService = CalcService()
    val planningService = PlanningService(calcService)
    val storageService = StorageService()

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureSerialization()
    }.start(wait = true)
}