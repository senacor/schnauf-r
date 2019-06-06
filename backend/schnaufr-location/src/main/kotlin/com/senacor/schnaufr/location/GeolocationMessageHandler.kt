package com.senacor.schnaufr.location

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.time.Instant
import java.util.*

class GeolocationMessageHandler(val repository: GeolocationRepository) : AbstractRSocket() {

    override fun requestChannel(payloads: Publisher<Payload>): Flux<Payload> {
        return Flux.just(SchnaufrPosition(
                UUID.randomUUID(),
                Geolocation(10.0, 10.0, Instant.now())).asPayload()
        )
    }
}