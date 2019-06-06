package com.senacor.schnaufr

import com.mongodb.ConnectionString
import com.senacor.schnaufr.schnauf.MessageHandler
import com.senacor.schnaufr.schnauf.SchnaufRepository
import com.senacor.schnaufr.schnauf.SchnaufServer
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {
    val logger: Logger = LoggerFactory.getLogger(Bootstrap::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        val mongoHost = System.getenv("MONGO_HOST") ?: "mongo"
        val mongoPort = System.getenv("MONGO_PORT")?.toInt() ?: 27017
        val mongoConnectionString = "mongodb://$mongoHost:$mongoPort"

        val server = SchnaufServer(
            MessageHandler(
                SchnaufRepository(
                    KMongo.createClient(ConnectionString(mongoConnectionString))
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
            logger.info("Starting application, connecting to MongoDB at $mongoConnectionString")
            server.start()
            logger.info("Application started")
            Thread.currentThread().join()
        }
    }
}
