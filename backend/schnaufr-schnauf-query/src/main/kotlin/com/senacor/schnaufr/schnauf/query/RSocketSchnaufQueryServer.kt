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

class RSocketSchnaufQueryServer() {
    companion object {
        val logger = LoggerFactory.getLogger(RSocketSchnaufQueryServer::class.java)
    }

    private val schnaufrClient = SchnaufrClient()
    private val schnaufClient = SchnaufClient()

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(SchnaufMessageHandler(schnaufClient, schnaufrClient))
    }

    fun start(): Single<NettyContextCloseable> {
        // ToDo: Start Clients
        //schnaufrClient.start();

        return RSocketFactory
                .receive()
                .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
                .transport(WebsocketServerTransport.create("localhost", 8080))  // Netty websocket transport
                .start()
                .doOnSuccess { logger.info("Server started") }
    }

    fun stop() {
        // ToDo: Stop Clients
        //schnaufrClient.start();

    }
}

