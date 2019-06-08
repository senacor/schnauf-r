package com.senacor.schnaufr.query

import com.senacor.schnaufr.query.model.MetaData
import com.senacor.schnaufr.query.model.Schnauf
import io.rsocket.*
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.Disposable
import reactor.core.publisher.*

class SchnaufClient {
    companion object {
        const val GET_ALL_SCHNAUFS_COMMAND = "getAllSchnaufs"
        const val WATCH_SCHNAUFS_COMMAND = "watchSchnaufs"
        const val GET_ALL_SCHNAUFS_AND_WATCH = "getAllSchnaufsAndWatch"
        val logger: Logger = LoggerFactory.getLogger(SchnaufQueryServer::class.java)
    }

    private val schnaufCommandHost = System.getenv("SCHNAUF_COMMAND_HOST") ?: "localhost"
    private val schnaufCommandPort = System.getenv("SCHNAUF_COMMAND_PORT")?.toInt() ?: 8081
    private val connectionString = "$schnaufCommandHost:$schnaufCommandPort"

    fun getAllSchnaufs(): Flux<Schnauf> {
        return rsocket.flatMapMany { rsocket ->
            rsocket.requestStream(DefaultPayload.create("", MetaData(GET_ALL_SCHNAUFS_COMMAND).toJson()))
                    .map { Schnauf.fromJson(it.dataUtf8) }
        }
    }

    fun watchAllSchnaufs(): Flux<Schnauf> {
        return rsocket.flatMapMany { rsocket ->
            rsocket.requestStream(DefaultPayload.create("", MetaData(WATCH_SCHNAUFS_COMMAND).toJson()))
                    .map { Schnauf.fromJson(it.dataUtf8) }
        }
    }

    fun getAllSchnaufsAndWatch(): Flux<Schnauf> {
        return rsocket.flatMapMany { rsocket ->
            rsocket.requestStream(DefaultPayload.create("", MetaData(GET_ALL_SCHNAUFS_AND_WATCH).toJson()))
                    .map { Schnauf.fromJson(it.dataUtf8) }
        }
    }

    private lateinit var disposable: Disposable
    private lateinit var rsocket: Mono<RSocket>

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufCommandHost, schnaufCommandPort))
                .start()

        disposable = rsocket.subscribe()
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}