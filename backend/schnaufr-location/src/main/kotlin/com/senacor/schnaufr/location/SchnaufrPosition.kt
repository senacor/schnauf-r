package com.senacor.schnaufr.location

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.util.*

data class SchnaufrPosition(val id: UUID, val geolocation: Geolocation) {

    companion object {
        fun fromJson(value: String): SchnaufrPosition = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}