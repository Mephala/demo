package com.example.demo

import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.ParallelFlux
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
    Processor(eventBus,BigInteger("100"))
    Thread.sleep(15000)
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

    private fun primeStart(it: PrimeStart){
        val sqrt = it.value.sqrt()
        var start = BigInteger("2")
        while (start.compareTo(sqrt) < 1) {
            eventBus.publish(PrimeCheck(start))
            start = start.add(BigInteger.ONE)
            if(primeFalseSignal){
                break
            }
        }

        eventBus.events.doAfterTerminate{
            println("Hele hele")
        }

//        if(!primeFalseSignal){
//            eventBus.publish(PrimeTrueResult(target))
//        }
    }

    private fun checkPrimality(it: PrimeCheck) {
        println(it.value)
        val value = it.value
        if (target.remainder(value) == BigInteger.ZERO) {
            eventBus.publish(PrimeFalseResult(value))
        }
    }
}