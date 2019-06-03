package com.senacor.schnaufr.user

import io.rsocket.kotlin.*
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SchnauferAPISpek : Spek({

    describe("schnaufer API") {
        val schnauferAPI = SchnauferAPI()
        before {
            schnauferAPI.setup()
        }

        after {
            schnauferAPI.stop()
        }

        it("should respond with 'huhu'") {

            val rSocket: RSocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(9090))
                .start()
                .blockingGet()

            val response = rSocket.requestResponse(DefaultPayload.text("hi")).blockingGet()


            expectThat(response.dataUtf8).isEqualTo("huhu")

        }

    }
})
