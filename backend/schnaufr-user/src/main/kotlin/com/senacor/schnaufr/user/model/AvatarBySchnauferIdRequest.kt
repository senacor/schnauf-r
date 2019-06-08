package com.senacor.schnaufr.user.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class AvatarBySchnauferIdRequest(val id: UUID) {

    companion object {
        fun fromJson(value: String): AvatarBySchnauferIdRequest = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)
}