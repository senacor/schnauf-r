package com.senacor.schnaufr.gateway

import io.mockk.every
import io.mockk.mockk
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import strikt.api.expectThat
import strikt.assertions.isFalse

class RSocketSchnaufQueryServerSpek : Spek({
    describe("schnauf query server") {
        val schnaufMessageHandler = mockk<SchnaufMessageHandler>()
        every { schnaufMessageHandler.requestStream(any()) } returns Flux.empty()

        val server = SchnaufQueryServer(schnaufMessageHandler, 8080)

        before {
            server.start()
        }
        after {
            server.stop()
        }

        it("can respond") {
            val rsSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .block()!!

            val iterator = rsSocket
                .requestStream(DefaultPayload.create(DefaultPayload.EMPTY_BUFFER))
                .toIterable().iterator()

            expectThat(iterator.hasNext()).isFalse()
        }
    }
})
