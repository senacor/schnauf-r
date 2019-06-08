package com.senacor.schnaufr.user

import com.senacor.schnaufr.*
import com.senacor.schnaufr.user.model.*
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

        val repository = SchnauferRepository(client)

        val server = SchnauferServer(MessageHandler(repository))

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

                repository.create(schnaufer).block()

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

                repository.create(schnaufer).block()

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

        context("when an avatar is requested by id") {

            it("returns an avatar") {
                val file = SchnauferRepository::class.java.getResourceAsStream("/avatars/ohmannnnnn.jpg")
                val avatarId = UUID()
                repository.saveAvatar(avatarId = avatarId, data = file).block()


                val requestPayload = DefaultPayload.create(AvatarByIdRequest(avatarId).toJson(), """{"operation": "findAvatar"}""")
                val response = rSocket.requestStream(requestPayload)
                    .map { it.data.convertToByteArray() }
                    .reduce(ByteArray(0)) { arr1, arr2 -> arr1.plus(arr2) }
                    .block()!!

                expectThat(response.size).isEqualTo(989664)
            }
        }
    }
})
