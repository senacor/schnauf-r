package com.senacor.schnaufr.gateway

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.Disposable
import reactor.core.publisher.Mono

class SchnaufUserClient {
    private lateinit var disposable: Disposable
    lateinit var rsocket: Mono<RSocket>

    private val schnaufUserClientHost = System.getenv("SCHNAUF_USER_HOST") ?: "localhost"
    private val schnaufUserClientPort = System.getenv("SCHNAUF_USER_PORT")?.toInt() ?: 8082
    private val connectionString = "$schnaufUserClientHost:$schnaufUserClientPort"

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SchnaufGatewayServer::class.java)
    }

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufUserClientHost, schnaufUserClientPort))
                .start()

        disposable = rsocket.subscribe()
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}