package com.senacor.schnaufr.user

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.*
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream
import com.senacor.schnaufr.user.model.Schnaufer
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.slf4j.LoggerFactory
import reactor.core.publisher.*
import java.io.InputStream
import java.util.UUID

class  SchnauferRepository(private val client: MongoClient) {

    companion object {
        val logger = LoggerFactory.getLogger(SchnauferRepository::class.java)
    }

    private val database = client.getDatabase("schnauf")
    private val collection = database.getCollection<Schnaufer>()
    private val bucket = GridFSBuckets.create(database, "avatar")

    fun create(schnaufer: Schnaufer): Mono<Schnaufer> {
        logger.info("Try to create user $schnaufer")
        return collection.insertOne(schnaufer).toMono()
            .flatMap { read(schnaufer.id) }
                .doOnError { logger.error(it.message) }
                .doOnSuccess{ logger.info("Successfully created $schnaufer") }
    }

    fun read(id: UUID): Mono<Schnaufer> {
        return collection.findOne(Schnaufer::id eq id).toMono()
            .doOnSubscribe { logger.info("Looking up user id '$id' in MongoDB") }
    }

    fun readByUsername(userName: String): Mono<Schnaufer> {
        return collection.findOne(Schnaufer::username eq userName).toMono()
            .doOnSubscribe { logger.info("Looking up user with name '$userName' in MongoDB") }
    }

    fun saveAvatar(schnauferId: UUID, data: InputStream): Mono<UUID> {

        val options = GridFSUploadOptions()
            .chunkSizeBytes(1024 * 1024)
            .metadata(Document("schnauferId", schnauferId))

        val streamToUpload = toAsyncInputStream(data)
        return bucket.uploadFromStream("avatar", streamToUpload, options).toMono()
            .doOnSuccess { logger.info("successfully uploaded avatar with id {}", schnauferId) }
            .doOnError { logger.error("failed to upload avatar with id {}", schnauferId, it) }
            .doFinally { streamToUpload.close() }
            .map { UUID.fromString(it.toString()) }
    }

    fun readAvatar(schnauferId: UUID): Mono<GridFSFile> {

        val whereQuery = BasicDBObject()
        whereQuery["metadata.schnauferId"] = schnauferId

        return bucket.find(whereQuery).toMono()
    }
}