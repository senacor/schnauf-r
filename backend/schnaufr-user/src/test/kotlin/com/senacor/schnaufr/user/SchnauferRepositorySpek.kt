package com.senacor.schnaufr.user

import com.senacor.schnaufr.UUID
import org.litote.kmongo.rxjava2.blockingAwait
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.*

class SchnauferRepositorySpek : Spek({

    describe("schnaufer master data management") {

        val client by mongoDB(port = 27017)
        val database by lazy { client.getDatabase("schnauf") }

        val sut by memoized { SchnauferRepository(client) }

        before {
            database.createCollection("schnaufers").blockingAwait()
        }

        it("can read schnaufers") {
            val schaufer = Schnaufer(id = UUID(), avatarId = UUID(), username = "momann", displayName = "Moni")
            sut.create(schaufer).blockingGet()

            val result = sut.read(schaufer.id).blockingGet()
            expectThat(result.displayName).isEqualTo("Moni")
        }

        it("can read avatars") {

            val stream = this::class.java.getResource("/avatars/avatar_moni.jpg").openStream()
            val schnauferId = UUID()
            sut.saveAvatar(schnauferId = schnauferId, data = stream).blockingAwait()

            val gridFSFile = sut.readAvatar(schnauferId).blockingGet()

            expectThat(gridFSFile.filename).isEqualTo("avatar")
            expectThat(gridFSFile.metadata).hasEntry("schnauferId", schnauferId)
        }

    }
})
