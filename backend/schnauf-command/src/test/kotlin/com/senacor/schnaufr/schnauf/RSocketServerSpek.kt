package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.model.CreateSchnaufRequest
import io.rsocket.kotlin.Closeable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.exceptions.ApplicationException
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.awaitility.Awaitility.await
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message
import java.util.concurrent.TimeUnit

class RSocketServerSpek : Spek({

    describe("schnauf API") {
        val client by mongoDB(port = 27067)
        val schnaufRepository by memoized { SchnaufRepository(client) }
        val messageHandler by memoized { MessageHandler(schnaufRepository) }
        val rSocketServer by memoized { RSocketServer(messageHandler) }
        lateinit var rSocket: RSocket
        lateinit var closeable: Closeable

        before {
            closeable = rSocketServer.start().blockingGet()
            rSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(8080))
                    .start()
                    .blockingGet()
        }

        after {
            closeable.close()
        }

        afterEach {
            schnaufRepository.deleteAll()
        }

        describe("creating a new schnauf with request/response") {
            it("it should throw if the operation name is wrong") {
                val schnaufRequest = CreateSchnaufRequest("first-schnauf", "darth vader")
                val payload = DefaultPayload.text(schnaufRequest.toJson(), """{"operation": "someWrongOperationName"}""")

                expectThrows<ApplicationException> {
                    rSocket.requestResponse(payload).blockingGet()
                }.message.isEqualTo("unrecognized operation someWrongOperationName")
            }

            it("should create a new schnauf") {
                val schnaufRequest = CreateSchnaufRequest("first-schnauf", "darth vader")
                val payload = DefaultPayload.text(schnaufRequest.toJson(), """{"operation": "createSchnauf"}""")

                val response = rSocket.requestResponse(payload).blockingGet()

                val createdSchnauf = Schnauf.fromPayload(response)

                expectThat(createdSchnauf.title).isEqualTo(schnaufRequest.title)
                expectThat(createdSchnauf.submitter).isEqualTo(schnaufRequest.submitter)

                await().atMost(5, TimeUnit.SECONDS).until {
                    schnaufRepository.readLatest().toList().blockingGet().isNotEmpty()
                }
            }
        }

        describe("retrieving schnaufs with request stream") {
            it("it should throw if the operation name is wrong") {
                val getAllPayload = DefaultPayload.text("", """{"operation": "someWrongOperationName"}""")

                expectThrows<ApplicationException> {
                    rSocket.requestStream(getAllPayload).blockingFirst()
                }.message.isEqualTo("unrecognized operation someWrongOperationName")
            }

            it("should retrieve all former schnaufs once") {
                // create a schnauf
                val schnaufRequest = CreateSchnaufRequest("first-schnauf", "darth vader")
                val createPayload = DefaultPayload.text(schnaufRequest.toJson(), """{"operation": "createSchnauf"}""")
                val addResponse = rSocket.requestResponse(createPayload).blockingGet()
                val createdSchnauf = Schnauf.fromPayload(addResponse)

                // get all schnaufs
                val getAllPayload = DefaultPayload.text("", """{"operation": "getAllSchnaufs"}""")
                val getAllResponse = rSocket.requestStream(getAllPayload).blockingFirst()
                val requestedSchnauf = Schnauf.fromPayload(getAllResponse)

                // assert creation
                expectThat(createdSchnauf.title).isEqualTo(schnaufRequest.title)
                expectThat(createdSchnauf.submitter).isEqualTo(schnaufRequest.submitter)

                // assert getting all schnaufs
                expectThat(requestedSchnauf.title).isEqualTo(schnaufRequest.title)
                expectThat(requestedSchnauf.submitter).isEqualTo(schnaufRequest.submitter)
            }

            // we can't test watching new schnaufs b/c this would require
            // to spin up a Mongo replica set locally. This is only headache
            // so we'll skip that for now
        }
    }
})
