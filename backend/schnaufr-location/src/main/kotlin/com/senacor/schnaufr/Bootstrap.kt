package com.senacor.schnaufr

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.senacor.schnaufr.location.GeolocationRepository
import com.senacor.schnaufr.location.GeolocationServer
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Bootstrap {

    private val logger = LoggerFactory.getLogger(Bootstrap::class.java)

    private lateinit var geolocationServer: GeolocationServer

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()
        setupApplicationShutdown(executor)
        startApplication(executor)
    }

    private fun startApplication(executor: ExecutorService) {
        executor.execute {
            logger.info("Starting application")
            val databaseConnection = createDatabaseConnection()
            val geolocationRepository = GeolocationRepository(databaseConnection)
            geolocationServer = GeolocationServer(geolocationRepository)
            geolocationServer.start()

            Thread.currentThread().join()
        }
    }

    private fun setupApplicationShutdown(executor: ExecutorService) {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                geolocationServer.stop()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })
    }

    private fun createDatabaseConnection(): MongoClient {
        val mongoHost = System.getenv("MONGO_HOST") ?: "mongo"
        val mongoConnectionString = "mongodb://$mongoHost:27017"
        val mongoClient = KMongo.createClient(ConnectionString(mongoConnectionString))
        logger.info("Connected to database on $mongoConnectionString")
        return mongoClient
    }
}
