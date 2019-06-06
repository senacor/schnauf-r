package com.senacor.schnaufr.user

import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper.toAsyncOutputStream
import com.senacor.schnaufr.operation
import io.rsocket.*
import io.rsocket.util.DefaultPayload
import org.slf4j.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.ByteArrayOutputStream
import java.util.UUID

class MessageHandler(private val repository: SchnauferRepository) : AbstractRSocket() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MessageHandler::class.java)
    }

    override fun requestResponse(payload: Payload): Mono<Payload> {
        logger.info("received payload '{}' with metadata '{}'", payload.dataUtf8, payload.metadataUtf8)

        return when (payload.operation) {
            "findUserByUsername" -> {
                Mono.defer {
                    repository.readByUsername(payload.dataUtf8)
                        .map { it.asPayload() }
                        .switchIfEmpty(Mono.error<Payload>(RuntimeException("userNotFound")))
                }
            }
            "findUserById" -> {
                Mono.defer {
                    repository.read(id = UUID.fromString(payload.dataUtf8))
                        .map { it.asPayload() }
                        .switchIfEmpty(Mono.error<Payload>(RuntimeException("userNotFound")))
                }
            }
            "readAvatar" -> {
                val schnauferId = UUID.fromString(payload.dataUtf8)
                Mono.defer {
                    val bos = ByteArrayOutputStream()
                    val asyncOS = toAsyncOutputStream(bos)
                    repository.readAvatar(schnauferId, asyncOS)
                        .map { DefaultPayload.create(bos.toByteArray()) }
                        .switchIfEmpty(Mono.error<Payload>(RuntimeException("noAvatarForSchnauferIdFound")))
                }
            }
            else -> return Mono.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}