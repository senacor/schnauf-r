package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.UUID
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SchnauferRepositorySpek : Spek({

    describe("schnauf data management") {

        val client by mongoDB(port = 27017)
        val sut by memoized { SchnaufRepository(client) }

        it("can create and read schnaufs") {
            val schnauf = Schnauf(id = UUID(), title = "integration-tests-sind-kacke-schnauf")
            sut.create(schnauf).blockingGet()

            val result = sut.read(schnauf.id).blockingGet()
            expectThat(result.title).isEqualTo("integration-tests-sind-kacke-schnauf")
        }
    }
})
