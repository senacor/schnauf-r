package com.senacor.schnaufr

import com.senacor.schnaufr.gateway.*
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object Bootstrap {

    val logger = LoggerFactory.getLogger(Bootstrap::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val executor = Executors.newSingleThreadExecutor()

        val schnaufCommandClient = SchnaufCommandClient()
        val schnaufUserClient = SchnaufUserClient()
        val schnaufQueryClient = SchnaufQueryClient()

        val server = SchnaufGatewayServer(
                gatewayMessageHandler = SchnaufGatewayMessageHandler(
                        schnaufCommandClient,
                        schnaufUserClient,
                        schnaufQueryClient
                ),
                port = System.getenv("APPLICATION_PORT")?.toInt() ?: 8080
        )

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                schnaufCommandClient.stop()
                schnaufUserClient.stop()
                schnaufQueryClient.stop()
                executor.shutdownNow()
                logger.info("Application stopped")
            }
        })

        executor.execute {
            logger.info("Starting application")

            logger.info("Connecting to Clients")
            schnaufCommandClient.start()
            schnaufUserClient.start()
            schnaufQueryClient.start()

            server.start()
            logger.info("Application started")
            Thread.currentThread().join()
        }
    }
}
