package com.senacor.schnaufr.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RSocketMetadata(val operation: String)
