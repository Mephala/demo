package com.example.demo

import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import reactor.core.scheduler.Schedulers
import java.math.BigInteger

fun main(args: Array<String>) {
    val upperBound = BigInteger("130000")
    var maybePrime = BigInteger("2")

    Flux.generate { sink: SynchronousSink<BigInteger> ->
        synchronized(upperBound) {
            if (maybePrime <= upperBound) {
                sink.next(maybePrime)
                maybePrime = maybePrime.add(BigInteger.ONE)
            } else {
                sink.complete()
            }
        }
    }.parallel().runOn(Schedulers.parallel())
            .doOnNext {
                checkPrimality(it)
            }.subscribe()

    Thread.sleep(10000)
}

private fun checkPrimality(maybePrime: BigInteger) {

    val sqrt = maybePrime.sqrt()
    var notPrime = false
    var start = BigInteger("2")



    Flux.generate { sink: SynchronousSink<BigInteger> ->
        if (!notPrime && start <= sqrt) {
            sink.next(start)
            start = start.add(BigInteger.ONE)
        } else {
            sink.complete()
        }
    }.parallel().runOn(Schedulers.parallel()).doOnNext {
        if (!notPrime && maybePrime.remainder(it) == BigInteger.ZERO) {
            notPrime = true
        }
    }.sequential().doOnComplete {
        if (!notPrime ) {
            println("$maybePrime is prime")
        }

    }.subscribe()
}