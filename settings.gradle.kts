pluginManagement {
    plugins {
        val kotlin_version: String by settings

        kotlin("jvm") version kotlin_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
    }
}

rootProject.name = "long-planner"
include("back", "back:model-exporter", "front")
