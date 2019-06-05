package com.senacor.schnaufr.user

import io.reactivex.*
import io.reactivex.disposables.Disposable
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

    private lateinit var disposable: Disposable

    fun start() {
        disposable = RSocketFactory
            .receive()
            .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
            .transport(TcpServerTransport.create(9090))
            .start()
            .doOnSuccess { logger.info("Server started") }
            .subscribe()
    }

    fun stop() {
        disposable.dispose()
    }

    fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        logger.info("received setup {}", setup)
        return Single.just(messageHandler)
    }
}

