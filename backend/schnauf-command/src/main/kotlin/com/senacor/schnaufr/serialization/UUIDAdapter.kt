package com.senacor.schnaufr.serialization

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.UUID

object UUIDAdapter {
    @ToJson
    fun toJson(uuid: UUID): String {
        return uuid.toString()
    }

    @FromJson
    fun fromJson(string: String): UUID {
        return UUID.fromString(string)
    }
}