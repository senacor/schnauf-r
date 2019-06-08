package com.senacor.schnaufr.schnauf.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.UUID

data class Schnaufr(val id: UUID, val avatarId: UUID, val username: String, val displayName: String) {

    companion object {
        fun fromJson(value: String): Schnaufr = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}