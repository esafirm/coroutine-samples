package com.esafirm.coroutines.sample

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import kotlin.concurrent.thread

fun main(args: Array<String>) {

    /* Print with Java Thread */
    printInThread()

    /* Print with RxJava */
    printInCompletable()

    /* Print with Coroutines */
    printInCoroutines()
}

fun printInCoroutines() = runBlocking {
    firstJob() // you can also use `suspend` keyword
    async(CommonPool) { println("Coroutines Job 2") }
    async(CommonPool) { println("Coroutines Job 3") }
}.also {
    println("Job Complete")
}

suspend fun firstJob(){
    println("Coroutines Job 1")
}


fun printInCompletable() {
    Completable.fromAction { println("Rx Job 1") }
            .andThen(Completable.fromAction { println("Rx Job 2") })
            .andThen(Completable.fromAction { println("Rx Job 3") })
            .doOnComplete { println("Job Complete") }
            .subscribeOn(Schedulers.computation())
            .subscribe()
}

fun printInThread() {
    firstJob {
        otherJob {
            otherOtherJob {
                println("Job Complete")
            }
        }
    }
}

typealias OnJobComplete = () -> Unit
fun firstJob(onComplete: OnJobComplete) {
    thread(start = true) {
        println("Thread Job 1")
        onComplete()
    }
}

fun otherJob(onComplete: OnJobComplete) {
    thread(start = true) {
        println("Thread Job 2")
        onComplete()
    }
}

fun otherOtherJob(onComplete: OnJobComplete) {
    thread(start = true) {
        println("Thread Job 3")
        onComplete()
    }
}

