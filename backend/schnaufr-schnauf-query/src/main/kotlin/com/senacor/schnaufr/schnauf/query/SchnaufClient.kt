package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.schnauf.query.model.MetaData
import com.senacor.schnaufr.schnauf.query.model.Schnauf
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SchnaufClient() {
    companion object {
        const val GET_ALL_SCHNAUFS_COMMAND = "getAllSchnaufs"
        val logger: Logger = LoggerFactory.getLogger(RSocketSchnaufQueryServer::class.java)
    }

    private val schnaufCommandHost = System.getenv("SCHNAUF_COMMAND_HOST") ?: "localhost"
    private val schnaufCommandPort = System.getenv("SCHNAUF_COMMAND_PORT")?.toInt() ?: 8081
    private val connectionString = "$schnaufCommandHost:$schnaufCommandPort"

    fun getAllSchnaufs(): Flowable<Schnauf> {
        return rsocket.flatMapPublisher {
            it.requestStream(DefaultPayload.text("", MetaData(GET_ALL_SCHNAUFS_COMMAND).toJson()))
                    .map { Schnauf.fromJson(it.dataUtf8) }
        }
    }

    private lateinit var disposable: Disposable;
    private lateinit var rsocket: Single<RSocket>;

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufCommandHost, schnaufCommandPort))
                .start()

        disposable = rsocket.subscribe();
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}