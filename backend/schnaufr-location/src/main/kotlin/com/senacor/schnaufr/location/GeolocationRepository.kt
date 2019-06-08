package com.senacor.schnaufr.location

import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoClient
import org.bson.BsonDocument
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import reactor.core.publisher.*
import java.util.*

class GeolocationRepository(client: MongoClient) {

    private val logger = LoggerFactory.getLogger(GeolocationRepository::class.java)

    private val database = client.getDatabase("schnaufr-location")
    private val collection = database.getCollection<SchnaufrLocation>()

    fun upsert(schnaufr: SchnaufrLocation): Mono<UpdateResult> {
        return collection
                .updateOneById(schnaufr.id, schnaufr, UpdateOptions().upsert(true))
                .toMono()
    }

    fun readAll(): Flux<SchnaufrLocation> {
        return collection.find().toFlux();
    }

    fun readById(id: UUID): Mono<SchnaufrLocation> {
        return collection.findOne(SchnaufrLocation::id eq id).toMono()
    }

    fun watch(): Flux<SchnaufrLocation> {

        val emitter = EmitterProcessor.create<SchnaufrLocation>()

        collection.withKMongo().watchIndefinitely(
                subscribeListener = { logger.info("Subscribed to new schnaufr locations") },
                errorListener = { logger.error("Error on schnaufr locations subscription", it) },
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
