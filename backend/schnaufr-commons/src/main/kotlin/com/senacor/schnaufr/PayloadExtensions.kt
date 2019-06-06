package com.senacor.schnaufr

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.Types
import io.rsocket.Payload

val Payload.operation: String?
    get() = metadataMap["operation"]

val Payload.principal: String?
    get() = metadataMap["principal"]

private val Payload.metadataMap: Map<String, String>
    get() {
        return {
            val mapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            val adapter = JsonSerializer.moshi.adapter<Map<String, String>>(mapType)
            adapter.fromJson(metadataUtf8)!!
        }()
    }