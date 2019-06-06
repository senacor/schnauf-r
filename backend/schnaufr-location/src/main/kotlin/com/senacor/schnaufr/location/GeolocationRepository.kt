package com.senacor.schnaufr.location

import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoClient
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.bson.BsonDocument
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.reactivestreams.watchIndefinitely
import org.litote.kmongo.rxjava2.findOne
import org.litote.kmongo.rxjava2.updateOne
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class GeolocationRepository(client: MongoClient) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JsonSerializer")
    }

    private val database = client.getDatabase("schnaufr-location")
    private val collection = database.getCollection<SchnaufrPosition>()

    fun upsert(schnaufr: SchnaufrPosition): Maybe<Boolean> {
        return collection.updateOne(schnaufr, UpdateOptions().upsert(true))
                .map(UpdateResult::wasAcknowledged)
    }

    fun readById(id: UUID): Maybe<SchnaufrPosition> {
        return collection.findOne(SchnaufrPosition::id eq id)
    }

    fun watch(): Observable<SchnaufrPosition> {
        val subject = PublishSubject.create<SchnaufrPosition>()
        collection.watchIndefinitely(
                fullDocument = FullDocument.UPDATE_LOOKUP,
                subscribeListener = { logger.info("Subscribed to new Schnaufr locations") },
                errorListener = { logger.error("Error on Schnaufr location subscription", it) }) { document ->
            document.fullDocument?.let {
                logger.info("Found new document on watch: $it")
                subject.onNext(it)
            }
        }

        return subject
    }

    fun deleteAll(): Publisher<DeleteResult> {
        return collection.deleteMany(BsonDocument())
    }
}
