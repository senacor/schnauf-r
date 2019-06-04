package com.senacor.schnaufr.schnauf.query

import com.mongodb.reactivestreams.client.MongoClient
import org.litote.kmongo.reactivestreams.getCollection
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory

class SchnaufRepository(private val client: MongoClient) {

    companion object {
        val logger = LoggerFactory.getLogger(SchnaufRepository::class.java)
    }

    private val database = client.getDatabase("schnauf")
    private val collection = database.getCollection<Schnauf>()

    fun read(): Publisher<Schnauf> {
        
        logger.info("Getting all schnaufs from Mongo");
        return collection.find();
    }
}