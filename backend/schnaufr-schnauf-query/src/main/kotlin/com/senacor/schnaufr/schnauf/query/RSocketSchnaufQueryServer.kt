package com.senacor.schnaufr.schnauf.query

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.Setup
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport


class RSocketSchnaufQueryServer() {

    private lateinit var disposable: Disposable;

    private fun createMongoClient(): MongoClient {
        return MongoClients.create("");
    }

    private fun createSchnaufRepository(): SchnaufRepository {
        return SchnaufRepository(createMongoClient());
    }

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(SchnaufMessageHandler(createSchnaufRepository()))
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

