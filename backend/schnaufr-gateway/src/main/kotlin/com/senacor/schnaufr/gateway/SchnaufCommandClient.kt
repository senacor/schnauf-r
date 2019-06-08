package com.senacor.schnaufr.gateway

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.Disposable
import reactor.core.publisher.Mono

class SchnaufCommandClient {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(SchnaufGatewayServer::class.java)
    }

    private val schnaufCommandHost = System.getenv("SCHNAUF_COMMAND_HOST") ?: "localhost"
    private val schnaufCommandPort = System.getenv("SCHNAUF_COMMAND_PORT")?.toInt() ?: 8081
    private val connectionString = "$schnaufCommandHost:$schnaufCommandPort"


    private lateinit var disposable: Disposable
    lateinit var rsocket: Mono<RSocket>

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufCommandHost, schnaufCommandPort))
                .start()

        disposable = rsocket.subscribe()
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}