package com.senacor.schnaufr.user

import io.reactivex.*
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.*
import org.slf4j.LoggerFactory

class SchnauferServer(
    private val messageHandler: MessageHandler
) {

    companion object {
        val logger = LoggerFactory.getLogger(SchnauferServer::class.java)
        const val FIND_USER_COMMAND = "findUser"
    }

    fun setup(): Single<NettyContextCloseable> {
        return RSocketFactory
            .receive()
            .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
            .transport(TcpServerTransport.create(9090))
            .start()
            .doOnSuccess { logger.info("Server started") }
    }

    fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        logger.info("received setup {}", setup)
        return Single.just(messageHandler)
    }
}

