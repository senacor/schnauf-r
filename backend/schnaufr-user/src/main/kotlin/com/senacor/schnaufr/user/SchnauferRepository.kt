package com.senacor.schnaufr.user

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.*
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream
import com.senacor.schnaufr.user.model.Schnaufer
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.slf4j.LoggerFactory
import reactor.core.publisher.*
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.UUID
import java.util.stream.Stream

class SchnauferRepository(private val client: MongoClient) {

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

    fun readAllUsers(): Flux<Schnaufer> {
        return collection.find().toFlux()
    }

    fun saveAvatar(avatarId: UUID, data: InputStream): Mono<ObjectId> {

        val options = GridFSUploadOptions()
            .chunkSizeBytes(1024 * 1024)
            .metadata(Document("avatarId", avatarId))

        val streamToUpload = toAsyncInputStream(data)
        return bucket.uploadFromStream("avatar", streamToUpload, options).toMono()
            .doOnSuccess { logger.info("successfully uploaded avatar with id {}", avatarId) }
            .doOnError { logger.error("failed to upload avatar with id {}", avatarId, it) }
            .doFinally { streamToUpload.close() }
    }

    fun readAvatar(schnauferId: UUID): Flux<ByteArray> {

        val whereQuery = BasicDBObject()
        whereQuery["metadata.avatarId"] = schnauferId

        val chunkSize = 512000

        return bucket.find(whereQuery).toMono()
            .map { gridFSFile ->
                bucket.openDownloadStream(gridFSFile.objectId)
            }.flatMapMany { downLoadStream ->
                Stream.generate { ByteBuffer.allocate(chunkSize) }
                    .toFlux()
                    .doOnNext { logger.debug("create empty buffer") }
                    .flatMapSequential({ buffer ->
                        downLoadStream.read(buffer).toMono()
                            .doOnSuccess { logger.debug("populate buffer with $it bytes") }
                            .map { bytesRead -> Pair(buffer, bytesRead) }
                            .doOnSuccess { logger.debug("emit buffer") }
                    }, 1)
                    .takeUntil { buffer -> buffer.second == -1 }
                    .map { buffer -> buffer.first.convertToByteArray() }.doOnNext { logger.info("buffer converted to array with size ${it.size}") }
                    .doOnComplete {
                        logger.debug("avatar stream completed")
                    }

            }
    }

}

