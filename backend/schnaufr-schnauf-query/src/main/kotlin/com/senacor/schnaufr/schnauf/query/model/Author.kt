package com.senacor.schnaufr.schnauf.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.UUID

data class Author(val avatarId: UUID, val username: String, val displayName: String) {
    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}