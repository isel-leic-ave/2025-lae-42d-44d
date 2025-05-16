package pt.isel

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class TestQueries {
    @Test
    fun testLazyConcat_two_sequences() {
        val sequence1 = sequenceOf("a", "b")
        val sequence2 = sequenceOf("z", "x", "y")
        val res = sequence1.lazyConcat(sequence2)

        assertEquals(listOf("a", "b", "z", "x", "y"), res.toList())
    }

    @Test
    fun testSuspConcat_two_sequences() {
        val sequence1 = sequenceOf("a", "b")
        val sequence2 = sequenceOf("z", "x", "y")
        val res = sequence1.suspConcat(sequence2)

        assertEquals(listOf("a", "b", "z", "x", "y"), res.toList())
    }

    @Test
    fun testCollapse() {
        val sequence = sequenceOf(null, null, 1, 2, 2, 7, 2, 1, 7, null, 1, 9, 9)
        val res = sequence.collapse()

        assertEquals(listOf(null, 1, 2, 7, 2, 1, 7, null, 1, 9), res.toList())
    }

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
