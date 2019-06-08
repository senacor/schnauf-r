package com.senacor.schnaufr.user.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class AvatarByIdRequest(val id: UUID) {

    companion object {
        fun fromJson(value: String): AvatarByIdRequest = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)
}