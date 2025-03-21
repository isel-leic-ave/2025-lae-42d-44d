class AK(private val name: String) {
    // class BK { // Nested class without this to the outer class
    inner class BK { // Inner class => with a this$0 to the outer class
        fun print() {
            // println("Outer class AJ name = $name")
            println("Outer class AJ name = ${this@AK.name}")
        }
    }
}

fun main() {
    AK("isel").BK().print()
    AK("super").BK().print()
}
