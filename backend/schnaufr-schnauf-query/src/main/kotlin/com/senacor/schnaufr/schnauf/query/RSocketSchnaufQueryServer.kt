package com.senacor.schnaufr.schnauf.query

import io.reactivex.Single
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.Setup
import io.rsocket.kotlin.transport.netty.server.NettyContextCloseable
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.transport.netty.server.WebsocketRouteTransport
import io.rsocket.kotlin.transport.netty.server.WebsocketServerTransport
import org.slf4j.LoggerFactory

class RSocketSchnaufQueryServer(private val schnaufMessageHandler: SchnaufMessageHandler) {
    companion object {
        val logger = LoggerFactory.getLogger(RSocketSchnaufQueryServer::class.java)
        val APPLICATION_PORT = System.getenv("APPLICATION_PORT")?.toInt() ?: 8080
    }

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(schnaufMessageHandler)
    }
    
    fun setup(): Single<NettyContextCloseable> {
        return RSocketFactory
                .receive()
                .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
                .transport(TcpServerTransport.create(APPLICATION_PORT))  // Netty websocket transport
                .start()
                .doOnSuccess { logger.info("Server started") }
    }
}

