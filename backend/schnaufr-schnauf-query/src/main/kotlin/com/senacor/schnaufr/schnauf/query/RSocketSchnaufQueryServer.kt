package com.senacor.schnaufr.schnauf.query

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket

class RSocketSchnaufQueryServer() {

    private lateinit var disposable : Disposable;

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(MessageHandler())
    }

    fun start() {
        disposable = RSocketFactory
            .receive()
            .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
            .transport(TcpServerTransport.create(8080))  // Netty websocket transport
            .start()
            .subscribe()
    }

    fun stop() {
        disposable.dispose()
    }

    private class MessageHandler: AbstractRSocket() {
        override fun requestStream(payload: Payload): Flowable<Payload> {
            return Flowable.fromIterable(listOf(DefaultPayload.text("test-schnauf"), DefaultPayload.text("mohmann-schnauf")))
        }
    }
}

