package com.senacor.schnaufr.schnauf.query

import io.reactivex.Flowable
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket

class SchnaufMessageHandler(val schnaufClient: SchnaufClient, val schnaufrClient: SchnaufrClient) : AbstractRSocket() {


    override fun requestStream(payload: Payload): Flowable<Payload> {
        return schnaufClient.getAllSchnaufs()
                .map { it.asPayload() };
    }


}