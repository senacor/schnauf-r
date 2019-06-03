package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUID
import com.squareup.moshi.Moshi
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket


class RSocketSchnaufQueryServer() {

    private lateinit var disposable: Disposable;

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

    private class MessageHandler : AbstractRSocket() {


        override fun requestStream(payload: Payload): Flowable<Payload> {
            val moshi = Moshi.Builder().build();
            val jsonAdapter = moshi.adapter(Schnauf::class.java)


            return Flowable.fromIterable(listOf(Schnauf(UUID(), "christoph", "schnauf"), Schnauf(UUID(), "mohmann", "schnauf2")))
                    .map(jsonAdapter::toJson).map { DefaultPayload.text(it) };


        }
    }
}

