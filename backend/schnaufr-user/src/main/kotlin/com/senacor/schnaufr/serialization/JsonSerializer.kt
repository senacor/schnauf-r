package com.senacor.schnaufr.serialization

import com.squareup.moshi.*
import org.slf4j.LoggerFactory

object JsonSerializer {

    val logger = LoggerFactory.getLogger("JsonSerializer")
    val moshi: Moshi

    init {
        moshi = Moshi.Builder()
            .add(UUIDAdapter)
            .build()
    }

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

    inline fun <reified T> fromJsonArray(json: String): List<T> {
        if (json.isBlank()) {
            throw IllegalArgumentException("body is blank")
        }
        try {
            val listType = Types.newParameterizedType(List::class.java, T::class.java)
            val adapter = moshi.adapter<List<T>>(listType)
            val result = adapter.fromJson(json)
            if (result == null) {
                logger.error("failed to deserialize into class '{}'. body: '{}'", T::class, json)
                throw JsonSerializationException("failed to deserialize into class ${T::class}")
            }
            return result
        } catch (ex: Exception) {
            logger.error("failed to deserialize JSON to list of class '{}'. Body: '{}'", T::class, json)
            throw JsonSerializationException(ex)
        }
    }

    inline fun <reified T> toJsonString(entity: T): String {
        try {
            val adapter = moshi.adapter<T>(T::class.java)
            return adapter.toJson(entity)
        } catch (ex: Exception) {
            logger.error("failed to serialize {} to JSON. entity: {}", ex, T::class, entity)
            throw JsonSerializationException(ex)
        }
    }

    inline fun <reified T> toJsonString(entities: List<T>): String {
        if (entities.isEmpty()) {
            throw IllegalArgumentException("cannot convert zero objects to a JSON list.")
        }
        try {
            val type = Types.newParameterizedType(Collection::class.java, T::class.java)
            val adapter = moshi.adapter<Collection<T>>(type)
            return adapter.toJson(entities)
        } catch (ex: Exception) {
            logger.error("failed to serialize {} to JSON list. entities: {}", ex, T::class, entities)
            throw JsonSerializationException(ex)
        }
    }
}

