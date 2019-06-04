package com.senacor.schnaufr.schnauf.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.util.*

data class SchnaufFeedEntry(val id: UUID, val title: String, val author: Author) {

    companion object {
        fun fromJson(value: String): SchnaufFeedEntry = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}