package com.senacor.schnaufr.location

import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoClient
import org.bson.BsonDocument
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.*

class GeolocationRepository(client: MongoClient) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JsonSerializer")
    }

    private val database = client.getDatabase("schnaufr-location")
    private val collection = database.getCollection<SchnaufrPosition>()

    fun upsert(schnaufr: SchnaufrPosition): Mono<Boolean> {
        return collection.updateOne(schnaufr, UpdateOptions().upsert(true))
                .toMono()
                .map(UpdateResult::wasAcknowledged)
    }

    fun readById(id: UUID): Mono<SchnaufrPosition> {
        return collection.findOne(SchnaufrPosition::id eq id).toMono()
    }

    fun watch(principal: UUID? = null): Flux<SchnaufrPosition> {

        val emitter = EmitterProcessor.create<SchnaufrPosition>()

        collection.withKMongo().watchIndefinitely(
                subscribeListener = { logger.info("Subscribed to new Schnaufr locations") },
                errorListener = { logger.error("Error on Schnaufr location subscription", it) },
                listener = { document ->
                    document.fullDocument?.let {
                        logger.info("Found new document on watch: $it")
                        emitter.onNext(it)
                    }
                })

        return emitter
    }

    fun deleteAll(): Publisher<DeleteResult> {
        return collection.deleteMany(BsonDocument())
    }
}
