package com.senacor.schnaufr.schnauf

import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.result.DeleteResult
import com.mongodb.reactivestreams.client.MongoClient
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.bson.BsonDocument
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.reactivestreams.watchIndefinitely
import org.litote.kmongo.rxjava2.findOne
import org.litote.kmongo.rxjava2.single
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class SchnaufRepository(client: MongoClient) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JsonSerializer")
    }

    private val database = client.getDatabase("schnauf")
    private val collection = database.getCollection<Schnauf>()

    fun create(schnauf: Schnauf): Single<Schnauf> {
        return collection.insertOne(schnauf).single()
                .flatMapMaybe { readById(schnauf.id) }
                .toSingle()
    }

    fun readById(id: UUID): Maybe<Schnauf> {
        return collection.findOne(Schnauf::id eq id)
    }

    fun readLatest(limit: Int = 10): Flowable<Schnauf> {
        return Flowable.fromPublisher(collection.find().limit(limit))
    }

    fun watch(): Observable<Schnauf> {
        val subject = PublishSubject.create<Schnauf>()
        collection.watchIndefinitely(
                fullDocument = FullDocument.UPDATE_LOOKUP,
                subscribeListener = { logger.info("Subscribed to new Schnaufs") },
                errorListener = { logger.error("Error on Schnauf subscription", it) }) { document ->
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
