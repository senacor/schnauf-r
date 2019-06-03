package com.senacor.schnaufr.schnauf

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket

class RSocketServer {

    private lateinit var disposable: Disposable

    fun start() {
        disposable = RSocketFactory
                .receive()
                .acceptor { { setup, rSocket -> handler(setup, rSocket) } }
                .transport(TcpServerTransport.create(8080))
                .start()
                .subscribe()
    }

    fun stop() {
        disposable.dispose()
    }

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(object : AbstractRSocket() {
            override fun requestResponse(payload: Payload): Single<Payload> {
                return Single.just(DefaultPayload.text("huhu"))
            }
        })
    }
}
