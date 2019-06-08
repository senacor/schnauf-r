package com.senacor.schnaufr.gateway.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.UUID

// author: User / ID and probably geoloaction or something like that
data class Schnauf(val id: UUID, val submitter: UUID, val title: String) {
    companion object {
        fun fromJson(value: String): Schnauf = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}