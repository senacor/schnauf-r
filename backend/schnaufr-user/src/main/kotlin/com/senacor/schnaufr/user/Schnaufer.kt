package com.senacor.schnaufr.user

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class Schnaufer(val id: UUID, val avatarId: UUID, val username: String, val displayName: String)