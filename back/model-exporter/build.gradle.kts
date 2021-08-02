plugins {
    kotlin("jvm")
    `java-library`
}

repositories {
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/alexzzam/personal")
    }
}

java.sourceCompatibility = JavaVersion.VERSION_11

val entityvcVer: String by rootProject.extra

dependencies {
//    implementation(project(":model"))
    implementation("com.gitlab.alexzam.entityvc:entityvc-model:$entityvcVer")
    implementation("com.gitlab.alexzam.entityvc:entityvc-model-export:$entityvcVer")
    implementation("com.gitlab.alexzam.entityvc:entityvc-goodfields:$entityvcVer")
}

val makeJs = tasks.create("makeJs", JavaExec::class) {
    dependsOn("assemble")
    group = "build"
    outputs.files(file("build/js/modelDesc.ts"))

    classpath = sourceSets.main.get().runtimeClasspath
    main = "com.github.alexzam.entityvc.model.exporter.ExporterKt"
    args("com.github.alexzam.entityvc.model", file("build/js/modelDesc.ts").absolutePath)

    doFirst {
        mkdir(file("build/js"))
    }
}

tasks.getByName("build").dependsOn(makeJs)
