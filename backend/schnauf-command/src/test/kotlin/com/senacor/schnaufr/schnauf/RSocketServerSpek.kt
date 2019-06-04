package com.senacor.schnaufr.schnauf

import io.rsocket.kotlin.Closeable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.awaitility.Awaitility.await
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.concurrent.TimeUnit

class RSocketServerSpek : Spek({

    describe("schnauf API") {
        val client by mongoDB(port = 27017)
        val repository by memoized { SchnaufRepository(client) }
        val rSocketServer by memoized { RSocketServer(repository) }

        lateinit var closeable: Closeable

        before {
            closeable = rSocketServer.start().blockingGet()
        }

        after {
            closeable.close()
        }

        it("should create new schnauf") {
            val rSocket: RSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .blockingGet()

            val response = rSocket.requestResponse(
                    DefaultPayload
                            .text(
                                    """
                                        {"title": "first-schnauf"}
                                        """,
                                    """
                                        {"operation": "createSchnauf"}
                                        """
                            )).blockingGet()

            expectThat(response.dataUtf8).isEqualTo("huhu")

            await().atMost(5, TimeUnit.SECONDS).until {
                !repository.readAll().toList().blockingGet().isEmpty()
            }
        }
    }
})
