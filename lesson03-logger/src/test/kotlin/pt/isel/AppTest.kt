package pt.isel

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
    @Test
    fun `test Logger with an Artist`() {
        val expected = """Artist
            - country = Country
              - idiom = en
              - name = UK
            - kind = Rock
            - name = David Bowie
            """.lines().iterator()
        val bowie = Artist("Rock", "David Bowie", Country("UK", "en"))
        StringBuilder()
            .also {
                it.log(bowie)
                it.toString().lines().forEach { actual ->
                    assertEquals(expected.next().trim(), actual.trim())
                }
            }
    }

    @Test
    fun `test Logger with an Rectangle`() {
        val rect = Rectangle(5, 7)
        System.out.log(rect)
    }

    @Test
    fun `test Logger with an JavaRectangle`() {
        val rect = JavaRectangle(5, 7)
        System.out.log(rect)
    }
}
