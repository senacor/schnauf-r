package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.model.CreateSchnaufRequest
import com.senacor.schnaufr.model.operation
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.util.AbstractRSocket

const val CREATE_SCHNAUF = "createSchnauf"
const val GET_ALL_SCHNAUFS = "getAllSchnaufs"
const val WATCH_SCHNAUFS = "watchSchnaufs"
const val GET_ALL_SCHNAUFS_AND_WATCH = "getAllSchnaufsAndWatch"

class MessageHandler(private val schnaufRepository: SchnaufRepository) : AbstractRSocket() {
    override fun requestResponse(payload: Payload): Single<Payload> {
        return when (payload.operation) {
           CREATE_SCHNAUF -> {
                val schnaufRequest = CreateSchnaufRequest.fromJson(payload.dataUtf8)
                schnaufRepository
                        .create(Schnauf.fromRequest(schnaufRequest))
                        .map { it.asPayload() }
            }

            else -> return Single.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }

    override fun requestStream(payload: Payload): Flowable<Payload> {
        return when (payload.operation) {
            GET_ALL_SCHNAUFS ->
                schnaufRepository
                        .readLatest()
                        .map { it.asPayload() }


            WATCH_SCHNAUFS ->
                schnaufRepository
                        .watch()
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .map { it.asPayload() }


            GET_ALL_SCHNAUFS_AND_WATCH ->
                schnaufRepository
                        .readLatest()
                        .concatWith(
                                schnaufRepository
                                        .watch()
                                        .toFlowable(BackpressureStrategy.BUFFER)
                        )
                        .map { it.asPayload() }

            else -> return Flowable.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}
