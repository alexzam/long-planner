tasks.register<Exec>("npm-install") {
    commandLine = listOf("npm", "install")
    inputs.files(file("package.json"))
    outputs.dir("node_modules")
}

tasks.register<Exec>("build") {
    dependsOn(":back:model-exporter:makeJs", "npm-install")
    commandLine = listOf("npm", "run", "build-dev")
    inputs.files(fileTree("src"))
    inputs.files(fileTree("node_modules"))
    outputs.dir("dist/build")
}

tasks.register<Delete>("clean") {
    delete("dist")
}
