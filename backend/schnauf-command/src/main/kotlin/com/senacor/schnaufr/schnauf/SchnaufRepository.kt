package com.senacor.schnaufr.schnauf

import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.reactivestreams.client.MongoClient
import org.bson.BsonDocument
import org.bson.conversions.Bson
import org.litote.kmongo.contains
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.size
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.*
import java.util.*
import kotlin.reflect.KProperty1

class SchnaufRepository(client: MongoClient) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JsonSerializer")
    }

    private val database = client.getDatabase("schnauf")
    private val collection = database.getCollection<Schnauf>()

    private fun recipientFilter(principal: String?, recipients: List<String>): Boolean = principal?.let {
        return recipients.contains(principal) || recipients.isEmpty()
    } ?: true

    private fun recipientFilterBson(principal: String?, recipients: KProperty1<Schnauf, List<String>>): Bson = principal?.let {
        Filters.or(
                recipients contains principal,
                recipients size 0
        )
    } ?: BasicDBObject()

    fun create(schnauf: Schnauf): Mono<Schnauf> {
        return collection.insertOne(schnauf).toMono()
                .flatMap { readById(schnauf.id) }
    }

    fun readById(id: UUID): Mono<Schnauf> {
        return collection.findOne(Schnauf::id eq id).toMono()
    }

    fun readLatest(limit: Int = 10, principal: String? = null): Flux<Schnauf> {
        return Flux.defer {
            collection.find(recipientFilterBson(principal, Schnauf::recipients)).limit(limit)
        }
    }

    fun watch(principal: String? = null): Flux<Schnauf> {

        val emitter = EmitterProcessor.create<Schnauf>()

        collection.withKMongo().watchIndefinitely(
                subscribeListener = { logger.info("Subscribed to new Schnaufs") },
                errorListener = { logger.error("Error on Schnauf subscription", it) },
                listener = { document ->
                    document.fullDocument?.let {
                        logger.info("Found new document on watch: $it")

                        if (recipientFilter(principal, it.recipients)) { // TODO
                            emitter.onNext(it)
                        }
                    }
                })

        return emitter
    }

    fun deleteAll(): Mono<DeleteResult> {
        return collection.deleteMany(BsonDocument()).toMono()
    }
}
