package com.senacor.schnaufr

import com.mongodb.ConnectionString
import com.senacor.schnaufr.user.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {

    val logger = LoggerFactory.getLogger(Bootstrap::class.java)
    var disposable: Disposable? = null

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                disposable?.dispose()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })

        executor.execute {
            logger.info("Starting application")
            disposable =
                SchnauferServer(
                    MessageHandler(
                        SchnauferRepository(
                            KMongo.createClient(ConnectionString("mongodb://localhost:27017"))
                        )
                    )
                )
                    .setup()
                    .subscribeBy { logger.info("Application started") }

            Thread.currentThread().join()
        }
    }
}
