package com.senacor.schnaufr.user

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.*
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.gridfs.AsyncOutputStream
import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper
import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream
import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper.toAsyncOutputStream
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import reactor.core.publisher.*
import java.io.ByteArrayOutputStream
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
        return collection.insertOne(schnaufer).toMono()
            .flatMap { read(schnaufer.id) }
    }

    fun read(id: UUID): Mono<Schnaufer> {
        return collection.findOne(Schnaufer::id eq id).toMono()
            .doOnSubscribe { logger.info("Looking up user id '$id' in MongoDB") }
    }

    fun readByUsername(userName: String): Mono<Schnaufer> {
        return collection.findOne(Schnaufer::username eq userName).toMono()
            .doOnSubscribe { logger.info("Looking up user with name '$userName' in MongoDB") }
    }

    fun saveAvatar(schnauferId: UUID, data: InputStream): Mono<String> {

        val options = GridFSUploadOptions()
            .chunkSizeBytes(1024 * 1024)
            .metadata(Document("schnauferId", schnauferId))

        val streamToUpload = toAsyncInputStream(data)
        return bucket.uploadFromStream("avatar", streamToUpload, options).toMono()
            .doOnSuccess { logger.info("successfully uploaded avatar with id {}", schnauferId) }
            .doOnError { logger.error("failed to upload avatar with id {}", schnauferId, it) }
            .doFinally { streamToUpload.close() }
            .map { it.toString() }
    }

    fun readAvatar(schnauferId: UUID): Flux<ByteArray> {

        val whereQuery = BasicDBObject()
        whereQuery["metadata.schnauferId"] = schnauferId

        val chunkSize = 512000

        return bucket.find(whereQuery).toMono().map {gridFSFile ->
            bucket.openDownloadStream(gridFSFile.objectId)
        }.flatMapMany {downLoadStream ->
            Stream.generate {
                ByteBuffer.allocate(chunkSize)}
                .toFlux()
                .flatMap { buffer ->
                    Mono.from(downLoadStream.read(buffer))
                        .map { buffer }
                }
                .map { it.array() }
                .takeWhile { chunk -> chunk.size < chunkSize }
                .doOnComplete {

                }

        }
    }
}
