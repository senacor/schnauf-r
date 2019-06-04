package com.senacor.schnaufr.user

import com.senacor.schnaufr.UUID
import io.rsocket.kotlin.*
import io.rsocket.kotlin.exceptions.ApplicationException
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import io.rsocket.kotlin.transport.netty.server.NettyContextCloseable
import org.litote.kmongo.rxjava2.blockingAwait
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.*
import strikt.assertions.*

class SchnauferSpek : Spek({

    describe("schnaufer API") {
        val client by mongoDB(port = 27017)
        val database by lazy { client.getDatabase("schnauf") }

        before {
            database.createCollection("schnaufers").blockingAwait()
        }

        val schnauferRepository = SchnauferRepository(client)

        val sut = SchnauferServer(MessageHandler(schnauferRepository))

        before {
            sut.setup().blockingGet()
        }

        after {


        }

        val rSocket: RSocket by lazy {
            RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(9090))
                .start()
                .blockingGet()
        }

        context("when a user is requested by id") {

            it("it return 'userNotFound' for unknown user") {
                val findUserPayload = DefaultPayload.text(UUID().toString(), """{"operation": "findUserById"}""")

                expectThrows<ApplicationException> {
                    rSocket.requestResponse(findUserPayload).blockingGet()
                }.message.isEqualTo("userNotFound")
            }

            it("returns a schnaufer") {
                val schnaufer = Schnaufer(
                    id = UUID(),
                    avatarId = UUID(),
                    username = "schnauferusername",
                    displayName = "screenSchnaufer"
                )

                schnauferRepository.create(schnaufer).blockingGet()

                val requestPayload = DefaultPayload.text(schnaufer.id.toString(), """{"operation": "findUserById"}""")
                val response = rSocket.requestResponse(requestPayload).blockingGet()
                val foundSchnaufer = Schnaufer.fromJson(response.dataUtf8)
                expectThat(foundSchnaufer).isEqualTo(schnaufer)
            }
        }

        context("when a user is requested by username") {

            it("should return empty for unknown user") {
                val findUserPayload = DefaultPayload.text("Hermann", """{"operation": "findUserByUsername"}""")

                expectThrows<ApplicationException> {
                    rSocket.requestResponse(findUserPayload).blockingGet()
                }.message.isEqualTo("userNotFound")
            }
        }

    }
})
