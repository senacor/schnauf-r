package com.senacor.schnaufr

import com.senacor.schnaufr.schnauf.query.RSocketSchnaufQueryServer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {

    val logger = LoggerFactory.getLogger(Bootstrap::class.java)
    var disposable: Disposable? = null
    val rSocketSchnaufQueryServer = RSocketSchnaufQueryServer()

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                disposable?.dispose()
                rSocketSchnaufQueryServer.stop()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })

        executor.execute {
            logger.info("Starting application")
            disposable =
                rSocketSchnaufQueryServer
                .start()
                .subscribeBy { logger.info("Application started") }

            Thread.currentThread().join()
        }
    }
}
