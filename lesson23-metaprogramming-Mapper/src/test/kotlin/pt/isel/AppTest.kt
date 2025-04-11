package pt.isel

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class AppTest {

    @Test
    fun `test mapping PersonDto to Person`() {
        val dto = PersonDto("Ze Manel", 23, "Portugal")
        val expected = Person("Ze Manel", "Portugal")
        val person = loadDynamicMapper(PersonDto::class, Person::class).mapFrom(dto)
        assertEquals(expected.name, person.name)
        assertEquals(expected.country, person.country)
    }

    @Test
    fun `test mapping ArtistSpotify to an Artist with immutable properties`() {
        val dto = ArtistSpotify(State("UK", "English"), "Rock", "David Bowie")
        val expected = Artist("Rock", "David Bowie", Country("UK", "English"))
        val bowie =
            loadDynamicMapper(ArtistSpotify::class, Artist::class)
                .mapFrom(dto)
        assertEquals(expected.kind, bowie.kind)
        assertEquals(expected.name, bowie.name)
        assertEquals(expected.country.name, bowie.country.name)
        assertEquals(expected.country.idiom, bowie.country.idiom)
    }
}
