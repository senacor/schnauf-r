package com.senacor.schnaufr

import com.senacor.schnaufr.gateway.SchnaufClient
import com.senacor.schnaufr.gateway.SchnaufMessageHandler
import com.senacor.schnaufr.gateway.SchnaufQueryServer
import com.senacor.schnaufr.gateway.SchnauferClient
import com.senacor.schnaufr.schnauf.query.*
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {

    val logger = LoggerFactory.getLogger(Bootstrap::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        val schnaufClient = SchnaufClient()
        val schnauferClient = SchnauferClient()

        val server = SchnaufQueryServer(
                messageHandler = SchnaufMessageHandler(
                        schnaufClient,
                        schnauferClient
                ),
                port = System.getenv("APPLICATION_PORT")?.toInt() ?: 8080
        )

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                schnaufClient.stop()
                schnauferClient.stop()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })

        executor.execute {
            logger.info("Starting application")

            logger.info("Connecting to Clients")
            schnaufClient.start()
            schnauferClient.start()

            server.start()
            logger.info("Application started")
            Thread.currentThread().join()
        }
    }
}
