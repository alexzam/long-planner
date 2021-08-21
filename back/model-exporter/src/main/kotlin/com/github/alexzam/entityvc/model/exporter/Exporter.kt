package com.github.alexzam.entityvc.model.exporter

import com.gitlab.alexzam.entityvc.modelexport.ExportUtil
import com.gitlab.alexzam.entityvc.useGoodFields
import java.io.File

fun main() {
    ExportUtil.exportJsFile(File("build/js/modelDesc.ts"), "com.github.alexzam.longplanner.model")
    { useGoodFields() }
    ExportUtil.exportTsFile(File("build/js/model.ts"), "com.github.alexzam.longplanner.model")
    { useGoodFields() }
}
