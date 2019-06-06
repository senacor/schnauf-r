package com.senacor.schnaufr.serialization

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Instant

internal object InstantAdapter {
    @ToJson
    fun toJson(instant: Instant): String {
        return instant.toString()
    }

    @FromJson
    fun fromJson(string: String): Instant {
        return Instant.parse(string)
    }
}