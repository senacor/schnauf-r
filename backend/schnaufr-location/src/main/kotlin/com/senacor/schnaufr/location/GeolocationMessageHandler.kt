package com.senacor.schnaufr.location

import io.rsocket.AbstractRSocket
import io.rsocket.Payload
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import reactor.core.scheduler.Schedulers

class GeolocationMessageHandler(private val repository: GeolocationRepository) : AbstractRSocket() {

    private val logger = LoggerFactory.getLogger(GeolocationMessageHandler::class.java)

    private val locationUpdatesEmitter = TopicProcessor.create<SchnaufrLocation>()

    override fun requestChannel(locationUpdatesPublisher: Publisher<Payload>): Flux<Payload> {
        logger.info("Channel requested")
        registerLocationUpdatePublisher(locationUpdatesPublisher)
        return repository.readAll()
                .concatWith(locationUpdatesEmitter)
                .map(SchnaufrLocation::asPayload)
    }

    private fun registerLocationUpdatePublisher(receivedPayloads: Publisher<Payload>) {
        Flux.from(receivedPayloads)
                .map { payload -> SchnaufrLocation.fromJson(payload.dataUtf8) }
                .subscribeOn(Schedulers.elastic())
                .subscribe(this::handleLocationUpdate)
    }

    private fun handleLocationUpdate(locationUpdate: SchnaufrLocation) {
        logger.debug("Handling location update $locationUpdate")

        locationUpdatesEmitter.onNext(locationUpdate)

        repository.upsert(locationUpdate)
                .doOnSuccess { logger.info("Location update saved: $locationUpdate") }
                .block()
    }
}
