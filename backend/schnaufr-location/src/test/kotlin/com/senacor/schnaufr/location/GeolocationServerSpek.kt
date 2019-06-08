package com.senacor.schnaufr.location

import com.senacor.schnaufr.mongoDB
import io.rsocket.Payload
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.reactivestreams.Publisher
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant
import java.util.*


class GeolocationServerSpek : Spek({

    describe("Schaufr Geolocation server") {

        val mongoConnection by mongoDB(port = 27067)
        val geolocationRepository by memoized { GeolocationRepository(mongoConnection) }

        val sut = GeolocationServer(geolocationRepository)


        before {
            sut.start()
        }
        after {
            sut.stop()
        }

        afterEach {
            geolocationRepository.deleteAll()
        }

        it("can respond on channel") {

            val rSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8097))
                    .start()
                    .block()!!

            val requestStream: Publisher<Payload> =
                    Flux.just(SchnaufrPosition(
                            UUID.randomUUID(),
                            Geolocation(10.0, 10.0, Instant.now())).asPayload())

            val firstResponse = rSocket.requestChannel(requestStream)
                    .doOnNext { println("Received Schnaufr Position $it") }
                    .map { SchnaufrPosition.fromJson(it.dataUtf8) }
                    .blockFirst()!!

            expectThat(firstResponse.geolocation.latitude).isEqualTo(23.0)
            expectThat(firstResponse.geolocation.longitude).isEqualTo(42.0)
        }
    }
})
