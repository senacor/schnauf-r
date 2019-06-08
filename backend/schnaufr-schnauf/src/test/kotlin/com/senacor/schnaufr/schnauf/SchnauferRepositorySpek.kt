package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.model.Schnauf
import com.senacor.schnaufr.mongoDB
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SchnauferRepositorySpek : Spek({

    describe("schnauf data management") {

        val client by mongoDB(port = 27017)
        val sut by memoized { SchnaufRepository(client) }

        it("can create and read schnaufs") {
            val submitter = UUID()
            val schnauf = Schnauf(id = UUID(), title = "integration-tests-sind-kacke-schnauf", submitter = submitter)
            sut.create(schnauf).block()!!

            val result = sut.readById(schnauf.id).block()!!
            expectThat(result.title).isEqualTo("integration-tests-sind-kacke-schnauf")
            expectThat(result.submitter).isEqualTo(submitter)
        }
    }
})
