package com.senacor.schnaufr.location

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.*

@JsonClass(generateAdapter = true)
data class SchnaufrLocation(val id: UUID, val currentLocation: Geolocation) {

    companion object {
        fun fromJson(value: String): SchnaufrLocation = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}