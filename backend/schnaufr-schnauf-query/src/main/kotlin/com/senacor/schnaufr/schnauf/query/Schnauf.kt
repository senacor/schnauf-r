package com.senacor.schnaufr.schnauf.query

import java.util.UUID

// author: User / ID and probably geoloaction or something like that
data class Schnauf(val id: UUID, val author: String, val content: String)