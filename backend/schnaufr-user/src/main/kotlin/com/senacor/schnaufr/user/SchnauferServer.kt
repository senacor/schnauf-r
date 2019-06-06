package com.senacor.schnaufr.user

import io.rsocket.*
import io.rsocket.transport.netty.server.*
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

class SchnauferServer(
    private val messageHandler: MessageHandler
) {

    companion object {
        val logger = LoggerFactory.getLogger(SchnauferServer::class.java)
    }

    private var closeable: CloseableChannel? = null

    fun start() {
        closeable = RSocketFactory
            .receive()
            .acceptor(this::handler)
            .transport(TcpServerTransport.create(9090))
            .start()
            .doOnSuccess { logger.info("Server started") }
            .block()
    }

    fun stop() {
        closeable?.dispose()
        logger.info("Server stopped")
    }

    fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        logger.info("received setup {}", setup)
        return Mono.just(messageHandler)
    }
}

