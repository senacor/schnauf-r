package com.senacor.schnaufr

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.Types
import io.rsocket.Payload
import java.util.UUID

val Payload.operation: String?
    get() = metadataMap["operation"]

val Payload.principal: UUID
    get() = if (metadataMap["principal"] != null) UUID.fromString(metadataMap["principal"]!!) else UUID.randomUUID()


val Payload.limit: Int
    get() = if(metadataMap["limit"] != null) metadataMap["limit"]!!.toInt() else -1

private val Payload.metadataMap: Map<String, String>
    get() {
        return {
            val mapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            val adapter = JsonSerializer.moshi.adapter<Map<String, String>>(mapType)
            adapter.fromJson(metadataUtf8)!!
        }()
    }