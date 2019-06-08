package com.senacor.schnaufr.user

import com.senacor.schnaufr.*
import com.senacor.schnaufr.user.model.Schnaufer
import com.senacor.schnaufr.user.model.SchnauferByIdRequest
import com.senacor.schnaufr.user.model.SchnauferByUsernameRequest
import io.rsocket.*
import io.rsocket.exceptions.ApplicationErrorException
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.toMono
import strikt.api.*
import strikt.assertions.*

class SchnauferSpek : Spek({

    describe("schnaufer API") {
        val client by mongoDB(port = 27017)
        val database by lazy { client.getDatabase("schnauf") }

        before {
            database.createCollection("schnaufers").toMono().block()
        }

        val schnauferRepository = SchnauferRepository(client)

        val server = SchnauferServer(MessageHandler(schnauferRepository))

        before {
            server.start()
        }

        after {
            server.stop()
        }

        val rSocket: RSocket by memoized(
            mode = CachingMode.SCOPE,
            factory = {
                RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .block()!!
            },
            destructor = { it.onClose().block() }
        )

        context("when a user is requested by id") {

            it("returns a schnaufer") {

                val schnaufer = Schnaufer(
                        id = UUID(),
                        avatarId = UUID(),
                        username = "schnauferusername",
                        displayName = "screenSchnaufer"
                )

                schnauferRepository.create(schnaufer).block()

                val schnauferByIdRequest = SchnauferByIdRequest(schnaufer.id)

                val requestPayload = DefaultPayload.create(schnauferByIdRequest.toJson(), """{"operation": "findUserById"}""")
                val response = rSocket.requestResponse(requestPayload).block()!!
                val foundSchnaufer = Schnaufer.fromJson(response.dataUtf8)
                expectThat(foundSchnaufer).isEqualTo(schnaufer)
            }

            it("it return 'userNotFound' for unknown user") {
                val findUserPayload = DefaultPayload.create(SchnauferByIdRequest(UUID()).toJson(), """{"operation": "findUserById"}""")

                expectThrows<ApplicationErrorException> {
                    rSocket.requestResponse(findUserPayload).block()
                }.message.isEqualTo("userNotFound")
            }
        }

        context("when a user is requested by username") {

            it("returns a schnaufer") {
                val schnaufer = Schnaufer(
                        id = UUID(),
                        avatarId = UUID(),
                        username = "heinz",
                        displayName = "Heinzi"
                )

                schnauferRepository.create(schnaufer).block()

                val schnauferByUsernameRequest = SchnauferByUsernameRequest(schnaufer.username)

                val requestPayload = DefaultPayload.create(schnauferByUsernameRequest.toJson(), """{"operation": "findUserByUsername"}""")
                val response = rSocket.requestResponse(requestPayload).block()!!
                val foundSchnaufer = Schnaufer.fromJson(response.dataUtf8)
                expectThat(foundSchnaufer).isEqualTo(schnaufer)
            }

            it("should return empty for unknown user") {
                val findUserPayload = DefaultPayload.create(SchnauferByUsernameRequest("Hermann").toJson(), """{"operation": "findUserByUsername"}""")

                expectThrows<ApplicationErrorException> {
                    rSocket.requestResponse(findUserPayload).block()
                }.message.isEqualTo("userNotFound")
            }
        }

    }
})
