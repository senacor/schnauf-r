package com.senacor.schnaufr.schnauf

import io.reactivex.Single
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.Setup
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RSocketServer(private val messageHandler: MessageHandler, private val port: Int = 8080) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RSocketServer::class.java)
    }

    fun start() =
            RSocketFactory
                    .receive()
                    .acceptor { { setup, rSocket -> handler(setup, rSocket) } }
                    .transport(TcpServerTransport.create(port))
                    .start()
                    .doOnSuccess { logger.info("Server started") }

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        logger.info("Received setup {}", setup)
        return Single.just(messageHandler)
    }
}
