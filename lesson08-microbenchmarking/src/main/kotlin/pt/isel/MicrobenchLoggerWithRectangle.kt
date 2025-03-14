package pt.isel

import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * BASELINE
 * Produces an output like:
 * Rectangle
 *  - area = 35
 *  - width = 5
 *  - height = 7
 */
fun Rectangle.log(out: Appendable) {
    out.appendLine("Rectangle")
    out.appendLine("- area = $area")
    out.appendLine("- width = $width")
    out.appendLine("- height = $height")


}

fun main() {
    // bench02loggerWithRectangle()
    // bench03loggerWithRectangle(10)
    // bench04loggerWithRectangle(10)
    // bench05loggerWithRectangle(10)

    val rect = Rectangle(5, 7)
    val mem = StringBuilder()
    println("########## Bench log Reflect")
    jBench {
        mem.clear()
        mem.log(rect)
    }
    println(mem)
    println("########## Bench log baseline for Rectangle")
    jBench {
        mem.clear()
        rect.log(mem)
    }
    println(mem)

}

/**
 * Some problems:
 * 1. Misses a baseline (i.e. reference) for comparison
 * 2. Every run presents a different result varying in ~100 ms
 * 3. Mixing DOMAIN (i.e. instantiate Rectangle) with measured OPERATION (i.e. log)
 */
fun bench01loggerWithRectangle(){
    val init = System.currentTimeMillis()
    val rect = Rectangle(5, 7)
    System.out.log(rect)
    val dur = System.currentTimeMillis() - init
    println("Logging rectangle takes $dur ms")
}

/**
 * 4. Overhead of JIT compiler. We should discard first executions
 */
fun bench02loggerWithRectangle(){
    val rect = Rectangle(5, 7)
    val dur = measureTimeMillis {
        System.out.log(rect)
    }
    println("Logging rectangle takes $dur ms")
}

/**
 * 5. IO (e.g. System.out) overhead
 */
fun bench03loggerWithRectangle(runs: Int = 10){
    val rect = Rectangle(5, 7)
    repeat(runs) {
        val nanos = measureNanoTime {
            System.out.log(rect)
        }
        println("Logging rectangle takes ${nanos / 1000} micros")
    }
}

/**
 * 6. System call overhead, i.e. System.nanoTime()
 */
fun bench04loggerWithRectangle(runs: Int = 10){
    val rect = Rectangle(5, 7)
    val mem = StringBuilder()
    repeat(runs) {
        val nanos = measureNanoTime {
            mem.clear()
            mem.log(rect)
        }
        println("Logging rectangle takes ${nanos / 1000} micros")
    }
    println(mem)
}

fun bench05loggerWithRectangle(runs: Int = 10){
    val rect = Rectangle(5, 7)
    val mem = StringBuilder()
    val iterations = 1_000_000
    repeat(runs) {
        val nanos = measureNanoTime {
            repeat(iterations) {
                mem.clear()
                mem.log(rect)
            }
        }
        val perOp = nanos / iterations
        println("Logging rectangle takes $perOp nanos")
    }
    println(mem)
}
