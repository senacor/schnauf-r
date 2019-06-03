package com.senacor.schnaufr.schnauf

import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.litote.kmongo.reactivestreams.forEach
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

class RSocketServerSpek : Spek({

    describe("schnauf API") {
        val client by mongoDB(port = 27017)
        val repository by memoized { SchnaufRepository(client) }
        val schnauferAPI by memoized { RSocketServer(repository) }

        before {
            schnauferAPI.start()
        }

        after {
            schnauferAPI.stop()
        }

        it("should create new schnauf") {
            val rSocket: RSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .blockingGet()

            val response = rSocket.requestResponse(
                    DefaultPayload
                            .text("""
                                {"title": "first-schnauf"}
                                """,
                                    """
                        {"operation": "createSchnauf"}
                            """
                            )).blockingGet()

            //expectThat(response.dataUtf8).isEqualTo("huhu")
            val allSchnaufs = repository.readAll().toList().blockingGet()
            expectThat(allSchnaufs).hasSize(1)
        }

    }
})
