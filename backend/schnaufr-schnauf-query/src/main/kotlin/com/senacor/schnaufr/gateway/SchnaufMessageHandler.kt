package com.senacor.schnaufr.gateway

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.operation
import com.senacor.schnaufr.gateway.model.Author
import com.senacor.schnaufr.gateway.model.Schnauf
import com.senacor.schnaufr.gateway.model.SchnaufFeedEntry
import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class SchnaufMessageHandler(val schnaufClient: SchnaufClient, val schnauferClient: SchnauferClient) : AbstractRSocket() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)

        const val GET_ALL_SCHNAUFS = "getAllSchnaufs"
    }
    override fun requestStream(payload: Payload): Flux<Payload> {
        return when (payload.operation) {
            GET_ALL_SCHNAUFS -> schnaufClient.getAllSchnaufs()
                    .flatMap { enrichWithSchnaufrInformation(it) }
                    .map { it.asPayload() }

            else -> return Flux.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }

    }

    private fun enrichWithSchnaufrInformation(schnauf: Schnauf): Mono<SchnaufFeedEntry> {
        return schnauferClient.getSchnaufrById(schnauf.submitter)
                .map { SchnaufFeedEntry(UUID(), schnauf.title, Author(it.avatarId, it.username, it.displayName)) }
    }

}