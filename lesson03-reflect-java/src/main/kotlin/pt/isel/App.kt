package pt.isel

import java.lang.reflect.Field
import java.net.URL
import java.time.LocalDate
import java.util.Date


fun main() {
    Date::class.java
        .declaredFields // Declared fields including private
        .forEach { field: Field -> println(field.name + ": " + field.type)}

    Date::class.java
        .methods  // public methods including inherited
        .forEach { println(it.name + "()") }
}

