package com.senacor.schnaufr

import com.senacor.schnaufr.user.model.Schnaufer

object DefaultSchnaufer {
    private val blumeSchnaufer = Schnaufer(
            java.util.UUID.fromString("1ba9b76b-464b-45e9-9ef3-2263b150d713"),
            java.util.UUID.fromString("df3b27d6-580e-4c96-bfbe-fa83c31e09df"),
            "blumenmartin",
            "Martin Blume"
    )
    private val ersfeldSchnaufer = Schnaufer(
            java.util.UUID.fromString("1762302b-2e20-4159-9d93-f9d43568d056"),
            java.util.UUID.fromString("babe7929-9088-4618-9d34-19a1b6500def"),
            "ersterFeld",
            "Christoph Ersfeld"
    )
    private val nebSchnaufer = Schnaufer(
            java.util.UUID.fromString("ce6fc2c1-b7e0-490f-a71e-17c09542c100"),
            java.util.UUID.fromString("756c79d4-5435-4e4c-b6fb-6e5672fa06c2"),
            "neb",
            "Sebastian Neb"
    )
    private val hildeSchnaufer = Schnaufer(
            java.util.UUID.fromString("04cd41b5-7864-4641-92cd-bc010782d6ca"),
            java.util.UUID.fromString("b9341e89-7847-422a-a5f9-8278d5f06090"),
            "hildenblam",
            "Hilde Blam"
    )
    private val holtkampSchnaufer = Schnaufer(
            java.util.UUID.fromString("c5d15563-5ac7-477d-95d6-783f7b0a729d"),
            java.util.UUID.fromString("87c29cfd-0faf-4241-8201-78f31af5ac59"),
            "hatekamp",
            "Jonas Holtkamp"
    )
    private val ohmanSchnaufer = Schnaufer(
            java.util.UUID.fromString("13898f8e-c5c1-4ed4-bb4d-e3c0725bea2a"),
            java.util.UUID.fromString("69ecdf99-a788-4e47-b24c-a590fc2f8a38"),
            "ohmannnnnn",
            "Michael Omann"
    )
    private val navaroSchnaufer = Schnaufer(
            java.util.UUID.fromString("c70c98fe-479d-4646-9ef2-e137b28b867c"),
            java.util.UUID.fromString("2863314f-f3c7-4bfc-9c9f-f13be58741eb"),
            "spainNavaro",
            "Antonio Navaro"
    )
    private val pewSchnaufer = Schnaufer(
            java.util.UUID.fromString("40f0fb39-4574-421a-8f08-0c2179614fb6"),
            java.util.UUID.fromString("b5660f78-c8cb-4b02-9954-c1e180a64020"),
            "pewpewpew",
            "Mathias Pewters"
    )

    val allSchnaufer = arrayOf(
            blumeSchnaufer,
            ersfeldSchnaufer,
            nebSchnaufer,
            hildeSchnaufer,
            holtkampSchnaufer,
            ohmanSchnaufer,
            navaroSchnaufer,
            pewSchnaufer
    )
}