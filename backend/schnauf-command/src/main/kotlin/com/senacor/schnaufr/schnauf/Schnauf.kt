package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.aUUID
import com.senacor.schnaufr.model.CreateSchnaufRequest
import com.senacor.schnaufr.serialization.JsonSerializer
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import java.util.*

data class Schnauf(val id: UUID, val title: String, val submitter: UUID, val recipients: List<UUID> = listOf()) {

    companion object {
        fun fromRequest(request: CreateSchnaufRequest): Schnauf = Schnauf(aUUID(), request.title, request.submitter, request.recipients)
        private fun fromJson(value: String): Schnauf = JsonSerializer.fromJson(value)
        fun fromPayload(payload: Payload): Schnauf = fromJson(payload.dataUtf8)
    }

    private fun toJson(): String = JsonSerializer.toJsonString(this)

    fun asPayload(): Payload {
        return DefaultPayload.text(toJson())
    }
}
