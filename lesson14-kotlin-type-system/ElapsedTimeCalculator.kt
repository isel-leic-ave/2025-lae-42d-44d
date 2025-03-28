object ElapsedTimeCalculator {
    private val startTime: Long= System.currentTimeMillis()

    fun elapsedTime(): Long {
        val currentTime = System.currentTimeMillis()
        return currentTime - startTime
    }
}

fun main(args: Array<String>) {
    println(ElapsedTimeCalculator.elapsedTime()) // <=> ElapsedTimeCalculator.INSTANCE.elapsedTime()
    Thread.sleep(2500)
    println(ElapsedTimeCalculator.elapsedTime())
    
}