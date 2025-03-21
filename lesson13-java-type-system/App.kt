fun main() {

    // I() //  error: I is abstract; cannot be instantiated
    val o = object : I {
        override fun x() {
            println("Running method x")
        }
    }

    // invokedynamic => new instance of Function3 with the method main$lambda$0
    // invokestatic bar(Function3)
    bar { nr, msg, flag -> // The lambda code is placed into main$lambda$0
        println("Running my lambda")

    }
}

// in JVM bar is: bar(Function3)
fun bar(other: (Int, String, Boolean) -> Unit) {


}