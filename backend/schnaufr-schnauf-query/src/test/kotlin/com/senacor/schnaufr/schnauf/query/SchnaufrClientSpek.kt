package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.schnauf.query.model.MetaData
import com.senacor.schnaufr.schnauf.query.model.Schnaufr
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo


val USERID = UUID();
const val USERNAME = "foo"
const val DISPLAYNAME = "bar"
const val FINDUSER_OPERATION = "findUser"

class SchnaufrClientSpek : Spek({


    class SchnaufrTestService() {
        private lateinit var disposable: Disposable;

        fun start() {
            disposable = RSocketFactory
                    .receive()
                    .acceptor { { setup, rSocket -> handler(setup, rSocket) } } // server handler RSocket
                    .transport(TcpServerTransport.create(8082))  // Netty websocket transport
                    .start().subscribe();
        }

        fun stop() {
            disposable.dispose()
        }

        private fun handler(setup: Setup, rSocket: RSocket): Single<RSocket> {
            val testSchnauf = Schnaufr(USERID, UUID(), USERNAME, DISPLAYNAME)
            return Single.just(object : AbstractRSocket() {
                override fun requestResponse(payload: Payload): Single<Payload> {
                    val operation = MetaData.fromJson(payload.metadataUtf8)?.operation;

                    if (FINDUSER_OPERATION.equals(operation) && payload.dataUtf8.equals(USERID.toString())) {
                        return return Single.just(DefaultPayload.text(testSchnauf.toJson()))
                    }
                    return Single.never()
                }
            });
        }
    }

    describe("schnaufr client") {
        val sut = SchnaufrClient()
        val testService = SchnaufrTestService()

        before {
            testService.start()
            sut.start()
        }
        after {
            sut.stop()
            testService.stop()
        }

        it("can find schnaufr by id") {

            val schnaufr = sut.getSchnaufrById(USERID).blockingGet();

            expectThat(schnaufr.displayName).isEqualTo(DISPLAYNAME);
            expectThat(schnaufr.username).isEqualTo(USERNAME);
            expectThat(schnaufr.id).isEqualTo(USERID);
        }


    }
})
