package com.senacor.schnaufr.gateway

import com.senacor.schnaufr.operation
import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class SchnaufGatewayMessageHandler(
        val schnaufCommandClient: SchnaufCommandClient,
        val schnaufUserClient: SchnaufUserClient,
        val schnaufQueryClient: SchnaufQueryClient
) : AbstractRSocket() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SchnaufGatewayServer::class.java)
        // Schnauf-command
        const val CREATE_SCHNAUF = "createSchnauf" // request-response
        const val WATCH_SCHNAUFS = "watchSchnaufs" // request-stream
        const val GET_ALL_SCHNAUFS_AND_WATCH = "getAllSchnaufsAndWatch" // request-stream

        // Schnauf-Query
        const val GET_ALL_SCHNAUFS = "getAllSchnaufs" // request-stream

        // Schnauf-User
        const val FIND_USER_BY_USERNAME = "findUserByUsername" // request-response
        const val FIND_USER_BY_ID = "findUserById" // request-response
    }

    override fun requestResponse(payload: Payload): Mono<Payload> {
        logger.info("Received request-response request ${payload.operation}")
        return when (payload.operation) {
            CREATE_SCHNAUF -> schnaufCommandClient.rsocket.flatMap { it.requestResponse(payload) }
            FIND_USER_BY_USERNAME -> schnaufUserClient.rsocket.flatMap { it.requestResponse(payload) }
            FIND_USER_BY_ID -> schnaufUserClient.rsocket.flatMap { it.requestResponse(payload) }
            else -> return Mono.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }

    override fun requestStream(payload: Payload): Flux<Payload> {
        logger.info("Received request-stream request ${payload.operation}")
        return when (payload.operation) {
            WATCH_SCHNAUFS -> schnaufCommandClient.rsocket.flatMapMany { it.requestStream(payload) }
            GET_ALL_SCHNAUFS_AND_WATCH -> schnaufCommandClient.rsocket.flatMapMany { it.requestStream(payload) }
            GET_ALL_SCHNAUFS -> schnaufQueryClient.rsocket.flatMapMany { it.requestStream(payload) }
            else -> return Flux.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}