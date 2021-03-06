package com.senacor.schnaufr.query

import com.senacor.schnaufr.query.model.MetaData
import com.senacor.schnaufr.query.model.SchnauferByIdRequest
import com.senacor.schnaufr.query.model.Schnaufr
import io.rsocket.*
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.Disposable
import reactor.core.publisher.Mono
import java.util.*

class SchnauferClient {
    private lateinit var disposable: Disposable
    private lateinit var rsocket: Mono<RSocket>

    private val schnaufrClientHost = System.getenv("SCHNAUFR_HOST") ?: "localhost"
    private val schnaufrClientPort = System.getenv("SCHNAUFR_PORT")?.toInt() ?: 8082
    private val connectionString = "$schnaufrClientHost:$schnaufrClientPort"

    companion object {
        const val FIND_USER_COMMAND = "findUserById"
        val logger: Logger = LoggerFactory.getLogger(SchnaufQueryServer::class.java)
    }

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufrClientHost, schnaufrClientPort))
                .start()

        disposable = rsocket.subscribe()
    }

    fun getSchnaufrById(id: UUID): Mono<Schnaufr> {
        // TODO: what happens if no schnaufr found?
        return rsocket.flatMap { rsocket ->
            rsocket.requestResponse(DefaultPayload.create(SchnauferByIdRequest(id).toJson(), MetaData(FIND_USER_COMMAND, -1, UUID.randomUUID()).toJson()))
                    .map { Schnaufr.fromJson(it.dataUtf8) }
                    .onErrorReturn(Schnaufr.defaultSchnaufr)
        };
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}