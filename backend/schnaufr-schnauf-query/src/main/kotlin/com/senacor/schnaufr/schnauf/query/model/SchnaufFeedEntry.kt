package com.senacor.schnaufr.schnauf.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.UUID

data class SchnaufFeedEntry(val id: UUID, val title: String, val author: Author) {

    companion object {
        fun fromJson(value: String): SchnaufFeedEntry = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}