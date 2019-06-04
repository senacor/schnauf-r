package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.serialization.JsonSerializer
import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.model.CreateSchnaufRequest
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.util.*

data class Schnauf(val id: UUID, val title: String, val submitter: String) {

    companion object {
        fun fromRequest(request: CreateSchnaufRequest): Schnauf = Schnauf(UUID(), request.title, request.submitter)
        fun fromJson(value: String): Schnauf = JsonSerializer.fromJson(value)
        fun fromPayload(payload: Payload): Schnauf = fromJson(payload.dataUtf8)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}
