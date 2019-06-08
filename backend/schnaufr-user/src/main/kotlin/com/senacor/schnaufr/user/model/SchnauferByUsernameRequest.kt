package com.senacor.schnaufr.user.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import java.util.UUID

@JsonClass(generateAdapter = true)
data class SchnauferByUsernameRequest(val username: String) {

    companion object {
        fun fromJson(value: String): SchnauferByUsernameRequest = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)
}