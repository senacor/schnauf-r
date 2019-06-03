package com.senacor.schnaufr.schnauf

import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class RSocketServerSpek : Spek({

    describe("schnauf API") {
        val schnauferAPI = RSocketServer()

        before {
            schnauferAPI.start()
        }

        after {
            schnauferAPI.stop()
        }

        it("should respond with 'huhu'") {
            val rSocket: RSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .blockingGet()


            val response = rSocket.requestResponse(DefaultPayload.text("hi")).blockingGet()

            expectThat(response.dataUtf8).isEqualTo("huhu")
        }

    }
})
