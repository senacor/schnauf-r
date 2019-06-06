package com.senacor.schnaufr.schnauf

import io.rsocket.*
import io.rsocket.transport.netty.server.*
import org.slf4j.*
import reactor.core.publisher.Mono

class SchnaufServer(
    private val messageHandler: MessageHandler,
    private val port: Int = 8080
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SchnaufServer::class.java)
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
        logger.info("Received setup {}", setup)
        return Mono.just(messageHandler)
    }
}
