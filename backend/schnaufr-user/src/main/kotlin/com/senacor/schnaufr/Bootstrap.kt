package com.senacor.schnaufr

import com.senacor.schnaufr.user.SchnauferAPI
import com.senacor.schnaufr.user.SchnauferRepository
import io.reactivex.rxkotlin.subscribeBy
import io.vertx.core.*
import io.vertx.core.logging.LoggerFactory

class Bootstrap(
    private val schnauferRepository: SchnauferRepository
) : AbstractVerticle() {

    companion object {
        private val logger = LoggerFactory.getLogger("Bootstrap")
    }

    private lateinit var closable: io.rsocket.kotlin.Closeable

    override fun start(startFuture: Future<Void>) {
        SchnauferAPI(schnauferRepository).setup().subscribeBy(
            onSuccess = {
                closable = it
                startFuture.complete()
            },
            onError = { startFuture.fail(it) }
        )
    }

    override fun stop(stopFuture: Future<Void>) {
        closable.close().subscribe { stopFuture.complete() }
    }
}
