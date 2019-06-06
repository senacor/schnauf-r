package com.senacor.schnaufr.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class CreateSchnaufRequest(val title: String, val submitter: UUID, val recipients: List<UUID> = listOf()) {

    companion object {
        fun fromJson(value: String): CreateSchnaufRequest = JsonSerializer.fromJson(value)
    }

    fun toJson(): String = JsonSerializer.toJsonString(this)
}
