package com.senacor.schnaufr.schnauf.query

import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isFalse

class RSocketSchnaufQueryServerSpek : Spek({

    describe("schnauf query server") {
        val sut = RSocketSchnaufQueryServer()


        before {
            sut.start()
        }
        after {
            sut.stop()
        }

        it("can respond") {
            val rsSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .blockingGet()

            val iterator = rsSocket.requestStream(DefaultPayload.EMPTY).blockingIterable().iterator()

            expectThat(iterator.hasNext()).isFalse()
        }
    }
})
