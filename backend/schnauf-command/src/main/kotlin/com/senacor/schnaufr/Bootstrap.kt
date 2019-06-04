package com.senacor.schnaufr

import com.mongodb.ConnectionString
import com.senacor.schnaufr.schnauf.RSocketServer
import com.senacor.schnaufr.schnauf.SchnaufRepository
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
            val mongoConnectionString = "mongodb://${System.getenv("MONGO_HOST")}:27017"

            logger.info("Starting application, connecting to MongoDB at $mongoConnectionString")
            disposable =
                    RSocketServer(SchnaufRepository(KMongo.createClient(ConnectionString(mongoConnectionString))))
                            .start()
                            .subscribeBy { logger.info("Application started") }

            Thread.currentThread().join()
        }
    }
}
