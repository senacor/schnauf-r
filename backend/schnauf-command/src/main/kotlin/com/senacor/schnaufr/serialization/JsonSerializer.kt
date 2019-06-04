package com.senacor.schnaufr.serialization

import com.squareup.moshi.Moshi
import org.slf4j.LoggerFactory

object JsonSerializer {

    val logger = LoggerFactory.getLogger("JsonSerializer")

    val moshi: Moshi = Moshi.Builder().add(UUIDAdapter).build()

    inline fun <reified T> fromJson(json: String): T {
        if (json.isBlank()) {
            throw IllegalArgumentException("body is blank")
        }
        try {
            val adapter = adapter<T>()
            val result: T? = adapter.fromJson(json)
            if (result == null) {
                logger.error("failed to deserialize into class '{}'. body: '{}'", T::class, json)
                throw JsonSerializationException("failed to deserialize into class ${T::class}")
            }
            return result
        } catch (ex: Exception) {
            logger.error("failed to deserialize into class '{}'. body: '{}'", ex, T::class, json)
            throw JsonSerializationException(ex)
        }
    }

    inline fun <reified T> adapter() = moshi.adapter<T>(T::class.java)

    inline fun <reified T> toJsonString(entity: T): String {
        try {
            val adapter = moshi.adapter<T>(T::class.java)
            return adapter.toJson(entity)
        } catch (ex: Exception) {
            logger.error("failed to serialize {} to JSON. entity: {}", ex, T::class, entity)
            throw JsonSerializationException(ex)
        }
    }
}
