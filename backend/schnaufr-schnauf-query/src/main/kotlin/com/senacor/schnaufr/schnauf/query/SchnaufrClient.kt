package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.schnauf.query.model.MetaData
import com.senacor.schnaufr.schnauf.query.model.Schnaufr
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class SchnaufrClient() {
    private lateinit var disposable: Disposable;
    private lateinit var rsocket: Single<RSocket>;

    private val schnaufrClientHost = System.getenv("SCHNAUFR_HOST") ?: "localhost"
    private val schnaufrClientPort = System.getenv("SCHNAUFR_PORT")?.toInt() ?: 8082
    private val connectionString = "$schnaufrClientHost:$schnaufrClientPort"

    companion object {
        const val FIND_USER_COMMAND = "findUser"
        val logger: Logger = LoggerFactory.getLogger(RSocketSchnaufQueryServer::class.java)
    }

    fun start() {
        rsocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(schnaufrClientHost, schnaufrClientPort))
                .start()

        disposable = rsocket.subscribe();
    }

    fun getSchnaufrById(id: UUID): Single<Schnaufr> {
        // TODO: what happens if no schnaufr found?
        return rsocket.flatMap {
            it.requestResponse(DefaultPayload.text(id.toString(), MetaData(FIND_USER_COMMAND).toJson()))
                    .map { Schnaufr.fromJson(it.dataUtf8) }
        };
    }

    fun stop() {
        disposable.dispose();
        logger.info("Disconnected from $connectionString")
    }
}