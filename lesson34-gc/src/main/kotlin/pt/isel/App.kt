package pt.isel

fun main() {
    checkGc()
    // checkGcWithoutRootReference()
    println("GC after the end of checkGc()")
    // Putting null on arr removes the root reference
    // to the large object array.
    // arr = null
    System.gc()
    printAllocatedMem()
}

var arr: Any? = null

fun checkGc() {
    println("Allocated mem after initialization!!")
    printAllocatedMem()
    /**
     * Be careful with this in real code!!!!
     */
    System.gc()
    println("After gc:")
    printAllocatedMem()
    /*
     * arr is a Root Reference
     */
    arr = makeGarbage()
    println("Mem after create some garbage")
    printAllocatedMem()
    println("After gc:")
    System.gc()
    printAllocatedMem()
}

fun checkGcWithoutRootReference() {
    println("Allocated mem after initialization!!")
    printAllocatedMem()
    /**
     * Be careful with this in real code!!!!
     */
    System.gc()
    println("After gc:")
    printAllocatedMem()
    // create 1_000_000 of objects stored in an Array
    // that has no reference to it.
    makeGarbage()
    println("Mem after create some garbage")
    printAllocatedMem()
    println("After gc:")
    System.gc()
    printAllocatedMem()
}

fun printAllocatedMem() {
    val runtime = Runtime.getRuntime()
    val allocatedMem = runtime.totalMemory() - runtime.freeMemory()
    println("MEM = ${allocatedMem / 1024} Kb")
}

const val size = 1_000_000

fun makeGarbage(): Array<Any?> {
    val arr = arrayOfNulls<Any>(size)
    for (i in 0 until size) {
        arr[i] = Any()
    }
    return arr
}
