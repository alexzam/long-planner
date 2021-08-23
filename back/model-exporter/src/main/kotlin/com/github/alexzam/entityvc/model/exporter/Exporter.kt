package com.github.alexzam.entityvc.model.exporter

import com.gitlab.alexzam.entityvc.model.fields.SimpleTypeAdapter
import com.gitlab.alexzam.entityvc.modelexport.ExportUtil
import com.gitlab.alexzam.entityvc.useGoodFields
import java.io.File
import java.math.BigDecimal

fun main() {
    ExportUtil.exportJsFile(File("build/js/modelDesc.ts"), "com.github.alexzam.longplanner.model")
    {
        useGoodFields()
        typeAdapters += SimpleTypeAdapter(BigDecimal::class, "string", "bigDecimal")
    }
    ExportUtil.exportTsFile(File("build/js/model.ts"), "com.github.alexzam.longplanner.model")
    {
        useGoodFields()
        typeAdapters += SimpleTypeAdapter(BigDecimal::class, "string", "bigDecimal")
    }
}
