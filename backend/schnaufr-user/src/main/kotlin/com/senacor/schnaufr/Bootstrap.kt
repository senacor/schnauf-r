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

        val mongoHost = System.getenv("MONGO_HOST") ?: "mongo"
        val mongoPort = System.getenv("MONGO_PORT")?.toInt() ?: 27017
        val mongoConnectionString = "mongodb://$mongoHost:$mongoPort"

        val schnauferRepository = SchnauferRepository(
                KMongo.createClient(ConnectionString(mongoConnectionString))
        )

        val server = SchnauferServer(
            MessageHandler(schnauferRepository)
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
            logger.info("Creating initial users")
            DefaultSchnaufer.allSchnaufer.forEach {
                schnauferRepository.create(it).block()
                schnauferRepository.saveAvatar(avatarId = it.avatarId, data = Bootstrap::class.java.getResourceAsStream("/avatars/${it.username}.jpg")).block()
            }
            logger.info("Created initial users")
            Thread.currentThread().join()
        }
    }
}
