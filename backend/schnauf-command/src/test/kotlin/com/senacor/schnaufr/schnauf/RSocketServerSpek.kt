package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.model.CreateSchnaufRequest
import io.rsocket.kotlin.Closeable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.exceptions.ApplicationException
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.awaitility.Awaitility.await
import org.litote.kmongo.rxjava2.blockingGet
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message
import java.util.concurrent.TimeUnit

const val PORT: Int = 8090

class RSocketServerSpek : Spek({

    describe("schnauf API") {
        val client by mongoDB(port = 27067)
        val schnaufRepository by memoized { SchnaufRepository(client) }
        val messageHandler by memoized { MessageHandler(schnaufRepository) }
        val rSocketServer by memoized { RSocketServer(messageHandler, PORT) }
        lateinit var rSocket: RSocket
        lateinit var closeable: Closeable

        fun createSchnauf(title: String, submitter: String, recipients: List<String> = listOf()): Schnauf {
            val schnaufRequest = CreateSchnaufRequest(title, submitter, recipients)
            val createPayload = DefaultPayload.text(schnaufRequest.toJson(), """{"operation": "createSchnauf"}""")
            val addResponse = rSocket.requestResponse(createPayload).blockingGet()
            return Schnauf.fromPayload(addResponse)
        }

        before {
            closeable = rSocketServer.start().blockingGet()
            rSocket = RSocketFactory
                    .connect()
                    .transport(TcpClientTransport.create(PORT))
                    .start()
                    .blockingGet()
        }

        beforeEach {
            schnaufRepository.deleteAll().blockingGet()
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
                val title = "first-schnauf"
                val submitter = "darth vader"
                val createdSchnauf = createSchnauf(title, submitter)

                // assert creation
                expectThat(createdSchnauf.title).isEqualTo(title)
                expectThat(createdSchnauf.submitter).isEqualTo(submitter)

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
                val title = "first-schnauf"
                val submitter = "darth vader"
                createSchnauf(title, submitter)

                // get all schnaufs
                val getAllPayload = DefaultPayload.text("", """{"operation": "getAllSchnaufs"}""")
                rSocket.requestStream(getAllPayload)
                        .take(5)
                        .test()
                        .awaitCount(1)
                        .assertValueCount(1)
                        .assertComplete()
                        .assertValue {
                            val schnauf = Schnauf.fromPayload(it)
                            schnauf.submitter == submitter && schnauf.title == title
                        }
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

                val getAllPayload = DefaultPayload.text("", """{"operation": "getAllSchnaufs","principal":"obi-wan"}""")
                rSocket.requestStream(getAllPayload)
                        .take(5)
                        .test()
                        .awaitCount(3)
                        .assertValueCount(3)
                        .assertComplete()
            }
        }
    }
})
