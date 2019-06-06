package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.*
import com.senacor.schnaufr.model.CreateSchnaufRequest
import io.rsocket.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.*

class MessageHandler(private val schnaufRepository: SchnaufRepository) : AbstractRSocket() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)

        const val CREATE_SCHNAUF = "createSchnauf"
        const val GET_ALL_SCHNAUFS = "getAllSchnaufs"
        const val WATCH_SCHNAUFS = "watchSchnaufs"
        const val GET_ALL_SCHNAUFS_AND_WATCH = "getAllSchnaufsAndWatch"
    }

    override fun requestResponse(payload: Payload): Mono<Payload> {
        return when (payload.operation) {
            CREATE_SCHNAUF -> {
                val schnaufRequest = CreateSchnaufRequest.fromJson(payload.dataUtf8)
                logger.info("Inserting $schnaufRequest into DB")
                schnaufRepository
                        .create(Schnauf.fromRequest(schnaufRequest))
                        .map { it.asPayload() }
            }

            else -> return Mono.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }

    override fun requestStream(payload: Payload): Flux<Payload> {
        val principal = payload.principal

        return when (payload.operation) {
            GET_ALL_SCHNAUFS ->
                schnaufRepository
                        .readLatest(principal = principal)
                        .map { it.asPayload() }


            WATCH_SCHNAUFS ->
                schnaufRepository
                        .watch(principal)
                        .map { it.asPayload() }


            GET_ALL_SCHNAUFS_AND_WATCH ->
                schnaufRepository
                        .readLatest(principal = principal)
                        .concatWith(
                                schnaufRepository
                                        .watch(principal)
                        )
                        .map { it.asPayload() }

            else -> return Flux.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}
