package com.senacor.schnaufr.location

import io.reactivex.Flowable
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket
import org.reactivestreams.Publisher
import java.time.Instant
import java.util.*

class GeolocationMessageHandler(val repository: GeolocationRepository) : AbstractRSocket() {

    override fun requestChannel(payloads: Publisher<Payload>): Flowable<Payload> {
        return Flowable.just(SchnaufrPosition(
                UUID.randomUUID(),
                Geolocation(10.0, 10.0, Instant.now())).asPayload()
        )
    }
}