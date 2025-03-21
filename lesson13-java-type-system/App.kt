fun main() {
    val objZ = object : Z {
        override fun x() {}

    }

    foo { msg:String ->
        println("Running auxiliary function")
        msg.length
    }
}


fun foo(mapper: (String) -> Int) {

}

fun bar(other: (String, Boolean, Z) -> Int) {

}

fun evenNumbers(numbers: Sequence<Int>): Sequence<Int> {
    return numbers
        .filter { it % 2 == 0 }
}
