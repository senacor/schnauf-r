package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.schnauf.query.model.Author
import com.senacor.schnaufr.schnauf.query.model.Schnauf
import com.senacor.schnaufr.schnauf.query.model.SchnaufFeedEntry
import io.reactivex.Flowable
import io.reactivex.Single
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket
import java.util.*

class SchnaufMessageHandler(val schnaufClient: SchnaufClient, val schnaufrClient: SchnaufrClient) : AbstractRSocket() {
    override fun requestStream(payload: Payload): Flowable<Payload> {
        return schnaufClient.getAllSchnaufs()
                // .flatMapSingle { enrichWithSchnaufrInformation(it) }
                .map { it.asPayload() }
    }

    private fun enrichWithSchnaufrInformation(schnauf: Schnauf): Single<SchnaufFeedEntry> {
        return schnaufrClient.getSchnaufrById(schnauf.submitter)
                .map { SchnaufFeedEntry(UUID.randomUUID(), schnauf.title, Author(it.avatarId, it.username, it.displayName)) }
    }

}