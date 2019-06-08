package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.schnauf.query.model.*
import io.rsocket.*
import reactor.core.publisher.*

class SchnaufMessageHandler(val schnaufClient: SchnaufClient, val schnauferClient: SchnauferClient) : AbstractRSocket() {
    override fun requestStream(payload: Payload): Flux<Payload> {
        return schnaufClient.getAllSchnaufs()
                // .flatMapSingle { enrichWithSchnaufrInformation(it) }
                .map { it.asPayload() }
    }

    private fun enrichWithSchnaufrInformation(schnauf: Schnauf): Mono<SchnaufFeedEntry> {
        return schnauferClient.getSchnaufrById(schnauf.submitter)
                .map { SchnaufFeedEntry(UUID(), schnauf.title, Author(it.avatarId, it.username, it.displayName)) }
    }

}