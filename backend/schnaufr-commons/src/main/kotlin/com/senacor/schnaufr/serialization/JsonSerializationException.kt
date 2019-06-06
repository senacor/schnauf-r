package com.senacor.schnaufr.serialization

class JsonSerializationException : RuntimeException {
    constructor(exception: Exception) : super(exception)
    constructor(message: String) : super(message)
}

