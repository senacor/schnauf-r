package com.senacor.schnaufr.schnauf.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.util.*


// author: User / ID and probably geoloaction or something like that
data class Schnauf(val id: UUID, val submitter: UUID, val title: String) {
    companion object {
        fun fromJson(value: String): Schnauf = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}