package com.senacor.schnaufr.schnauf.query

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.Setup
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport

class RSocketSchnaufQueryServer() {
    private lateinit var disposable: Disposable;


    private fun createSchnaufClient(): SchnaufClient {
        return SchnaufClient();
    }

    private fun createSchnaufrClient(): SchnaufrClient {
        return SchnaufrClient();
    }

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(SchnaufMessageHandler(createSchnaufClient(), createSchnaufrClient()))
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
}

