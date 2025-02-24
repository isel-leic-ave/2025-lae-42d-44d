package pt.isel

import java.net.URL
import java.time.LocalDate
import java.util.Date
import kotlin.reflect.KCallable
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun main() {
//    Date::class
//        .memberProperties
//        .filter { it.parameters.size == 1 }
//        .forEach { println(it.name + "()") }
    checkYear(LocalDate.now())
}

private val fnGetYear = Date::class
    .members
    .first { it.name == "getYear" }

fun checkYear(obj: Any) {
        fnGetYear
            .call(obj)
            .also { getYearResult -> println("getYear() => $getYearResult") }
}