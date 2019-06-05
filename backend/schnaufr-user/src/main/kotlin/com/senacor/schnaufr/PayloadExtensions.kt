package com.senacor.schnaufr

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.Types
import io.rsocket.Payload

val Payload.operation: String?
    get() = {
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val adapter = JsonSerializer.moshi.adapter<Map<String, String>>(mapType)
        val json = adapter.fromJson(metadataUtf8)!!
        json["operation"]
    }()
