package com.senacor.schnaufr.user

import com.senacor.schnaufr.operation
import com.senacor.schnaufr.serialization.JsonSerializer
import com.senacor.schnaufr.user.model.*
import io.rsocket.*
import io.rsocket.util.DefaultPayload
import org.slf4j.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
            "findAllUsers" -> {
                Mono.defer {
                    repository.readAllUsers()
                            .collectList()
                            .map { JsonSerializer.toJsonString(it) }
                            .map { DefaultPayload.create(it) }
                }
            }
            else -> return Mono.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }

    override fun requestStream(payload: Payload): Flux<Payload> {
        logger.info("received payload '{}' with metadata '{}'", payload.dataUtf8, payload.metadataUtf8)


        return when (payload.operation) {

            "findAvatar" -> {
                val schnauferId = AvatarBySchnauferIdRequest.fromJson(payload.dataUtf8).id
                Flux.defer {
                    repository.readAvatar(schnauferId)
                        .map { DefaultPayload.create(it) }
                }
            }
            else -> return Flux.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}