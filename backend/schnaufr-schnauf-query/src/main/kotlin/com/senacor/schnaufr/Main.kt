package com.senacor.schnaufr

import com.senacor.schnaufr.schnauf.query.RSocketSchnaufQueryServer

fun main(args: Array<String>) {
    System.out.println(("Hello schnauf"))
    RSocketSchnaufQueryServer().start()
}