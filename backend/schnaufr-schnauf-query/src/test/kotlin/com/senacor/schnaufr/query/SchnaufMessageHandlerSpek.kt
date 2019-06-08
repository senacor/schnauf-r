package com.senacor.schnaufr.query

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.query.model.MetaData
import com.senacor.schnaufr.query.model.Schnauf
import com.senacor.schnaufr.query.model.SchnaufFeedEntry
import com.senacor.schnaufr.query.model.Schnaufr
import io.mockk.every
import io.mockk.mockk
import io.rsocket.util.DefaultPayload
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse

class SchnaufMessageHandlerSpek : Spek({
    val schnaufId = UUID()
    val authorId = UUID()
    val title = "alles-bloed-hier-schnauf"
    val avatarId = UUID()
    val username = "randomSurfer"
    val displayName = "Matze Pewters"

    describe("schnauf message handler") {
        val mockedSchnaufClient = mockk<SchnaufClient>()
        every { mockedSchnaufClient.getAllSchnaufs(any()) } returns Flux.just(Schnauf(schnaufId, authorId, title))
        val mockedSchnaufrClient =  mockk<SchnauferClient>()
        every { mockedSchnaufrClient.getSchnaufrById(authorId) } returns Mono.just(Schnaufr(authorId, avatarId, username, displayName))
        val sut = SchnaufMessageHandler(mockedSchnaufClient, mockedSchnaufrClient)


        it("returns an aggregated stream of schnauf feed entries") {
            val entries = sut.requestStream(DefaultPayload.create("", MetaData("getAllSchnaufs", -1, java.util.UUID.randomUUID()).toJson())).toIterable().iterator()

            expectThat(entries.hasNext())
            val entry = SchnaufFeedEntry.fromJson(entries.next().dataUtf8)
            expectThat(entry.title).isEqualTo(title)
            expectThat(entry.author.avatarId).isEqualTo(avatarId)
            expectThat(entry.author.displayName).isEqualTo(displayName)
            expectThat(entry.author.username).isEqualTo(username)
            expectThat(entries.hasNext()).isFalse()
        }
    }
})
