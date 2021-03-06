package com.senacor.schnaufr.query

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.query.SchnauferClient
import com.senacor.schnaufr.query.model.MetaData
import com.senacor.schnaufr.query.model.SchnauferByIdRequest
import com.senacor.schnaufr.query.model.Schnaufr
import io.rsocket.*
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Mono
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SchnaufrClientSpek : Spek({
    val USERID = UUID();
    val USERNAME = "foo"
    val DISPLAYNAME = "bar"
    val FINDUSER_OPERATION = "findUserById"

    class SchnaufrTestService {
        private lateinit var disposable: Closeable

        fun start() {
            disposable = RSocketFactory
                    .receive()
                    .acceptor(this::handler)
                    .transport(TcpServerTransport.create(8082))  // Netty websocket transport
                    .start()
                    .block()!!
        }

        fun stop() {
            disposable.dispose()
        }

        fun handler(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
            val testSchnauf = Schnaufr(USERID, UUID(), USERNAME, DISPLAYNAME)
            return Mono.just(object : AbstractRSocket() {
                override fun requestResponse(payload: Payload): Mono<Payload> {
                    val operation = MetaData.fromJson(payload.metadataUtf8)?.operation;

                    if (FINDUSER_OPERATION.equals(operation) && SchnauferByIdRequest.fromJson(payload.dataUtf8).id == USERID) {
                        return Mono.just(DefaultPayload.create(testSchnauf.toJson()))
                    }
                    return Mono.never()
                }
            });
        }
    }

    describe("schnaufr client") {
        val sut = SchnauferClient()
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

            val schnaufr = sut.getSchnaufrById(USERID).block()!!

            expectThat(schnaufr.displayName).isEqualTo(DISPLAYNAME)
            expectThat(schnaufr.username).isEqualTo(USERNAME)
            expectThat(schnaufr.id).isEqualTo(USERID)
        }


    }
})
