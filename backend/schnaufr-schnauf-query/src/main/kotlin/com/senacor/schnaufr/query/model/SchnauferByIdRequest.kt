package com.senacor.schnaufr.query.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.UUID

@JsonClass(generateAdapter = true)
data class SchnauferByIdRequest(val id: UUID) {

    companion object {
        fun fromJson(value: String): SchnauferByIdRequest = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)
}