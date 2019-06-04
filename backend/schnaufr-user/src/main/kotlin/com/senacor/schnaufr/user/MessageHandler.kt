package com.senacor.schnaufr.user

import com.senacor.schnaufr.operation
import io.reactivex.*
import io.rsocket.kotlin.*
import io.rsocket.kotlin.util.AbstractRSocket
import org.reactivestreams.Publisher
import org.slf4j.*
import java.lang.RuntimeException
import java.lang.UnsupportedOperationException
import java.util.UUID
import javax.print.attribute.standard.JobOriginatingUserName

class MessageHandler(private val repository: SchnauferRepository) : AbstractRSocket() {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MessageHandler::class.java)
    }

    override fun requestResponse(payload: Payload): Single<Payload> {
        logger.info("received payload '{}' with metadata '{}'", payload.dataUtf8, payload.metadataUtf8)

        return when(payload.operation) {
            "findUserByUsername" -> {
                Single.defer {
                    repository.readByUsername(payload.dataUtf8)
                        .map { it.asPayload() }
                        .switchIfEmpty(Single.error(RuntimeException("userNotFound")))
                }
            }
            "findUserById" -> {
                Single.defer {
                    repository.read(id = UUID.fromString(payload.dataUtf8))
                        .map { it.asPayload() }
                        .switchIfEmpty(Single.error(RuntimeException("userNotFound")))
                }
            }
            else -> return Single.error(UnsupportedOperationException("unrecognized operation ${payload.operation}"))
        }
    }
}