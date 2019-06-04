package com.senacor.schnaufr.user

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import io.rsocket.kotlin.*
import java.util.UUID

@JsonClass(generateAdapter = true)
data class Schnaufer(val id: UUID, val avatarId: UUID, val username: String, val displayName: String) {

    companion object {
        fun fromJson(value: String): Schnaufer = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}