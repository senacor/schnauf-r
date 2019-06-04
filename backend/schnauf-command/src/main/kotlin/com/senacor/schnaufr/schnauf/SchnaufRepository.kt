package com.senacor.schnaufr.schnauf

import com.mongodb.reactivestreams.client.MongoClient
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.rxjava2.findOne
import org.litote.kmongo.rxjava2.single
import java.util.*

class SchnaufRepository(client: MongoClient) {

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

    fun readAll(): Flowable<Schnauf> {
        return Flowable.fromPublisher(collection.find())
    }
}
