package com.senacor.schnaufr.schnauf.query

import com.squareup.moshi.JsonClass
import java.util.*


// author: User / ID and probably geoloaction or something like that
@JsonClass(generateAdapter = true)
data class Schnauf(val id: UUID, val author: String, val content: String)