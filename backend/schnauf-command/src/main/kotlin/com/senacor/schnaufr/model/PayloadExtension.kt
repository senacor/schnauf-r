package com.senacor.schnaufr.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.lang.reflect.ParameterizedType
import java.util.*

val mapType: ParameterizedType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
val adapter: JsonAdapter<Map<String, String>> = JsonSerializer.moshi.adapter<Map<String, String>>(mapType)
val jsonMetadataMapper = { payload: Payload -> adapter.fromJson((payload as DefaultPayload).metadataUtf8)!! }

val Payload.operation: String?
    get() = jsonMetadataMapper(this)["operation"]

val Payload.principal: UUID?
    get() = jsonMetadataMapper(this)["principal"]?.let { UUID.fromString(it) }
