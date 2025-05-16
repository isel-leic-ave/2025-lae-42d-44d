package pt.isel

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class TestQueries {
    @Test
    fun testSuspZip_firstSequenceLonger() {
        val sequence1 = sequenceOf(1, 2, 3, 4)
        val sequence2 = sequenceOf("a", "b")

        val result = sequence1.suspZip(sequence2) { a, b -> "$a:$b" }.toList()

        // Zipping stops at the shortest sequence
        assertEquals(listOf("1:a", "2:b"), result)
    }

    @Test
    fun testSuspZip_secondSequenceLonger() {
        val sequence1 = sequenceOf(10, 20)
        val sequence2 = sequenceOf("x", "y", "z")

        val result = sequence1.suspZip(sequence2) { a, b -> "$a-$b" }.toList()

        // Zipping stops at the shortest sequence
        assertEquals(listOf("10-x", "20-y"), result)
    }
}
