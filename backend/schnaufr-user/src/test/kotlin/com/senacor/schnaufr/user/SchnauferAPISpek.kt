package com.senacor.schnaufr.user

import io.rsocket.kotlin.*
import io.rsocket.kotlin.exceptions.ApplicationException
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import io.rsocket.kotlin.transport.netty.server.NettyContextCloseable
import org.litote.kmongo.rxjava2.blockingAwait
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.util.UUID

class SchnauferAPISpek : Spek({

    describe("schnaufer API") {
        val client by mongoDB(port = 27017)
        val database by lazy { client.getDatabase("schnauf") }

        val sut by memoized { SchnauferRepository(client) }

        before {
            database.createCollection("schnaufers").blockingAwait()
        }


        val schnauferRepository = SchnauferRepository(client)

        val schnauferAPI = SchnauferServer(MessageHandler(schnauferRepository))

        lateinit var schnaufer: NettyContextCloseable
        before {
            schnaufer = schnauferAPI.setup().blockingGet()
        }

        after {
            schnaufer.close()
        }


        it("find user should return userNotFound for unknown user") {

            val rSocket: RSocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(9090))
                .start()
                .blockingGet()

            val findUserPayload = DefaultPayload.text("matzeP", "findUser")
            expectThrows<ApplicationException>() {
                rSocket.requestResponse(findUserPayload).blockingGet()
            }
        }

        it("should return empty for unknown user") {
            val rSocket: RSocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(9090))
                .start()
                .blockingGet()

            val unknownUUID = UUID.randomUUID()
            val findUserPayload = DefaultPayload.text(unknownUUID.toString(), "findUser")
            val response = rSocket.requestResponse(findUserPayload).blockingGet()
            expectThat(response.dataUtf8).isEqualTo("userNotFound")
        }

        it("should return schnaufer") {
            val schnaufer =
                Schnaufer(UUID.randomUUID(), UUID.randomUUID(), "schnauferusername", "screenSchnaufer")
            schnauferRepository.create(schnaufer).blockingGet()

            val rSocket: RSocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(9090))
                .start()
                .blockingGet()

            val requestPayload = DefaultPayload.text(schnaufer.id.toString(), "findUser")
            val response = rSocket.requestResponse(requestPayload).blockingGet()
            val foundSchnaufer = response.dataUtf8
            expectThat(foundSchnaufer).isEqualTo(schnaufer.toString())
        }

    }
})
