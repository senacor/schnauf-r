package com.senacor.schnaufr.location

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import java.time.Instant
import java.util.*

class GeolocationMessageHandler(val repository: GeolocationRepository) : AbstractRSocket() {

    private val logger = LoggerFactory.getLogger(GeolocationMessageHandler::class.java)

    override fun requestChannel(payloads: Publisher<Payload>): Flux<Payload> {

        logger.info("Channel requested")

        return Flux.fromIterable(
                0.rangeTo(10).map {
                    SchnaufrPosition(
                            UUID.randomUUID(),
                            Geolocation(23.0, 42.0, Instant.now())
                    ).asPayload()
                })

    }
}
