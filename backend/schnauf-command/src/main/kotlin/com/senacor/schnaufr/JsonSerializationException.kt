package com.senacor.schnaufr

class JsonSerializationException : RuntimeException {
    constructor(exception: Exception) : super(exception)
    constructor(message: String) : super(message)
}
