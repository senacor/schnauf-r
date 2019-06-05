package com.senacor.schnaufr

import com.mongodb.ConnectionString
import com.senacor.schnaufr.user.*
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {

    val logger = LoggerFactory.getLogger(Bootstrap::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        val server = SchnauferServer(
            MessageHandler(
                SchnauferRepository(
                    KMongo.createClient(ConnectionString("mongodb://localhost:27017"))
                )
            )
        )

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                server.stop()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })

        executor.execute {
            logger.info("Starting application")
            server.start()
            logger.info("Application started")
            Thread.currentThread().join()
        }
    }
}
