package pt.isel

class PersonDto(
    val name: String,
    val age: Int,
    @Match(name = "country") val from: String)

/**
 * BASELINE map PersonDto to Person
 */
fun PersonDto.toPerson() : Person {
    return Person(this.name, this.from)
}

fun main() {
    val dto = PersonDto("Ze Manel", 23, "Portugal")
    val expected = Person("Ze Manel", "Portugal")
    lateinit var person:Person
    println("################## Bench Baseline toPerson()")
    jBench {
         person = dto.toPerson()
    }
    println(person)
    println("################## Bench Reflect NaiveMapper")
    jBench {
         person = dto.mapTo(Person::class)
    }
    println(person)
}