package com.senacor.schnaufr.location

import com.senacor.schnaufr.mongoDB
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.awaitility.Awaitility.await
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import reactor.test.test
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


class GeolocationServerSpek : Spek({

    describe("schaufr location API") {

        val initialPositionCount: Long = 10

        val mongoConnection by mongoDB(port = 27067)
        val geolocationRepository by memoized { GeolocationRepository(mongoConnection) }

        val sut = GeolocationServer(geolocationRepository)

        lateinit var rSocket: RSocket

        beforeEach {
            sut.start()

            1.rangeTo(initialPositionCount).forEach { i ->
                geolocationRepository.upsert(aLocation(i)).block()
            }

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

            it("does send current locations to new client") {

                rSocket.requestChannel(Flux.just(aLocation().asPayload()))
                        .map { SchnaufrLocation.fromJson(it.dataUtf8) }
                        .take(initialPositionCount)
                        .test()
                        .thenAwait()
                        .expectNextCount(initialPositionCount)
                        .expectComplete()
                        .verify()
            }

            it("does send new locations to new client") {

                rSocket.requestChannel(Flux.just(aLocation().asPayload()))
                        .map { SchnaufrLocation.fromJson(it.dataUtf8) }
                        .take(initialPositionCount)
                        .test()
                        .thenAwait()
                        .expectNextCount(initialPositionCount)
                        .expectComplete()
                        .verify()

                await().atMost(5, TimeUnit.SECONDS).until {
                    geolocationRepository.readAll().collectList().block()!!.size >= 10
                }
            }
        }
    }
})

private fun aLocation() = aLocation(1)

private fun aLocation(n: Long): SchnaufrLocation {
    return SchnaufrLocation(
            UUID.randomUUID(),
            Geolocation(n + .5, n + 1.5, Instant.now()))
}
