package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUIDAdapter
import com.squareup.moshi.Moshi
import io.reactivex.Flowable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket

class SchnaufMessageHandler(private val schnaufRepository: SchnaufRepository) : AbstractRSocket() {

    companion object {
        val moshi = Moshi.Builder().add(UUIDAdapter).build();
        val schnaufJsonAdapter = SchnaufJsonAdapter(moshi);
    }

    override fun requestStream(payload: Payload): Flowable<Payload> {
        return Flowable.fromPublisher(schnaufRepository.read())
                .map(SchnaufMessageHandler.schnaufJsonAdapter::toJson).map { DefaultPayload.text(it) };
    }
}