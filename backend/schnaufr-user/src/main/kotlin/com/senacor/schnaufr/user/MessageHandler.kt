package com.senacor.schnaufr.user

import com.senacor.schnaufr.operation
import com.senacor.schnaufr.user.model.SchnauferByIdRequest
import com.senacor.schnaufr.user.model.SchnauferByUsernameRequest
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
                val schnauferByUsernameRequest = SchnauferByUsernameRequest.fromJson(payload.dataUtf8)
                Mono.defer {
                    repository.readByUsername(schnauferByUsernameRequest.username)
                        .map { it.asPayload() }
                        .switchIfEmpty(Mono.error<Payload>(RuntimeException("userNotFound")))
                }
            }
            "findUserById" -> {
                val schnauferByIdRequest = SchnauferByIdRequest.fromJson(payload.dataUtf8)
                Mono.defer {
                    repository.read(id = schnauferByIdRequest.id)
                        .map { it.asPayload() }
                        .switchIfEmpty(Mono.error<Payload>(RuntimeException("userNotFound")))
                }
            }
            else -> return Mono.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}