package com.senacor.schnaufr.location

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.time.Instant

data class Geolocation(val latitude: Double,
                       val longitude: Double,
                       val timestamp: Instant) {
    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}