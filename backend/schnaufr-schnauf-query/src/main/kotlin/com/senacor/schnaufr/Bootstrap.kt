package com.senacor.schnaufr

import com.senacor.schnaufr.schnauf.query.RSocketSchnaufQueryServer
import com.senacor.schnaufr.schnauf.query.SchnaufClient
import com.senacor.schnaufr.schnauf.query.SchnaufMessageHandler
import com.senacor.schnaufr.schnauf.query.SchnaufrClient
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {

    val logger = LoggerFactory.getLogger(Bootstrap::class.java)
    var disposable: Disposable? = null
    var schnaufClient = SchnaufClient()
    var schnaufrClient = SchnaufrClient()

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                disposable?.dispose()
                schnaufClient.stop()
                // schnaufrClient.stop()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })

        executor.execute {
            logger.info("Starting application")

            logger.info("Connecting to Clients")
            schnaufClient.start()
            // schnaufrClient.start()

            val messageHandler = SchnaufMessageHandler(
                    schnaufClient,
                    schnaufrClient
            )
            val rSocketSchnaufQueryServer = RSocketSchnaufQueryServer(messageHandler)
            disposable =
                    rSocketSchnaufQueryServer
                            .setup()
                            .subscribeBy { logger.info("Application started") }

            Thread.currentThread().join()
        }
    }
}
