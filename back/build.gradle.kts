val ktor_version: String by extra
val kotlin_version: String by extra
val logback_version: String by extra

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    mainClass.set("com.github.alexzam.home.retirementplanner.AppKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.8")
    implementation("org.springframework:spring-expression:5.3.9")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

//	implementation 'com.github.vittee.kformula:kformula:1.0.3'

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
}

tasks.register<Copy>("importFront") {
    dependsOn(":front:build")
    from("../front/dist")
    into("build/resources/main/static")
}

tasks["processResources"].dependsOn("importFront")