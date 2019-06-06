package com.senacor.schnaufr.location

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.time.Instant

data class Geolocation(val latitude: Double,
                       val longitude: Double,
                       val timestamp: Instant) {
    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}