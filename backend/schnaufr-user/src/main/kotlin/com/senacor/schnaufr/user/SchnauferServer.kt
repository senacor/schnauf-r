package com.senacor.schnaufr.user

import com.netifi.broker.BrokerClient
import io.rsocket.*
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

class SchnauferServer(
    private val messageHandler: MessageHandler
) {

    companion object {
        val logger = LoggerFactory.getLogger(SchnauferServer::class.java)
    }

    private lateinit var brokerClient: BrokerClient

    fun start() {
        // Build Netifi Netifi Connection
        brokerClient = BrokerClient.tcp()
            .disableSsl()
            .group("quickstart.services.schnaufr")
            .destination("schnaufer-user")
            .accessKey(9007199254740991L)
            .accessToken("kTBDVtfRBO4tHOnZzSyY5ym2kfY=")
            .host("localhost")
            .port(8001)
            .build()

        brokerClient
            .addNamedRSocket("schnaufer-socket", messageHandler)
            .groupServiceSocket("quickstart.services.schnaufr")
    }

    fun stop() {
        logger.info("Server stopped")
    }

    fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        logger.info("received setup {}", setup)
        return Mono.just(messageHandler)
    }
}

class SchnauferServerNetifi {
    fun start() {
    }
}

