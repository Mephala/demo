package com.example.demo

import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.ParallelFlux
import reactor.core.publisher.SynchronousSink
import java.math.BigInteger


fun main(args: Array<String>) {

//    val limit = BigInteger("100")
//    var start = BigInteger("2")
//    while(start.compareTo(limit) < 1){
//        val eventBus = EventBus()
//
//        eventBus.events.subscribe {
//            when (it) {
//                is PrimeTrueResult -> println("${it.value} is prime")
//            }
//        }
//        Processor(eventBus,start)
//        start = start.add(BigInteger.ONE)
//    }
//
//    println("All started.")


    val eventBus = EventBus()

    eventBus.events.subscribe {
        when (it) {
            is PrimeTrueResult -> println("${it.value} is prime")
        }
    }
    Processor(eventBus, BigInteger("100"))
//    Thread.sleep(15000)
}


sealed class Event
data class PrimeCheck(val value: BigInteger) : Event()
data class PrimeTrueResult(val value: BigInteger) : Event()
data class PrimeFalseResult(val value: BigInteger) : Event()
data class PrimeStart(val value: BigInteger) : Event()

class EventBus {
    private val processor = DirectProcessor.create<Event>()
    private val sink = processor.sink()

    val events = ParallelFlux.from(processor)

    fun publish(event: Event) {
        sink.next(event)
    }
}

class Processor(val eventBus: EventBus, val target: BigInteger) {

    private var primeFalseSignal = false

    init {

        eventBus.events.subscribe {
            when (it) {
                is PrimeCheck -> checkPrimality(it)
                is PrimeFalseResult -> primeFalseSignal = true
                is PrimeStart -> primeStart(it)
            }
        }

        eventBus.publish(PrimeStart(target))

    }

    private fun primeStart(it: PrimeStart) {

        val sqrt = it.value.sqrt()
        var start = BigInteger("2")

        Flux.generate { sink: SynchronousSink<BigInteger> ->
            if ((start.compareTo(sqrt) < 1) && !primeFalseSignal) {
                sink.next(start)
                start = start.add(BigInteger.ONE)
            } else {
                sink.complete()
            }
        }.doOnNext {
            eventBus.publish(PrimeCheck(start))
        }.doOnComplete {
            if (!primeFalseSignal) {
                eventBus.publish(PrimeTrueResult(target))
            }
        }.subscribe()
    }

    private fun hede() {

    }

    private fun checkPrimality(it: PrimeCheck) {
        println(it.value)
        val value = it.value
        if (target.remainder(value) == BigInteger.ZERO) {
            eventBus.publish(PrimeFalseResult(value))
        }
    }
}