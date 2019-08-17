package com.example.demo

import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import kotlin.math.sin

fun main(args: Array<String>) {
    var start = 1

    Flux.generate{sink:SynchronousSink<Int> ->
        if(start < 10){
            sink.next(start)
            start++
        }else{
            sink.complete()
        }
    }.parallel()
            .doOnNext{
                println(it)
            }.subscribe()
}