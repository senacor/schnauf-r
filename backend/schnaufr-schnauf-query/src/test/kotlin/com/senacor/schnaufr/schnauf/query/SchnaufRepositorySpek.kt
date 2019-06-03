package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.UUID
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.rxjava2.blockingAwait
import org.litote.kmongo.rxjava2.blockingGet
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SchnaufRepositorySpek : Spek({

    describe("schnaufer query repository") {

        val client by mongoDB(port = 27017)
        val database by lazy { client.getDatabase("schnauf") }

        val sut by memoized { SchnaufRepository(client) }

        before {
            database.createCollection("schnauf").blockingAwait();

            val collection = database.getCollection<Schnauf>();
            collection.insertOne(Schnauf(UUID(), "Christoph", "test-schnauf")).blockingAwait();
        }

        it("can read schnaufs") {

            val result = sut.read().blockingGet()

            expectThat(result.author).isEqualTo("Christoph");
            expectThat(result.content).isEqualTo("test-schnauf");
        }
    }
})
