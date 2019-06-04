package com.senacor.schnaufr.user

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.*
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream
import io.reactivex.*
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.rxjava2.*
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.UUID

class SchnauferRepository(private val client: MongoClient) {

    companion object {
        val logger = LoggerFactory.getLogger(SchnauferRepository::class.java)
    }

    private val database = client.getDatabase("schnauf")
    private val collection = database.getCollection<Schnaufer>()
    private val bucket = GridFSBuckets.create(database, "avatar")

    fun create(schnaufer: Schnaufer): Single<Schnaufer> {
        return collection.insertOne(schnaufer).single()
            .flatMapMaybe { read(schnaufer.id) }
            .toSingle()
    }

    fun read(id: UUID): Maybe<Schnaufer> {
        return collection.findOne(Schnaufer::id eq id)
    }

    fun saveAvatar(schnauferId: UUID, data: InputStream): Completable {

        val options = GridFSUploadOptions()
            .chunkSizeBytes(1024 * 1024)
            .metadata(Document("schnauferId", schnauferId))

        val streamToUpload = toAsyncInputStream(data)
        return bucket.uploadFromStream("avatar", streamToUpload, options).single()
            .doOnSuccess { logger.info("successfully uploaded avatar with id {}", schnauferId) }
            .doOnError { logger.error("failed to upload avatar with id {}", schnauferId, it) }
            .doOnDispose { streamToUpload.close() }
            .ignoreElement()
    }

    fun readAvatar(schnauferId: UUID): Maybe<GridFSFile> {

        val whereQuery = BasicDBObject()
        whereQuery["metadata.schnauferId"] = schnauferId

        return bucket.find(whereQuery).maybe()
    }
}