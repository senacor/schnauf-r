package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.model.CreateSchnaufRequest
import com.senacor.schnaufr.model.RSocketMetadata
import io.reactivex.Single
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket

class RSocketServer(private val schnaufRepository: SchnaufRepository) {

    fun start() =
        RSocketFactory
                .receive()
                .acceptor { { setup, rSocket -> handler(setup, rSocket) } }
                .transport(TcpServerTransport.create(8080))
                .start()

    private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
        return Single.just(object : AbstractRSocket() {
            override fun requestResponse(payload: Payload): Single<Payload> {
                val metadata = RSocketMetadata.fromJson(payload.metadataUtf8)
                if (metadata.operation == "createSchnauf") {
                    val schnaufRequest = CreateSchnaufRequest.fromJson(payload.dataUtf8)
                    return schnaufRepository
                            .create(Schnauf.fromRequest(schnaufRequest))
                            .map { DefaultPayload.text("huhu") }
                }

                return Single.just(DefaultPayload.text("bla"))
            }
        })
    }
}
