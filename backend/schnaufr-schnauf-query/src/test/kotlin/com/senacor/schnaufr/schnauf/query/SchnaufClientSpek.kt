package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.schnauf.query.model.MetaData
import com.senacor.schnaufr.schnauf.query.model.Schnauf
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
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
        private lateinit var disposable: Disposable;

        fun start() {
            disposable = RSocketFactory
                    .receive()
                    .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
                    .transport(TcpServerTransport.create(8081))  // Netty websocket transport
                    .start()
                    .subscribe();
        }

        fun stop() {
            disposable.dispose()
        }

        private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
            return Single.just(object : AbstractRSocket() {
                override fun requestStream(payload: Payload): Flowable<Payload> {
                    val operation = MetaData.fromJson(payload.metadataUtf8)?.operation;

                    if (GET_ALL_SCHNAUFS_COMMAND.equals(operation)) {
                        return Flowable.fromArray(DefaultPayload.text(schnauf1.toJson()), DefaultPayload.text(schnauf2.toJson()))
                    }
                    return Flowable.empty()
                }
            });
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

            val schnaufs = sut.getAllSchnaufs().blockingIterable().iterator();

            expectThat(schnaufs.hasNext()).isTrue()
        }


    }
})
