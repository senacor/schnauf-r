package com.senacor.schnaufr.location

import com.senacor.schnaufr.mongoDB
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.awaitility.Awaitility.await
import org.reactivestreams.Publisher
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import reactor.test.test
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


class GeolocationServerSpek : Spek({

    describe("schaufr location API") {

        val mongoConnection by mongoDB(port = 27067)
        val geolocationRepository by memoized { GeolocationRepository(mongoConnection) }

        val sut = GeolocationServer(geolocationRepository)

        lateinit var rSocket: RSocket

        beforeEach {
            sut.start()
            rSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8097))
                    .start()
                    .block()!!
        }

        afterEach {
            sut.stop()
            geolocationRepository.deleteAll()
        }

        describe("receiving location updates") {

            it("does broadcast the update to clients") {

                val requestStream: Publisher<Payload> = Flux.just(
                        SchnaufrLocation(
                                UUID.randomUUID(),
                                Geolocation(23.0, 42.0, Instant.now())
                        ).asPayload()
                )

                rSocket.requestChannel(requestStream)
                        .doOnNext { println("Client received Schnaufr Position $it") }
                        .map { SchnaufrLocation.fromJson(it.dataUtf8) }
                        .take(1)
                        .test()
                        .thenAwait()
                        .expectNextMatches { schnaufr ->
                            schnaufr.currentLocation.latitude.equals(23.0)
                                    && schnaufr.currentLocation.longitude.equals(42.0)
                        }
                        .expectComplete()
                        .verify()

                await().atMost(5, TimeUnit.SECONDS).until {
                    geolocationRepository.readAll().collectList().block()!!.isNotEmpty()
                }
            }
        }
    }
})
