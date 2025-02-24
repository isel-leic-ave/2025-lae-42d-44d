package pt.isel

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class AppTest {
    @Test fun `test mapping PersonDto to Person`() {
        val dto = PersonDto("Ze Manel", 23, "Portugal")
        val expected = Person("Ze Manel", "Portugal")
        val person = dto.mapTo(Person::class)
        assertEquals(expected.name, person.name)
        assertEquals(expected.country, person.country)
    }

}
