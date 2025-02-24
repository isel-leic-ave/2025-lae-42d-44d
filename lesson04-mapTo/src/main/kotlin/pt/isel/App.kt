package pt.isel

import kotlin.reflect.KClass

class X {}

interface Y {}

enum class Z

fun receiveType(o: KClass<*>) {
    o.members
}


fun main() {
    receiveType(Z::class)
}