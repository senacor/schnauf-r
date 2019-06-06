package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.aUUID
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SchnauferRepositorySpek : Spek({

    describe("schnauf data management") {

        val client by mongoDB(port = 27017)
        val sut by memoized { SchnaufRepository(client) }

        it("can create and read schnaufs") {
            val submitter = aUUID()
            val schnauf = Schnauf(id = aUUID(), title = "integration-tests-sind-kacke-schnauf", submitter = submitter)
            sut.create(schnauf).blockingGet()

            val result = sut.readById(schnauf.id).blockingGet()
            expectThat(result.title).isEqualTo("integration-tests-sind-kacke-schnauf")
            expectThat(result.submitter).isEqualTo(submitter)
        }
    }
})
