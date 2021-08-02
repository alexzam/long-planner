package com.github.alexzam.entityvc.model.exporter

import com.gitlab.alexzam.entityvc.modelexport.ExportUtil
import com.gitlab.alexzam.entityvc.modelexport.TsGenerator
import com.gitlab.alexzam.entityvc.useGoodfields
import java.io.File

fun main(args: Array<String>) {
    ExportUtil.cliExport(args)

    val generator = TsGenerator()
        .apply { useGoodfields() }

    ExportUtil.exportToFile(File("build/js/model.ts"), args[0], generator)
}
