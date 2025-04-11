package pt.isel

class PersonDto(
    val name: String,
    val age: Int,
    @Match(name = "country") val from: String)