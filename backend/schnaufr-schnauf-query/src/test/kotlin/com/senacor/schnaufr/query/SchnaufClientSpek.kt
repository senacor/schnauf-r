package com.senacor.schnaufr.query

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.query.SchnaufClient
import com.senacor.schnaufr.query.model.MetaData
import com.senacor.schnaufr.query.model.Schnauf
import io.rsocket.*
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import strikt.api.expectThat
import strikt.assertions.isTrue

class SchnaufClientSpek : Spek({
    val GET_ALL_SCHNAUFS_COMMAND = "getAllSchnaufs"
    val schnauf1Id = UUID()
    val schnauf1Content = "blume-schnauf"
    val schnauf1User = UUID()
    val schnauf2Id = UUID()
    val schnauf2Content = "ersfeld-schnauf"
    val schnauf2User = UUID()
    val schnauf1 = Schnauf(schnauf1Id, schnauf1User, schnauf1Content)
    val schnauf2 = Schnauf(schnauf2Id, schnauf2User, schnauf2Content)

    class SchnaufTestService() {
        private lateinit var disposable: Closeable

        fun start() {
            disposable = RSocketFactory
                .receive()
                .acceptor(this::handler) // server handler RSocket
                .transport(TcpServerTransport.create(8081))  // Netty websocket transport
                .start()
                .block()!!
        }

        fun stop() {
            disposable.dispose()
        }

        fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
            return Mono.just(object : AbstractRSocket() {
                override fun requestStream(payload: Payload): Flux<Payload> {
                    val operation = MetaData.fromJson(payload.metadataUtf8)?.operation;

                    if (GET_ALL_SCHNAUFS_COMMAND.equals(operation)) {
                        return Flux.just(DefaultPayload.create(schnauf1.toJson()), DefaultPayload.create(schnauf2.toJson()))
                    }
                    return Flux.empty()
                }
            })
        }
    }

    describe("schnauf client") {
        val sut = SchnaufClient()
        val testService = SchnaufTestService()

        before {
            testService.start()
            sut.start()
        }
        after {
            sut.stop()
            testService.stop()
        }

        it("can get all schnaufs") {

            val schnaufs = sut.getAllSchnaufs(-1, java.util.UUID.randomUUID()).toIterable().iterator();

            expectThat(schnaufs.hasNext()).isTrue()
        }

    }
})
