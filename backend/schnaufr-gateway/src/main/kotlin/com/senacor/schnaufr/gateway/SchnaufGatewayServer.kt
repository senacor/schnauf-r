package com.senacor.schnaufr.gateway

import io.rsocket.*
import io.rsocket.transport.netty.server.*
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

class SchnaufGatewayServer(private val gatewayMessageHandler: SchnaufGatewayMessageHandler, private val port: Int) {
    companion object {
        val logger = LoggerFactory.getLogger(SchnaufGatewayServer::class.java)
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

    private fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        return Mono.just(gatewayMessageHandler)
    }
}

