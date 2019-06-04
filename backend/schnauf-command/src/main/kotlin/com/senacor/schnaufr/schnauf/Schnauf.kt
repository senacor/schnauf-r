package com.senacor.schnaufr.schnauf

import com.senacor.schnaufr.UUID
import com.senacor.schnaufr.model.CreateSchnaufRequest
import java.util.*

data class Schnauf(val id: UUID, val title: String, val submitter: String) {

    companion object {
        fun fromRequest(request: CreateSchnaufRequest): Schnauf =  Schnauf(UUID(), request.title, request.submitter)
    }
}
