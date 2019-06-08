package com.senacor.schnaufr.user

import java.nio.ByteBuffer

fun ByteBuffer.convertToByteArray(chunkSize: Int): ByteArray {// Create a byte array
    // Retrieve bytes between the position and limit
    // (see Putting Bytes into a ByteBuffer)
    val bytes = ByteArray(remaining())

    // transfer bytes from this buffer into the given destination array
    get(bytes, 0, bytes.size)
    return bytes
}