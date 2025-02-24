package pt.isel

import java.net.URL
import java.time.LocalDate
import java.util.Date
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun main() {
    println("Kotlin properties of Date:")
    Date::class
        .declaredMemberProperties
        .forEach { println("- " + it.name) }

    println("Java fields of Date:")
    Date::class.java
        .declaredFields
        .forEach { println("- " + it.name) }}