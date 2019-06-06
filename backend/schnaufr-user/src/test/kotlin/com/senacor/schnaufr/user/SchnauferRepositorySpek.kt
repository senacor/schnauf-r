package com.senacor.schnaufr.user

import com.mongodb.reactivestreams.client.gridfs.helpers.AsyncStreamHelper
import com.senacor.schnaufr.UUID
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.toMono
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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
            expectThat(result.displayName).isEqualTo("Moni")
        }

        it("can read avatars") {
            val file = File("/home/mpeters/Downloads/VID_20190603_073254.mp4")
            val inputStream = FileInputStream(file)
            val schnauferId = UUID()
            sut.saveAvatar(schnauferId = schnauferId, data = inputStream).block()

            val outputStream = AsyncStreamHelper.toAsyncOutputStream(FileOutputStream(File("./test.mp4")))
            sut.readAvatar(schnauferId, outputStream).map {
                it.toMono().block()
            }.block()

            outputStream.close()
        }

    }
})
