package com.github.alexzam.entityvc.model.exporter

import com.gitlab.alexzam.entityvc.model.Model
import com.gitlab.alexzam.entityvc.model.fields.SimpleTypeAdapter
import com.gitlab.alexzam.entityvc.modelexport.ExportUtil
import com.gitlab.alexzam.entityvc.useGoodFields
import java.io.File
import java.math.BigDecimal

fun main() {
    val modelConfig: Model.() -> Unit = {
        useGoodFields()
        typeAdapters += SimpleTypeAdapter(BigDecimal::class, "string", "bigDecimal")
        typeAdapters += SimpleTypeAdapter(Boolean::class, "boolean", "boolean")
        typeAdapters += MapAdapter()
    }

    ExportUtil.exportJsFile(File("build/js/modelDesc.ts"), "com.github.alexzam.longplanner.model", modelConfig)
    ExportUtil.exportTsFile(File("build/js/model.ts"), "com.github.alexzam.longplanner.model", modelConfig)
}
