package com.github.alexzam.entityvc.model.exporter

import com.gitlab.alexzam.entityvc.model.fields.TypeAdapter
import com.gitlab.alexzam.entityvc.model.fields.TypeResolver
import kotlin.reflect.KType

class MapAdapter : TypeAdapter() {
    override fun getTsType(type: KType, typeResolver: TypeResolver): String? =
        ifApplicable(type, Map::class) {
            val keyType = type.arguments[0]
                .type
                ?.let { typeResolver(it) }

            val valType = type.arguments[1]
                .type
                ?.let { typeResolver(it) }

            if (keyType != null && valType != null) "{ [key: $keyType]: $valType }" else null
        }
}