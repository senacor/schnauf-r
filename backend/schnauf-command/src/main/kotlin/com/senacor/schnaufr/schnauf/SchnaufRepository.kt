package com.senacor.schnaufr.schnauf

import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.reactivestreams.client.MongoClient
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.bson.BsonDocument
import org.bson.conversions.Bson
import org.litote.kmongo.contains
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.reactivestreams.watchIndefinitely
import org.litote.kmongo.reactivestreams.withKMongo
import org.litote.kmongo.rxjava2.findOne
import org.litote.kmongo.rxjava2.single
import org.litote.kmongo.size
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KProperty1

class SchnaufRepository(client: MongoClient) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JsonSerializer")
    }

    private val database = client.getDatabase("schnauf")
    private val collection = database.getCollection<Schnauf>()

    private fun recipientFilter(principal: UUID?, recipients: List<UUID>): Boolean = principal?.let {
        return recipients.contains(principal) || recipients.isEmpty()
    } ?: true

    private fun recipientFilterBson(principal: UUID?, recipients: KProperty1<Schnauf, List<UUID>>): Bson = principal?.let {
        Filters.or(
                recipients contains principal,
                recipients size 0
        )
    } ?: BasicDBObject()

    fun create(schnauf: Schnauf): Single<Schnauf> {
        return collection.insertOne(schnauf).single()
                .flatMapMaybe { readById(schnauf.id) }
                .toSingle()
    }

    fun readById(id: UUID): Maybe<Schnauf> {
        return collection.findOne(Schnauf::id eq id)
    }

    fun readLatest(limit: Int = 10, principal: UUID? = null): Flowable<Schnauf> {
        return Flowable.fromPublisher(collection.find(recipientFilterBson(principal, Schnauf::recipients)).limit(limit))
    }

    fun watch(principal: UUID? = null): Observable<Schnauf> {
        val publisher = PublishSubject.create<Schnauf>()

        collection.withKMongo().watchIndefinitely(
                subscribeListener = { logger.info("Subscribed to new Schnaufs") },
                errorListener = { logger.error("Error on Schnauf subscription", it) },
                listener = { document ->
                    document.fullDocument?.let {
                        logger.info("Found new document on watch: $it")

                        if (recipientFilter(principal, it.recipients)) { // TODO
                            publisher.onNext(it)
                        }
                    }
                })

        return publisher
    }

    fun deleteAll(): Publisher<DeleteResult> {
        return collection.deleteMany(BsonDocument())
    }
}
