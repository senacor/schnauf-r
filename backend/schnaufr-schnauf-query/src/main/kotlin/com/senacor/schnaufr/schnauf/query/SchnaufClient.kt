package com.senacor.schnaufr.schnauf.query

import com.senacor.schnaufr.schnauf.query.model.Schnauf
import io.reactivex.Flowable

class SchnaufClient() {


    fun getAllSchnaufs(): Flowable<Schnauf> {
        return Flowable.empty();
    }
}