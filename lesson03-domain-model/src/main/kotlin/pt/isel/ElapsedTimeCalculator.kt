package pt.isel

object ElapsedTimeCalculator {
    private val startTime: Long= System.currentTimeMillis()

    fun elapsedTime(): Long {
        val currentTime = System.currentTimeMillis()
        return currentTime - startTime
    }
}