package pt.isel

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class BenchMapper {
    private val dto = PersonDto("Ze Manel", 31, "Portugal")
    private val mapper = NaiveMapper(PersonDto::class, Person::class)

    @Benchmark
    fun benchBaselineMapperPerson(): Person {
        return dto.toPerson()
    }
    @Benchmark
    fun benchReflectMapperPerson(): Person {
        return mapper.mapFrom(dto)
    }
}

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
