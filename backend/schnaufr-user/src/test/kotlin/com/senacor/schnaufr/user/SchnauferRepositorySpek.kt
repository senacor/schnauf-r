package com.senacor.schnaufr.user

import com.senacor.schnaufr.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.toMono
import strikt.api.expectThat
import strikt.assertions.*
import java.io.*

class SchnauferRepositorySpek : Spek({

    describe("schnaufer master data management") {

        val client by mongoDB(port = 27017)
        val database by lazy { client.getDatabase("schnauf") }

        val sut by memoized { SchnauferRepository(client) }

        before {
            database.createCollection("schnaufers").toMono().block()
        }

        it("can read schnaufers") {
            val schaufer = Schnaufer(id = UUID(), avatarId = UUID(), username = "momann", displayName = "Moni")
            sut.create(schaufer).block()

            val result = sut.read(schaufer.id).block()
            expectThat(result?.displayName).isEqualTo("Moni")
        }

        it("can read avatars") {
            val file = SchnauferRepository::class.java.getResourceAsStream("/avatars/avatar_moni.jpg")
            val schnauferId = UUID()
            sut.saveAvatar(schnauferId = schnauferId, data = file).block()

            val result = sut.readAvatar(schnauferId).reduce(ByteArray(0)) { arr1, arr2 ->
                arr1.plus(arr2)
            }.block()!!

            expectThat(result).isEmpty()

        }

    }
})
