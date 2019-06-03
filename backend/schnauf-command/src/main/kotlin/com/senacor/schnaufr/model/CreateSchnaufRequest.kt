package com.senacor.schnaufr.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateSchnaufRequest(val title: String)
