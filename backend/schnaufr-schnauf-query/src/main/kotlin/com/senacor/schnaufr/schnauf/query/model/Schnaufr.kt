package com.senacor.schnaufr.schnauf.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.util.*

@JsonClass(generateAdapter = true)
data class Schnaufr(val id: UUID, val avatarId: UUID, val username: String, val displayName: String) {

    companion object {
        fun fromJson(value: String): Schnaufr = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}