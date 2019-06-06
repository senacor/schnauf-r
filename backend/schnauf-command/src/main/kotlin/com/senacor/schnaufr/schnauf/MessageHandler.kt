package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.model.CreateSchnaufRequest
import com.senacor.schnaufr.model.operation
import com.senacor.schnaufr.model.principal
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val CREATE_SCHNAUF = "createSchnauf"
const val GET_ALL_SCHNAUFS = "getAllSchnaufs"
const val WATCH_SCHNAUFS = "watchSchnaufs"
const val GET_ALL_SCHNAUFS_AND_WATCH = "getAllSchnaufsAndWatch"

class MessageHandler(private val schnaufRepository: SchnaufRepository) : AbstractRSocket() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun requestResponse(payload: Payload): Single<Payload> {
        return when (payload.operation) {
            CREATE_SCHNAUF -> {
                val schnaufRequest = CreateSchnaufRequest.fromJson(payload.dataUtf8)
                logger.info("Inserting $schnaufRequest into DB")
                schnaufRepository
                        .create(Schnauf.fromRequest(schnaufRequest))
                        .map { it.asPayload() }
            }

            else -> return Single.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }

    override fun requestStream(payload: Payload): Flowable<Payload> {
        val principal = payload.principal

        return when (payload.operation) {
            GET_ALL_SCHNAUFS ->
                schnaufRepository
                        .readLatest(principal = principal)
                        .map { it.asPayload() }


            WATCH_SCHNAUFS ->
                schnaufRepository
                        .watch(principal)
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .map { it.asPayload() }


            GET_ALL_SCHNAUFS_AND_WATCH ->
                schnaufRepository
                        .readLatest(principal = principal)
                        .concatWith(
                                schnaufRepository
                                        .watch(principal)
                                        .toFlowable(BackpressureStrategy.BUFFER)
                        )
                        .map { it.asPayload() }

            else -> return Flowable.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}
