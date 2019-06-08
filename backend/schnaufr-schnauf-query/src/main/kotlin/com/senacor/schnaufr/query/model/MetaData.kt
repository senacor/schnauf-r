package com.senacor.schnaufr.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.*

data class MetaData(val operation: String, val limit: Int?, val principal: UUID?) {
    companion object {
        fun fromJson(value: String): MetaData = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}