package com.senacor.schnaufr.user

import io.reactivex.*
import io.rsocket.kotlin.*
import io.rsocket.kotlin.util.AbstractRSocket
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import java.util.UUID

class MessageHandler(
    private val repository: SchnauferRepository
) : AbstractRSocket() {

    companion object {
        val logger = LoggerFactory.getLogger(MessageHandler::class.java)
    }

    override fun requestResponse(payload: Payload): Single<Payload> {
        logger.info("received payload '{}' with metadata '{}'", payload.dataUtf8, payload.metadataUtf8)

        val command = payload.metadataUtf8

        if (command.equals(SchnauferServer.FIND_USER_COMMAND)) {
            logger.info("Looking up user in DB")
            try {
                val userId = UUID.fromString(payload.dataUtf8)
                val userMaybe = repository.read(userId)
                return if (userMaybe.isEmpty.blockingGet()) {
                    Single.just(DefaultPayload.text("userNotFound"))
                } else {
                    val gotten = userMaybe.blockingGet()
                    Single.just(DefaultPayload.text(gotten.toString()))
                }
            } catch (ex: IllegalArgumentException) {
                logger.error("Supplied userId param {} is no UUID!", payload.dataUtf8)
                return Single.error(ex)
            }
        }

        return Single.error(NotImplementedError("Command $command not found!"))
    }

    override fun fireAndForget(payload: Payload): Completable {
        return super.fireAndForget(payload)
    }

    override fun requestChannel(payloads: Publisher<Payload>): Flowable<Payload> {
        return super.requestChannel(payloads)
    }

    override fun requestStream(payload: Payload): Flowable<Payload> {
        return super.requestStream(payload)
    }
}