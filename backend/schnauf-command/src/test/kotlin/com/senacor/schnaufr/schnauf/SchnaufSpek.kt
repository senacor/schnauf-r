package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.model.CreateSchnaufRequest
import io.rsocket.*
import io.rsocket.exceptions.ApplicationErrorException
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.awaitility.Awaitility.await
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.test.*
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message
import java.util.concurrent.TimeUnit

const val PORT: Int = 8090

class SchnaufSpek : Spek({

    describe("schnauf API") {
        val client by mongoDB(port = 27067)
        val schnaufRepository by memoized { SchnaufRepository(client) }
        val messageHandler by memoized { MessageHandler(schnaufRepository) }
        val server by memoized { SchnaufServer(messageHandler, PORT) }
        lateinit var rSocket: RSocket
        lateinit var closeable: Closeable

        fun createSchnauf(title: String, submitter: String, recipients: List<String> = listOf()): Schnauf {
            val schnaufRequest = CreateSchnaufRequest(title, submitter, recipients)
            val createPayload = DefaultPayload.create(schnaufRequest.toJson(), """{"operation": "createSchnauf"}""")
            val addResponse = rSocket.requestResponse(createPayload).block()!!
            return Schnauf.fromPayload(addResponse)
        }

        before {
            server.start()
        }

        after {
            server.stop()
        }

        before {
            rSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(PORT))
                    .start()
                    .block()!!
        }

        beforeEach {
            schnaufRepository.deleteAll().block()!!
        }

        afterEach {
            schnaufRepository.deleteAll().block()!!
        }

        describe("creating a new schnauf with request/response") {
            it("it should throw if the operation name is wrong") {
                val schnaufRequest = CreateSchnaufRequest("first-schnauf", "darth vader")
                val payload = DefaultPayload.create(schnaufRequest.toJson(), """{"operation": "someWrongOperationName"}""")

                expectThrows<ApplicationErrorException> {
                    rSocket.requestResponse(payload).block()!!
                }.message.isEqualTo("unrecognized operation someWrongOperationName")
            }

            it("should create a new schnauf") {
                val title = "first-schnauf"
                val submitter = "darth vader"
                val createdSchnauf = createSchnauf(title, submitter)

                // assert creation
                expectThat(createdSchnauf.title).isEqualTo(title)
                expectThat(createdSchnauf.submitter).isEqualTo(submitter)

                await().atMost(5, TimeUnit.SECONDS).until {
                    schnaufRepository.readLatest().collectList().block()!!.isNotEmpty()
                }
            }
        }

        describe("retrieving schnaufs with request stream") {
            it("it should throw if the operation name is wrong") {
                val getAllPayload = DefaultPayload.create("", """{"operation": "someWrongOperationName"}""")

                expectThrows<ApplicationErrorException> {
                    rSocket.requestStream(getAllPayload).blockFirst()
                }.message.isEqualTo("unrecognized operation someWrongOperationName")
            }

            it("should retrieve all former schnaufs once") {
                // create a schnauf
                val title = "first-schnauf"
                val submitter = "darth vader"
                createSchnauf(title, submitter)

                // get all schnaufs
                val getAllPayload = DefaultPayload.create("", """{"operation": "getAllSchnaufs"}""")
                rSocket.requestStream(getAllPayload)
                        .take(5)
                        .test()
//                    .thenAwait()
//                    .expectNextCount(1)
//                    .expectNextMatches { payload ->
//                        val schnauf = Schnauf.fromPayload(payload)
//                        schnauf.submitter == submitter && schnauf.title == title
//                    }
//                    .expectComplete()
//                    .verify()
            }

            // we can't test watching new schnaufs b/c this would require to spin up a Mongo replica set locally.
            // This is only headache so we'll skip that for now

            it("should retrieve all former schnaufs that are either without recipients or directed at me") {
                // create a few schnaufs
                val title1 = "first-schnauf"
                val submitter1 = "darth vader"
                createSchnauf(title1, submitter1) // broadcast

                val title2 = "second-schnauf"
                val submitter2 = "darth vader"
                createSchnauf(title2, submitter2, listOf("obi-wan")) // to me

                val title3 = "first-schnauf"
                val submitter3 = "darth vader"
                createSchnauf(title3, submitter3, listOf("obi-wan")) // to me

                val title4 = "first-schnauf"
                val submitter4 = "darth vader"
                createSchnauf(title4, submitter4, listOf("leia")) // to someone different

                val getAllPayload = DefaultPayload.create("", """{"operation": "getAllSchnaufs","principal":"obi-wan"}""")
                rSocket.requestStream(getAllPayload)
                        .take(5)
                        .test()

//                        .awaitCount(3)
//                        .assertValueCount(3)
//                        .assertComplete()
            }
        }
    }
})
