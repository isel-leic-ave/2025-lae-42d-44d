package pt.isel

import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

fun jBench(runs: Int = 10, block: () -> Unit) {
    val iterations = 1_000_000
    repeat(runs) {
        val dur = measureNanoTime {
            repeat(iterations) {
                block()
            }
        }
        val perOp = dur / iterations
        println("Operation takes $perOp nanos")
    }
}

