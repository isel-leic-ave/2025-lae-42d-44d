package pt.isel

import kotlin.system.measureNanoTime


fun jBench(runs: Int = 10, block: () -> Unit){
    val iterations = 1_000_000
    repeat(runs) {
        val nanos = measureNanoTime {
            repeat(iterations) {
                block()
            }
        }
        val perOp = nanos / iterations
        println("Logging rectangle takes $perOp nanos")
    }
}
