package pt.isel

import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis


/**
 * Baseline for comparison with Logger via Reflect
 *
 * Rectangle
 *  - area = 35
 *  - height = 7
 *  - width = 5
 */
fun Rectangle.log(out: Appendable) {
    out.appendLine("Rectangle")
    out.appendLine("- area = $area")
    out.appendLine("- height = $height")
    out.appendLine("- width = $width")
}

fun main() {
    // bench01LoggerWithRectangle()
    // bench02LoggerWithRectangle()
    // bench03LoggerWithRectangle()
    // bench04LoggerWithRectangle()
    // bench05LoggerWithRectangle()
    val rect = Rectangle(5,7)
    val mem = StringBuilder()
    println("############### Bench Log via Reflect")
    jBench {
        mem.clear()
        mem.log(rect)
    }
    println(mem)
    println("############### Bench Log baseline")
    jBench {
        mem.clear()
        rect.log(mem)
    }
    println(mem)
}

/*
 * 1. Mixing OPERATION to measure withe DOMAIN instantiation
 */
fun bench01LoggerWithRectangle() {
    val start = System.currentTimeMillis()
    System.out.log(Rectangle(5,7))
    val dur = System.currentTimeMillis() - start
    println("Logging a Rectangle takes ${dur} ms")
}

/**
 * 2. IO overhead (i.e. System.out) => Should avoid it
 */
fun bench02LoggerWithRectangle() {
    val rect = Rectangle(5,7)
    val start = System.currentTimeMillis()
    System.out.log(rect)
    val dur = System.currentTimeMillis() - start
    println("Logging a Rectangle takes ${dur} ms")
}

/**
 * 3. JIT Compiler overhead => Discard first executions
 */
fun bench03LoggerWithRectangle() {
    val rect = Rectangle(5,7)
    val mem = StringBuilder()
    val dur = measureTimeMillis {
        mem.clear()
        mem.log(rect)
    }
    println("Logging a Rectangle takes ${dur} ms")
    println(mem)
}

/**
 * 4. Overhead of the System.nanno()
 */
fun bench04LoggerWithRectangle(runs: Int = 10) {
    val rect = Rectangle(5,7)
    val mem = StringBuilder()
    repeat(runs) {
        val dur = measureNanoTime {
            mem.clear()
            mem.log(rect)
        }
        println("Logging a Rectangle takes ${dur/1000} micros")
    }
    println(mem)
}

fun bench05LoggerWithRectangle(runs: Int = 10) {
    val rect = Rectangle(5,7)
    val mem = StringBuilder()
    val iterations = 1_000_000
    repeat(runs) {
        val dur = measureNanoTime {
            repeat(iterations) {
                mem.clear()
                mem.log(rect)
            }
        }
        val perOp = dur / iterations
        println("Logging a Rectangle takes $perOp nanos")
    }
    println(mem)
}
