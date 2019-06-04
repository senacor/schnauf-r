package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUIDAdapter
import com.squareup.moshi.Moshi
import io.reactivex.Flowable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket

class SchnaufMessageHandler(val schnaufClient: SchnaufClient, val schnaufrClient: SchnaufrClient) : AbstractRSocket() {

    companion object {
        val moshi = Moshi.Builder().add(UUIDAdapter).build();
        val schnaufJsonAdapter = SchnaufJsonAdapter(moshi);
    }

    override fun requestStream(payload: Payload): Flowable<Payload> {
        return schnaufClient.getAllSchnaufs()
                .map(SchnaufMessageHandler.schnaufJsonAdapter::toJson).map { DefaultPayload.text(it) };
    }


}