tasks.register<Exec>("npm-install") {
    commandLine = listOf("npm", "install")
    inputs.files(file("package.json"))
    outputs.dir("node_modules")
}

tasks.register<Copy>("importModel") {
    dependsOn(":back:model-exporter:makeJs")
    from("../back/model-exporter/build/js")
    into("generated")
}

tasks.register<Exec>("webpack-dev") {
    dependsOn("importModel", "npm-install")
    group = "build"
    commandLine = listOf("npm", "run", "build-dev")
    inputs.files(fileTree("src"))
    inputs.files(fileTree("node_modules"))
    outputs.dir("dist/js")
    outputs.dir("dist/css")
}

tasks.register<Exec>("webpack-prod") {
    dependsOn("importModel", "npm-install")
    group = "build"
    commandLine = listOf("npm", "run", "build")
    inputs.files(fileTree("src"))
    inputs.files(fileTree("node_modules"))
    outputs.dir("dist/js")
    outputs.dir("dist/css")
}

tasks.register<Copy>("copyStatic") {
    from("static")
    into("dist")
}

tasks.register("build") {
    dependsOn("copyStatic", "webpack-dev")
    group = "build"
}

tasks.register<Delete>("clean") {
    delete("dist", "generated")
}
