package com.senacor.schnaufr.model

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.*

data class Schnauf(val id: UUID, val title: String, val submitter: UUID, val recipients: List<UUID> = listOf()) {

    companion object {
        fun fromRequest(request: CreateSchnaufRequest): Schnauf = Schnauf(UUID(), request.title, request.submitter, request.recipients)
        private fun fromJson(value: String): Schnauf = JsonSerializer.fromJson(value)
        fun fromPayload(payload: Payload): Schnauf = fromJson(payload.dataUtf8)
    }

    private fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.create(toJson())
    }
}
