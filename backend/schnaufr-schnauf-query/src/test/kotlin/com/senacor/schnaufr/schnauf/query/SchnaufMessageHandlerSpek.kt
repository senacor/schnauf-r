package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.schnauf.query.model.Schnauf
import com.senacor.schnaufr.schnauf.query.model.SchnaufFeedEntry
import com.senacor.schnaufr.schnauf.query.model.Schnaufr
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.Single
import io.rsocket.kotlin.DefaultPayload
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import java.util.*

class SchnaufMessageHandlerSpek : Spek({
    val schnaufId = UUID.randomUUID()
    val authorId = UUID.randomUUID()
    val title = "alles-bloed-hier-schnauf"
    val avatarId = UUID.randomUUID()
    val username = "randomSurfer"
    val displayName = "Matze Pewters"

    describe("schnauf message handler") {
        val mockedSchnaufClient = mockk<SchnaufClient>()
        every { mockedSchnaufClient.getAllSchnaufs() } returns Flowable.just(Schnauf(schnaufId, authorId, title))
        val mockedSchnaufrClient =  mockk<SchnaufrClient>()
        every { mockedSchnaufrClient.getSchnaufrById(authorId) } returns Single.just(Schnaufr(authorId, avatarId, username, displayName ))
        val sut = SchnaufMessageHandler(mockedSchnaufClient, mockedSchnaufrClient)


        it("returns an aggregated stream of schnauf feed entries") {
            val entries = sut.requestStream(DefaultPayload.EMPTY).blockingIterable().iterator()

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
