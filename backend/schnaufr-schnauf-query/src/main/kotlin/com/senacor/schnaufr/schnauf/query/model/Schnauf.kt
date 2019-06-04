package com.senacor.schnaufr.schnauf.query.model

import com.squareup.moshi.JsonClass
import java.util.*


// author: User / ID and probably geoloaction or something like that
@JsonClass(generateAdapter = true)
data class Schnauf(val id: UUID, val submitter: String, val title: String)