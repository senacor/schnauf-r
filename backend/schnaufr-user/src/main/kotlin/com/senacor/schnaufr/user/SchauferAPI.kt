package com.senacor.schnaufr.user

import io.reactivex.*
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.*
import io.rsocket.kotlin.util.AbstractRSocket
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory

class SchnauferAPI {

    companion object {
        val logger = LoggerFactory.getLogger(SchnauferAPI::class.java)
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
        return Single.just(MessageHandler())
    }
}

class MessageHandler : AbstractRSocket() {

    companion object {
        val logger = LoggerFactory.getLogger(MessageHandler::class.java)
    }

    override fun requestResponse(payload: Payload): Single<Payload> {
        logger.info("received payload '{}' with metadata '{}'", payload.dataUtf8, payload.metadataUtf8)
        return Single.just(DefaultPayload.text("huhu"))
    }

    override fun fireAndForget(payload: Payload): Completable {
        return super.fireAndForget(payload)
    }

    override fun requestChannel(payloads: Publisher<Payload>): Flowable<Payload> {
        return super.requestChannel(payloads)
    }

    override fun requestStream(payload: Payload): Flowable<Payload> {
        return super.requestStream(payload)
    }
}