package com.senacor.schnaufr.user

import com.senacor.schnaufr.operation
import io.rsocket.*
import org.slf4j.*
import reactor.core.publisher.Mono
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
            else -> return Mono.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}