package com.senacor.schnaufr.gateway

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.Disposable
import reactor.core.publisher.Mono

class SchnaufQueryClient {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(SchnaufGatewayServer::class.java)
    }

    private val schnaufQueryHost = System.getenv("SCHNAUF_QUERY_HOST") ?: "localhost"
    private val schnaufQueryPort = System.getenv("SCHNAUF_QUERY_PORT")?.toInt() ?: 8083
    private val connectionString = "$schnaufQueryHost:$schnaufQueryPort"


    private lateinit var disposable: Disposable
    lateinit var rsocket: Mono<RSocket>

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufQueryHost, schnaufQueryPort))
                .start()

        disposable = rsocket.subscribe()
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}