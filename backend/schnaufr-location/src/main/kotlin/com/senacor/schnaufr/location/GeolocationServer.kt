package com.senacor.schnaufr.location

import io.reactivex.Single
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.Setup
import io.rsocket.kotlin.transport.netty.server.NettyContextCloseable
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import org.slf4j.LoggerFactory

class GeolocationServer(val repository: GeolocationRepository) {

    private val logger = LoggerFactory.getLogger(GeolocationServer::class.java)

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(GeolocationMessageHandler(repository))
    }

    fun start(): Single<NettyContextCloseable> {
        return RSocketFactory
                .receive()
                .acceptor { { setup, rSocket -> handler(setup, rSocket) } }
                .transport(TcpServerTransport.create(8080))
                .start()
                .doOnSuccess { logger.info("Server started") }
    }

    fun stop() {
        // Nothing to do
    }

}

