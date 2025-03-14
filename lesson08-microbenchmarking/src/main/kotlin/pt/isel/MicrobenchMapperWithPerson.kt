package pt.isel

class PersonDto(
    val name: String,
    val age: Int,
    @Match(name = "country") val from: String)

/*
 * toPerson(dto) baseline
 */
fun PersonDto.toPerson(): Person {
    return Person(
        name,
        from
    )
}

fun main() {
    val dto = PersonDto("Ze Manel", 31, "Portugal")
    lateinit var person: Person
    println("########## Bench Reflect mapFrom() enhanced")
    val mapper = NaiveMapper(PersonDto::class, Person::class)
    jBench {
        person = mapper.mapFrom(dto)
    }
    println(person)
    println("########## Bench Baseline toPerson()")
    jBench {
        person = dto.toPerson()
    }
    println(person)
    println("########## Bench Reflect mapTo()")
    jBench {
        person = dto.mapTo(Person::class)
    }
    println(person)
}