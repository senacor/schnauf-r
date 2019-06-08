package com.senacor.schnaufr.query

import io.rsocket.*
import io.rsocket.transport.netty.server.*
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

class SchnaufQueryServer(private val messageHandler: SchnaufMessageHandler, private val port: Int) {
    companion object {
        val logger = LoggerFactory.getLogger(SchnaufQueryServer::class.java)
    }

    private var closeable: CloseableChannel? = null


    fun start() {
        closeable = RSocketFactory
            .receive()
            .acceptor(this::handler)
            .transport(TcpServerTransport.create(port))
            .start()
            .doOnSuccess { logger.info("Server started") }
            .block()
    }
    fun stop() {
        closeable?.dispose()
        logger.info("Server stopped")
    }

    fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        return Mono.just(messageHandler)
    }
}

