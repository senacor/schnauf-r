package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.schnauf.query.model.Schnaufr
import io.reactivex.Single
import java.util.*

class SchnaufrClient() {
    fun getSchnaufrById(id: UUID): Single<Schnaufr> {
        return Single.never()
    }
}