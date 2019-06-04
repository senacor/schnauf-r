package com.senacor.schnaufr.model

import com.senacor.schnaufr.serialization.JsonSerializer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RSocketMetadata(val operation: String) {

    companion object {
        fun fromJson(value: String): RSocketMetadata = JsonSerializer.fromJson(value)
    }
}
