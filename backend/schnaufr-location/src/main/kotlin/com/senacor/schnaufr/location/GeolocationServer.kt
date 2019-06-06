package com.senacor.schnaufr.location


import io.rsocket.ConnectionSetupPayload
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.server.CloseableChannel
import io.rsocket.transport.netty.server.TcpServerTransport
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

class GeolocationServer(val repository: GeolocationRepository) {

    private val logger = LoggerFactory.getLogger(GeolocationServer::class.java)
    private val messageHandler = GeolocationMessageHandler(repository)

    private var closeable: CloseableChannel? = null

    fun start() {
        closeable = RSocketFactory
                .receive()
                .acceptor(this::handler)
                .transport(TcpServerTransport.create(8080))
                .start()
                .doOnSuccess { logger.info("Server started") }
                .block()
    }

    fun stop() {
        closeable?.dispose()
        logger.info("Server stopped")
    }

    private fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        logger.info("Received setup {}", setup)
        return Mono.just(messageHandler)
    }

}

