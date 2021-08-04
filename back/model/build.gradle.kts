plugins {
    kotlin("jvm")
    `java-library`
    id("org.jetbrains.kotlin.plugin.serialization")
}

java.sourceCompatibility = JavaVersion.VERSION_11

val entityvcVer: String by rootProject.extra

dependencies {
    api("com.gitlab.alexzam.entityvc:entityvc-model:$entityvcVer")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
}
