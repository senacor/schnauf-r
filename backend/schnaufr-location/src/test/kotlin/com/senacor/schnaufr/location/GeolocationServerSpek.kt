package com.senacor.schnaufr.location

import com.senacor.schnaufr.mongoDB
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import org.reactivestreams.Publisher
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

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

        it("can respond") {
            val rSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .block()!!

            val channel = rSocket.requestChannel(Publisher {  })


        }
    }
})
